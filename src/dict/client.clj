(ns dict.client
    (:require [clj-http.client :as http])
    (:require [hickory.core :as hickory])
    (:require [hickory.select :as selector])
    (:require [clojure.string :as string]))

(def base-url "https://www.dict.cc/?s=")

(defn get-body
  [string]
  (:body (http/get (str base-url string)
                   {:headers {"User-Agent" "Mozilla/5.0 Gecko/20100101"}
                    :insecure? true})))

(defn parsed-body
  [expression]
  (-> (get-body expression) hickory/parse hickory/as-hickory))

(defn translation-table
  [expression]
  (get (selector/select (selector/child (selector/tag "table"))
                        (parsed-body expression))
       2))

(defn translation-table-rows
  [expression]
  (selector/select (selector/child (selector/tag "tr"))
                   (translation-table expression)))

(defn actual-translation-rows
  [expression]
  (filter (fn [row] (contains? (:attrs row) :id))
          (translation-table-rows expression)))

(defn row-to-cells
  [row]
  (selector/select (selector/child (selector/tag "td")) row))

(defn translation-cell-pairs
  [expression]
  (map (fn [row]
         (let [cells (row-to-cells row)]
           [(get cells 1) (get cells 2)]))
       (actual-translation-rows expression)))

(defn links-from-cell
  [cell]
  (selector/select (selector/child (selector/tag "a")) cell))

(defn get-deep-content
  [node]
  (if (string? node) node (get-deep-content (first (:content node)))))

(defn reduce-cell-contents
  [cell]
  (string/join " " (map get-deep-content (links-from-cell cell))))

(defn translation-pairs
  [expression]
  (map (fn [cell-pair]
         [(reduce-cell-contents (first cell-pair))
          (reduce-cell-contents (get cell-pair 1))])
       (translation-cell-pairs expression)))

(defn search
  [expression]
  (translation-pairs expression))
