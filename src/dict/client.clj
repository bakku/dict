(ns dict.client
    (:require [clj-http.client :as http])
    (:require [hickory.core :as hickory])
    (:require [hickory.select :as selector])
    (:require [clojure.string :as string]))

(def base-url "https://www.dict.cc/?s=")

(defn get-body
  "Returns the HTML body of the dict.cc translation page. Clients without user agent are not allowed"
  [string]
  (:body (http/get (str base-url string)
                   {:headers {"User-Agent" "Mozilla/5.0 Gecko/20100101"}
                    :insecure? true})))

(defn parsed-body
  "Parses the HTML body of the dict.cc translation page using hickory"
  [expression]
  (-> (get-body expression) hickory/parse hickory/as-hickory))

(defn translation-table
  "Returns only the subset of HTML page of dict.cc which holds the translations as content"
  [expression]
  (get (selector/select (selector/child (selector/tag "table"))
                        (parsed-body expression))
       2))

(defn translation-table-rows
  "Returns all translations as HTML table rows"
  [expression]
  (selector/select (selector/child (selector/tag "tr"))
                   (translation-table expression)))

(defn actual-translation-rows
  "Filters the translation rows to only hold actual translations and no meta information"
  [expression]
  (filter (fn [row] (contains? (:attrs row) :id))
          (translation-table-rows expression)))

(defn row-to-cells
  "Maps one translation row to all of it cells"
  [row]
  (selector/select (selector/child (selector/tag "td")) row))

(defn translation-cell-pairs
  "Maps all translation rows to a pair of cells"
  [expression]
  (map (fn [row]
         (let [cells (row-to-cells row)]
           [(get cells 1) (get cells 2)]))
       (actual-translation-rows expression)))

(defn links-from-cell
  "Gets all the HTML links from a cell since they hold the translation data"
  [cell]
  (selector/select (selector/child (selector/tag "a")) cell))

(defn get-deep-content
  "Retrieves the content from a node recursively"
  [node]
  (if (string? node) node (get-deep-content (first (:content node)))))

(defn reduce-cell-contents
  "Reduces all the parts of a translation cell into one string"
  [cell]
  (string/join " " (map get-deep-content (links-from-cell cell))))

(defn translation-pairs
  "Reduces all translation cell pairs into one english translation and one german translation"
  [expression]
  (map (fn [cell-pair]
         [(reduce-cell-contents (first cell-pair))
          (reduce-cell-contents (get cell-pair 1))])
       (translation-cell-pairs expression)))

(defn search
  "Returns all translation pairs for a given expression"
  [expression]
  (translation-pairs expression))
