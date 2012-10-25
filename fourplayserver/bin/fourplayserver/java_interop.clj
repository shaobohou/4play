(ns fourplayserver.java-interop)

(def rows 6)
(def cols 7)

(def empty-board {:rows rows :cols cols :state (repeat (* rows cols) 0)})

(defn get-board-state
  [board]
  0)

(defn make-move
  [board move-index move-type]
  board)
     