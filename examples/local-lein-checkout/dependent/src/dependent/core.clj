(ns dependent.core
  (:require [dependency :as dep]))

(defn call-dependency
  "I call the dependency"
  []
  (dep/f))
