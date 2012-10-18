(ns dorothy-example.core
  (:use [dorothy.core]))

(defn save-dependency-tree [raw-sent filename]
  (let [root {:idx 0 :surface "root"}
        sent (vec (cons root (map #(let [word (nth raw-sent %)
                                         idx (Integer/parseInt (nth word 0))
                                         surface (nth word 1)
                                         head (Integer/parseInt (nth word 3))]
                                     {:idx idx
                                      :surface surface
                                      :head head})
                                  (range (count raw-sent)))))
        deps (for [word sent :when (not (zero? (:idx word)))]
               (vector (let [head-word (get sent (:head word))]
                         (str (:surface head-word) "_" (:idx head-word)))
                       (str (:surface word) "_" (:idx word))))
        support-info (map (fn [word]
                            (vector (str (:surface word) "_" (:idx word))
                                    (hash-map :label (:surface word))))
                          sent)]
    (-> (vec (concat deps support-info))
        dot
        (save! filename {:format :pdf}))))

(let [raw-sent  [["1" "The" "DT" "4" "NMOD" "B-sn"]
                 ["2" "luxury" "NN" "4" "NMOD" "B-sn"]
                 ["3" "auto" "NN" "4" "NMOD" "B-NXT"]
                 ["4" "maker" "NN" "7" "SUB" "B-O"]
                 ["5" "last" "JJ" "6" "NMOD" "B-NXT"]
                 ["6" "year" "NN" "7" "VMOD" "B-NXT"]
                 ["7" "sold" "VBD" "0" "ROOT" "B-O"]
                 ["8" "1,214" "CD" "9" "NMOD" "B-NXT"]
                 ["9" "cars" "NNS" "7" "OBJ" "B-O"]
                 ["10" "in" "IN" "7" "VMOD" "B-O"]
                 ["11" "the" "DT" "12" "NMOD" "B-NXT"]
                 ["12" "U.S." "NNP" "10" "PMOD" "B-O"]]]
  (save-dependency-tree raw-sent "example.pdf"))