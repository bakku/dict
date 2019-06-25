(defproject dict "0.1.0-SNAPSHOT"
  :description "dict is a small application that translates german words to english words"
  :url "https://github.com/bakku/dict"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :main ^:skip-aot dict.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
