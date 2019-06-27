(defproject dict "0.1.0-RELEASE"
  :description "dict is a small application that translates german words to english words"
  :url "https://github.com/bakku/dict"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [clj-http "3.10.0"]
                 [hickory "0.7.1"]]
  :main ^:skip-aot dict.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
