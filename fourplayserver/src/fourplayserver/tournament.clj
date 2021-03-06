(ns fourplayserver.tournament
  (:require [cheshire.core :as json]
            [fourplayserver.java-interop :as board]
            [fourplayserver.models.socket-connections :refer [connections]]
            [clojure.math.combinatorics :as combo])
  (:import [net.swiftkey.fourplay GameLoop ServiceStub]))

(def bots (atom []))

(defn start-bot
  [bot bot-name]
  (let [game (GameLoop. (ServiceStub. "localhost" 3000) bot bot-name)]
    (swap! bots conj (future (.play game 1000000)))))

(defn send-socket-message
  [type message]
  (doseq [connection @connections]
    (.send connection (json/generate-string {:type type :message message}))))

(defn games-to-play
  [players]
  (shuffle (map (partial into #{}) (combo/combinations players 2))))

(defonce tournament (ref {:running false :players {} :results {}}))

(defonce game-ids (ref {}))
(defonce games (ref {}))

(defn first-to-play? [] (if (= (rand-int 2) 1) true false))

(defn get-game
  [player-id]
  (get @games (get @game-ids player-id)))

(defn create-game!
  [player1 player2]
  (let [game-id (inc (count @games))]
    (alter game-ids #(assoc % player1 game-id))
    (alter game-ids #(assoc % player2 game-id))
    (let [new-game (if (first-to-play?) 
                     {:id game-id :board board/empty-board :player1 player1 :player2 player2 :whosnext player1}
                     {:id game-id :board board/empty-board :player1 player2 :player2 player1 :whosnext player2})]
      (alter games #(assoc % game-id new-game)))))

(defn log-result-player
  "Returns a player with a result merged in"
  [player result]
  (let [result-map (cond 
                     (= 1 result) {:games-played 1 :games-won 1}
                     (= -1 result) {:games-played 1 :games-lost 1}
                     (= 0 result) {:games-played 1 :games-drawn 1}
                     :else {:game-played 1})]
  (merge-with + (assoc player :state :WAIT) result-map)))

(defn conclude-game!
  "Logs the results of a game of a player and sets them
   to waiting state."
  [player opponent result]
  (alter tournament 
         (fn [t] (assoc t 
                        :players (assoc (:players t) player (log-result-player (get (:players t) player) result))
                        :results (merge-with + (:results t) {[player opponent] result})))))

(defn get-waiting-players
  "Gets waiting players (who are not player-id)"
  [player-id]
  (map first (remove #(= (first %) player-id) (filter #(= :WAIT (:state (second %))) (:players @tournament)))))
        
(defn assign-next-game!
  "Attemps to assign a next game for a player. Does nothing if there are no viable opps"
  [player-id]
  (when (empty? (:games-to-play @tournament))
    (alter tournament assoc :games-to-play (games-to-play (keys (:players @tournament)))))
  (let [games-to-play (:games-to-play @tournament)
        waiting-players (into #{} (map (fn [p] #{player-id p}) (get-waiting-players player-id)))
        next-game (some waiting-players games-to-play)]
    (when-not (nil? next-game)
      (let [opp-id (first (remove #(= % player-id) next-game))]
        (create-game! player-id opp-id)
        (alter tournament
                 (fn [t]
                   (assoc t 
                          :players
                          (merge-with merge (:players t)
                                      {player-id {:state :PLAYING}
                                       opp-id {:state :PLAYING}})
                          :games-to-play
                          (remove #(= % next-game) games-to-play))))))))

(defn send-socket-end-game
  [game result]
  (when-not (:socketed game)
    (send-socket-message "game-end" {:player1 (:player1 game) 
                                     :player2 (:player2 game)
                                     :result result
                                     :board (board/serialize-board (:board game))})
    (send-socket-message "state" (get @tournament :players))
    (alter games #(update-in % [(:id game)] assoc :socketed true))))

(defn poll
  [{id :id}]
  (dosync
    (let [player-id (Integer/parseInt id)]
      (if (true? (:running @tournament))
        (if (= :WAIT (:state (get (:players @tournament) player-id)))
          (do
            (assign-next-game! player-id)
          {:state "WAIT" :board {:rows board/rows :cols board/cols}})
          (let [game (get-game player-id)
                board (:board game)
                board-state (board/get-board-state board)]
            (cond 
              (nil? game)
              (throw (IllegalArgumentException. "Unregistered player id"))
              (= 1 board-state) ; PLAYER1 WIN
              (if (= (:player1 game) player-id)
                (do 
                  (conclude-game! player-id (:player2 game) 1)
                  (send-socket-end-game game board-state)
                  {:state "WON" :board (board/serialize-board board)})
                (do 
                  (conclude-game! player-id (:player1 game) -1)
                  (send-socket-end-game game board-state)
                  {:state "LOST" :board (board/serialize-board board)}))
              (= -1 board-state); PLAYER2 WIN
              (if (= (:player2 game) player-id)
                (do 
                  (conclude-game! player-id (:player1 game) 1)
                  (send-socket-end-game game board-state)
                  {:state "WON" :board (board/serialize-board (board/flip-board board)) })
                (do 
                  (conclude-game! player-id (:player2 game) -1)
                  (send-socket-end-game game board-state)
                  {:state "LOST" :board (board/serialize-board (board/flip-board board)) }))
              (= :DRAW board-state)
              (if (= (:player1 game) player-id) 
                (do
                  (conclude-game! player-id (:player2 game) 0)
                  (send-socket-end-game game 0)
                  {:state "DRAW" :board (board/serialize-board board)})
                (do
                  (conclude-game! player-id (:player1 game) 0)
                  (send-socket-end-game game 0)
                  {:state "DRAW" :board (board/serialize-board (board/flip-board board)) }))
              :else ;in play
              (let [board-inner (if (= (:player2 game) player-id) (board/serialize-board (board/flip-board board)) (board/serialize-board board))]
                (if (= (:whosnext game) player-id)
                  {:state "MOVE" :board board-inner}
                  {:state "WAIT" :board board-inner})))))
        (if (:just-reset @tournament)
          (do (println "DIE") {:state "DIE!!!" :board {:rows board/rows :cols board/cols}})
          {:state "WAIT" :board {:rows board/rows :cols board/cols}})))))
  
(defn update-game!
  [game new-board]
  (let [next-player (if (= (:whosnext game) (:player1 game)) (:player2 game) (:player1 game))]
    (alter games #(assoc % (:id game) (assoc game :board new-board :whosnext next-player)))))

(defn move
  [{id :id move-index :index}]
  (let [player-id (Integer/parseInt id)
        move-col (Integer/parseInt move-index)]
    (dosync
      (if (= :WAIT (:status (get (:players @tournament) player-id)))
        (throw (IllegalArgumentException. "Game hasn't started yet!"))
        (let [game (get-game player-id)
              board (:board game)
              board-state (board/get-board-state board)]
          (if (= :PLAY board-state)
            (if (= (:whosnext game) player-id)
              (let [new-board (if (= (:player1 game) player-id)
                                (board/make-move board move-col)
                                (board/flip-board (board/make-move (board/flip-board board) move-col)))]
                (update-game! game new-board))
              (throw (IllegalArgumentException. "It isn't your move!")))
            (throw (IllegalArgumentException. "Game is already over!"))))))
    (get-game player-id))
  true)

(defn start
  "Starts the tournament if there are 3 or more players"
  [args]
  (if (true? (:running @tournament))
    (throw (IllegalArgumentException. "Tournament has already started!"))
    (if (< (count (:players @tournament)) 2)
      (throw (IllegalArgumentException. "Not enough players"))
      (do 
        (send-socket-message "tournament-started" "")
        (dosync (alter tournament (fn [t] (assoc t 
                                                 :running true
                                                 :games-to-play (games-to-play (keys (:players t)))))))))))

(defn join
  [{name :name}]
  (if (:running @tournament)
    (throw (IllegalArgumentException. "Tournament has already started!"))
    (if name
      (dosync
        (alter tournament dissoc :just-reset)
        (let [player-id (inc (count (get @tournament :players)))]
          (send-socket-message "player-joined" {:id player-id :name name})
          (alter tournament (fn [t] 
                              (assoc t 
                                     :players (assoc (:players t) player-id {:name name :state :WAIT :last-opp nil
                                                                             :games-played 0 :games-won 0 :games-lost 0
                                                                             :games-drawn 0}))))
          {:id player-id}))
      (throw (IllegalArgumentException. "Name required")))))

(defn reset
  "Resets the tournament to its base state"
  [args]
  (dosync
    (ref-set games {})
    (ref-set game-ids {})
    (ref-set tournament {:running false :players {} :results {}
                         :just-reset true}))
  (doseq [bot @bots]
    (future-cancel bot))
  (reset! bots []))

(defn players
  [args]
  (get @tournament :players))

(defn state
  [args]
  {:bots (count @bots)
   :games-to-play (:games-to-play @tournament)
   :results (:results @tournament)
   :players (:players @tournament)
   :running (:running @tournament)})