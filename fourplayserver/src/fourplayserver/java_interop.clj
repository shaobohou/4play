(ns fourplayserver.java-interop)

(def rows 8)
(def cols 8)
(def n 4)

(def empty-board {:rows rows :cols cols :state (vec (repeat (* rows cols) 0))})

(defn row-of-n?
  "It's so inefficient!"
  [row]
  (first 
    (first 
      (drop-while #(not (or (= (repeat n 1) %) (= (repeat n -1) %))) (partition n 1 row)))))

(defn get-diagonal
  [board start delta]
  (loop [pos start
         diag []]
    (if (or (< pos 0) (> pos (dec (* rows cols)))) 
      diag
      (recur (+ pos delta) (conj diag (get board pos))))))

(defn get-positive-diagonals
  [board]
  (let [bottom-row (into #{} (range cols))
        left-col (map #(* cols %) (range rows))
        start-positions (into bottom-row left-col)]
    (map #(get-diagonal board % (inc cols)) start-positions)))

(defn get-negative-diagonals
  [board]
  (let [bottom-row (into #{} (range cols))
        right-col (map #(+ (dec cols) (* cols %)) (range rows))
        start-positions (into bottom-row right-col)]
    (println start-positions)
    (map #(get-diagonal board % (dec cols)) start-positions)))

(defn get-board-state
  [board]
  (let [rows (partition cols cols (:state board))
        row-result (first (remove nil? (map row-of-n? rows)))]
    (if (nil? row-result) 
      (let [cols (map (fn [i] (map #(get (vec %) i) rows)) (range cols))
            col-result (first (remove nil? (map row-of-n? cols)))]
        (if (nil? col-result) 
          (let [pos-diags (get-positive-diagonals (:state board))
                pd-result (first (remove nil? (map row-of-n? pos-diags)))
                neg-diags (get-negative-diagonals (:state board))
                nd-result (first (remove nil? (map row-of-n? neg-diags)))]
            (if (nil? pd-result) (if (nil? nd-result) 0 nd-result) pd-result)) 
          col-result))
      row-result)))

(defn make-move
  [board move-index move-type]
  (loop [row 0]
    (if (= row rows)
      (throw (IllegalArgumentException. "Bad Move! :-("))
      (if (= 0 (get (:state board) (+ move-index (* cols row))))
        (assoc board :state (assoc (:state board) (+ move-index (* cols row)) move-type))
        (recur (inc row))))))
