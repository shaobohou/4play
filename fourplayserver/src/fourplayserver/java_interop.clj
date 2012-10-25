(ns fourplayserver.java-interop)

(def rows 6)
(def cols 7)

(def empty-board {:rows rows :cols cols :state (vec (repeat (* rows cols) 0))})

(defn get-board-state
  [board]
  0)

(defn make-move
  [board move-index move-type]
  (loop [row 0]
    (if (= 0 (get (:state board) (+ move-index (* cols row))))
      (assoc board :state (assoc (:state board) (+ move-index (* cols row)) move-type))
      (recur (inc row)))))
     