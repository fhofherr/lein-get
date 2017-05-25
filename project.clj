(defproject fhofherr/lein-get "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/fhofherr/lein-get"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :eval-in-leiningen true
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[fhofherr/stub-fn "0.1.0-SNAPSHOT"]
                                  [org.clojure/tools.namespace "0.2.11"
                                   :exclusions [org.clojure/clojure]]]}})
