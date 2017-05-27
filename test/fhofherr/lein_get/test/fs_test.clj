(ns fhofherr.lein-get.test.fs-test
  (:require [clojure.test :refer :all]
            [fhofherr.lein-get.fs :as fs])
  (:import [java.nio.file Paths]))


(deftest create-path
  (testing "return a path object verbatim"
    (let [p (Paths/get "." (make-array String 0))
          p' (fs/path p)]
      (is (identical? p p'))))

  (testing "join multiple strings to a path"
    (let [p (fs/path "." "resources" "empty-file")]
      (is (= (str p) "./resources/empty-file"))))

  (testing "join a path and multiple strings"
    (let [p (fs/path ".")
          p' (fs/path p "resources" "empty-file")]
      (is (= (str p') "./resources/empty-file")))))

(deftest identify-directories-and-files
  (let [dir-path (fs/path "." "resources")
        file-path (fs/path "." "resources" "empty-file")]
    (testing "identify directories"
      (is (true? (fs/directory? dir-path)))
      (is (false? (fs/directory? file-path))))

    (testing "identify files"
      (is (true? (fs/file? file-path)))
      (is (false? (fs/file? dir-path))))))

(deftest resolve-path
  (testing "resolve relative paths"
    (let [root-path (fs/get-cwd)
          relative-path "resources/empty-file"
          resolved (fs/resolve-path root-path relative-path)]
      (is (true? (fs/exists? resolved))))))
