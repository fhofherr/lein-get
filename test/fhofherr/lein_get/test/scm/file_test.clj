(ns fhofherr.lein-get.test.scm.file-test
  (:require [clojure.test :refer :all]
            [fhofherr.stub-fn.clojure.test :refer [stub-fn]]
            [fhofherr.lein-get
             [scm :as scm]
             [fs :as fs]]
            [fhofherr.lein-get.scm.file])
  (:import [java.nio.file FileAlreadyExistsException]))

(deftest checkout-file-dependencies
  (testing "fail if target directory exists"
    (let [exists? (stub-fn exists? [p] true)]
      (with-redefs [fs/exists? exists?]
        (is (thrown? FileAlreadyExistsException
                     (scm/checkout "."
                                   {:scm :file :uri "some/src/path"}
                                   "some/target/path"))))))

  (testing "link source directory to target directory"
    (let [exists? (stub-fn exists? [p] false)
          mkdir-p (stub-fn mkdir-p [p])
          symlink (stub-fn symlink [link-target link])
          link (fs/resolve-path "." "some/target/path")]
      (with-redefs [fs/exists? exists?
                    fs/mkdir-p mkdir-p
                    fs/symlink symlink]
        (scm/checkout "."
                      {:scm :file :uri "some/src/path"}
                      "some/target/path")
        (is (invoked? mkdir-p :args {'p (.getParent link)}))
        (is (invoked? symlink :args {'link-target "some/src/path"
                                     'link link}))))))
