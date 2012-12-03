(ns fourplayserver.java-interop
  (:import [net.swiftkey.fourplay Board]))

(def rows 8)
(def cols 8)
(def n 4)

(def empty-board {:rows rows :cols cols :state (Board. (int-array (vec (repeat (* rows cols) 0))) rows cols)})


(defn serialize-board
  [board]
  (assoc board :state (seq (.serializeBoard (:state board)))))

(defn flip-board
  [board]
  (assoc board :state
         (Board. (int-array (loop [new-board []
                                   remaining (:state (serialize-board board))]
                              (if (empty? remaining)
                                new-board
                                (let [f (first remaining)
                                      f* (* -1 f)]
                                  (recur (vec (conj new-board f*)) (rest remaining))))))
                 rows cols)))

(defn get-board-state
  [board]
  (let [state (:state board)]
    (cond
      (.isDraw state 4)
      :DRAW
      (.hasWon state 1 4)
      1
      (.hasWon state -1 4)
      -1
      :else
      :PLAY)))

(defn make-move
  [board move-index]
  (assoc board :state (.withMove (:state board) move-index)))
