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
          symlink (stub-fn symlink [link-target link])]
      (with-redefs [fs/exists? exists?
                    fs/symlink symlink]
        (scm/checkout "."
                      {:scm :file :uri "some/src/path"}
                      "some/target/path")
        (is (invoked? symlink :args {'link-target "some/src/path"
                                     'link (fs/resolve-path "."
                                                            "some/target/path")}))))))
