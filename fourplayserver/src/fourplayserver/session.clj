(ns fourplayserver.session
  (:require [fourplayserver.java-interop :as board]))

(defonce player-ids (ref 0))
(defonce game-ids (ref {}))
(defonce games (ref {}))
(defonce waiting-player  (ref nil))

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

(defn add-player!
  [player-id]
  (if @waiting-player
    (do 
      (create-game! @waiting-player player-id)
      (ref-set waiting-player nil))
    (ref-set waiting-player player-id)))

(defn new-game
  [params]
  (dosync
    (let [player-id @player-ids]
      (alter player-ids inc)
      (add-player! player-id)
      {:id player-id})))

(defn poll
  [{id :id}]
  (let [player-id (Integer/parseInt id)]
    (if (= @waiting-player player-id)
      {:state "WAIT" :board {:rows board/rows :cols board/cols}}
      (let [game (get-game player-id)
            board (:board game)
            board-state (board/get-board-state board)]
        (cond 
          (nil? game)
          (throw (IllegalArgumentException. "Unregistered player id"))
          (= 1 board-state)
          (if (= (:player1 game) player-id) {:state "WON" :board board} {:state "LOST" :board board})
          (= -1 board-state)
          (if (= (:player2 game) player-id) {:state "WON" :board board} {:state "LOST" :board board})
          :else ;in play
          (if (= (:whosnext game) player-id)
            {:state "MOVE" :board board}
            {:state "WAIT" :board board}))))))

(defn update-game!
  [game new-board]
  (let [next-player (if (= (:whosnext game) (:player1 game)) (:player2 game) (:player1 game))]
    (alter games #(assoc % (:id game) (assoc game :board new-board :whosnext next-player)))))

(defn move
  [{id :id move-index :index}]
  (let [player-id (Integer/parseInt id)
        move-col (Integer/parseInt move-index)]
    (dosync
      (if (= @waiting-player player-id)
        (throw (IllegalArgumentException. "Game hasn't started yet!"))
        (let [game (get-game player-id)
              board (:board game)
              board-state (board/get-board-state board)]
          (if (= 0 board-state)
            (if (= (:whosnext game) player-id)
              (let [move-type (if (= (:player1 game) player-id) 1 -1)
                    new-board (board/make-move board move-col move-type)]
                (update-game! game new-board))
              (throw (IllegalArgumentException. "It isn't your move!")))
        (throw (IllegalArgumentException. "Game is already over!"))))))
  (get-game player-id)))
        