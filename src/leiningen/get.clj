(ns leiningen.get
  (:refer-clojure :exclude [get])
  (:require [fhofherr.lein-get.core :as core]))

(defn get
  "Go through the projects dependencies and obtain all `lein-get`
  dependencies."
  [project & args]
  (let [project-root (:root project)
        deps (:dependencies project)]
    (doseq [dependency-vector deps]
      (core/get-dependency project-root dependency-vector))))
