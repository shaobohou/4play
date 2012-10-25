(ns fourplayserver.session)

(def rows 6)
(def cols 7)

(defonce player-ids (atom 0))
(defonce game-ids (atom {}))
(defonce games (atom {}))
(defonce waiting-player  (atom nil))

(defn first-to-play? [] (if (= (rand-int 2) 1) true false))

(def empty-board {:rows rows :cols cols :state (repeat (* rows cols) 0)})

(defn get-game
  [player-id]
  (get @game-ids player-id))

(defn create-game
  [player1 player2]
  (let [game-id (inc (count @games))]
    (swap! game-ids #(assoc % player1 game-id))
    (swap! game-ids #(assoc % player2 game-id))
    (let [new-game (if (first-to-play) 
                     {:board empty-board :player1 player1 :player2 player2 :whosnext player1}
                     {:board empty-board :player1 player1 :player2 player2 :whosnext player2})]
      (swap! games #(assoc % game-id )))))

(defn add-player
  [player-id]
  (if @waiting-player
    (do 
      (create-game @waiting-player player-id)
      (reset! @waiting-player nil))
    (reset! waiting-player player-id)))

(defn new-game
  "This is horribly un-thread-safe"
  [params]
  (let [player-id @player-ids]
    (swap! players inc)
    (add-player player-id)
    {:player-id player-id}))

(defn poll
  [params])