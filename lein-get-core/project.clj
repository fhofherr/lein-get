(defproject fhofherr/lein-get-core "0.1.0-SNAPSHOT"
  :description "Core library for fhofherr/lein-get"
  :url "https://github.com/fhofherr/lein-get"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :plugins [[lein-codox "0.10.3"]]
  :codox {:namespaces [#"^fhofherr\.lein-get\."]
          :metadata {:doc/format :markdown}}
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[fhofherr/stub-fn "0.1.0-SNAPSHOT"]
                                  [org.clojure/tools.namespace "0.2.11"
                                   :exclusions [org.clojure/clojure]]]}})
