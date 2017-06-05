(defproject dependent "0.1.0-SNAPSHOT"
  :description "Depends on local-dependency"
  :url "https://github.com/fhofherr/lein-get"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [local-dependency "0.1.0-SNAPSHOT" :get "../local-dependency"]]
  :plugins [[fhofherr/lein-get "0.1.0-SNAPSHOT"]])
