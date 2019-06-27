(ns dict.core
  (:require [dict.client :as client])
  (:require [clojure.pprint :as pprint])
  (:gen-class))

(defn to-tabular-layout
  [translation-array]
  (map (fn [pair]
         {"english" (first pair) "german" (get pair 1)})
       translation-array))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (if (= (count args) 0)
    (println "Please pass a german word to the program")
    (pprint/print-table (to-tabular-layout (client/search (first args))))))
