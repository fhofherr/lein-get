(ns fhofherr.lein-get.test.core-test
  (:require [clojure.test :refer :all]
            [fhofherr.stub-fn.clojure.test :refer [stub-fn]]
            [fhofherr.lein-get.core :as core]))

(deftest find-get-dependency-spec
  (testing "return nil if there is no spec"
    (is (nil? (core/find-get-dependency-spec ['some-dependency "0.10.15"]))))

  (testing "return the spec if found"
    (is (= "../relative/path/on/file/system"
           (core/find-get-dependency-spec
            ['some-dependency
             "0.10.15"
             :get
             "../relative/path/on/file/system"])))
    (is (= {:type :leiningen-checkout
            :path {:scm :file
                   :uri "../relative/path/on/file/system"}}
           (core/find-get-dependency-spec
            ['some-dependency
             "0.10.15"
             :get
             {:type :leiningen-checkout
              :path {:scm :file
                     :uri "../relative/path/on/file/system"}}])))))

(deftest get-dependency
  (testing "do nothing if vector contains no dependency spec"
    (let [get-typed-dependency (stub-fn get-typed-dependency
                                        [project-root dep-spec])]
      (with-redefs [core/get-typed-dependency get-typed-dependency]
        (core/get-dependency "." ['some-dependency "0.10.15"])
        (is (invoked? get-typed-dependency :times 0)))))

  (testing "convert shorthand for local dependency"
    (let [get-typed-dependency (stub-fn get-typed-dependency
                                        [project-root dep-spec])
          dep-vec ['some-dependency
                   "0.10.15"
                   :get
                   "../relative/path/on/file/system"]
          expected-args {'project-root "."
                         'dep-spec {:type :leiningen-checkout
                                    :path {:scm :file
                                           :uri "../relative/path/on/file/system"}}}]
      (with-redefs [core/get-typed-dependency get-typed-dependency]
        (core/get-dependency "." dep-vec)
        (is (invoked? get-typed-dependency :args expected-args)))))

  (testing "pass complete dependency spec verbatim"
    (let [get-typed-dependency (stub-fn get-typed-dependency
                                        [project-root dep-spec])
          dep-vec ['some-dependency
                   "0.10.15"
                   :get
                   {:type :leiningen-checkout
                    :path {:scm :file
                           :uri "../relative/path/on/file/system"}}]
          expected-args {'project-root "."
                         'dep-spec {:type :leiningen-checkout
                                    :path {:scm :file
                                           :uri "../relative/path/on/file/system"}}}]
      (with-redefs [core/get-typed-dependency get-typed-dependency]
        (core/get-dependency "." dep-vec)
        (is (invoked? get-typed-dependency :args expected-args)))))

  (testing "fail if dependency spec is neither string nor map"
    (let [get-typed-dependency (stub-fn get-typed-dependency [dep-spec])
          dep-vec ['some-dependency "0.10.15" :get 12345]]
      (is (thrown? IllegalArgumentException (core/get-dependency "." dep-vec)))
      (is (invoked? get-typed-dependency :times 0)))))
