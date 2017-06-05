(ns dependent.core-test
  (:require [clojure.test :refer :all]
            [dependent.core :as core]))


(deftest calls-dependency
  (is (= "DEPENDENCY CALLED" (core/call-dependency))))

