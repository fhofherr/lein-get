(ns fhofherr.lein-get.test.core-test
  (:require [clojure.test :refer :all]
            [fhofherr.stub-fn.clojure.test :refer [stub-fn]]
            [fhofherr.lein-get
             [core :as core]
             [scm :as scm]
             [fs :as fs]
             [io :as io]]))

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
                                        [project-root dep-name dep-spec])]
      (with-redefs [core/get-typed-dependency get-typed-dependency]
        (core/get-dependency "." ['some-dependency "0.10.15"])
        (is (invoked? get-typed-dependency :times 0)))))

  (testing "convert shorthand for local dependency"
    (let [get-typed-dependency (stub-fn get-typed-dependency
                                        [project-root dep-name dep-spec])
          dep-vec ['some-dependency
                   "0.10.15"
                   :get
                   "../relative/path/on/file/system"]
          expected-args {'project-root "."
                         'dep-name 'some-dependency
                         'dep-spec {:type :leiningen-checkout
                                    :path {:scm :file
                                           :uri "../relative/path/on/file/system"}}}]
      (with-redefs [core/get-typed-dependency get-typed-dependency]
        (core/get-dependency "." dep-vec)
        (is (invoked? get-typed-dependency :args expected-args)))))

  (testing "pass complete dependency spec verbatim"
    (let [get-typed-dependency (stub-fn get-typed-dependency
                                        [project-root dep-name dep-spec])
          dep-vec ['some-dependency
                   "0.10.15"
                   :get
                   {:type :leiningen-checkout
                    :path {:scm :file
                           :uri "../relative/path/on/file/system"}}]
          expected-args {'project-root "."
                         'dep-name 'some-dependency
                         'dep-spec {:type :leiningen-checkout
                                    :path {:scm :file
                                           :uri "../relative/path/on/file/system"}}}]
      (with-redefs [core/get-typed-dependency get-typed-dependency]
        (core/get-dependency "." dep-vec)
        (is (invoked? get-typed-dependency :args expected-args)))))

  (testing "fail if dependency spec is neither string nor map"
    (let [get-typed-dependency (stub-fn get-typed-dependency
                                        [project-root dep-name dep-spec])
          dep-vec ['some-dependency "0.10.15" :get 12345]]
      (is (thrown? IllegalArgumentException (core/get-dependency "." dep-vec)))
      (is (invoked? get-typed-dependency :times 0)))))

(deftest get-leiningen-checkout-dependency
  (let [project-root "."
        dep-vec ['some-dependency
                   "0.10.15"
                   :get
                   {:type :leiningen-checkout
                    :path {:scm :file
                           :uri "../relative/path/on/file/system"}}]
        target-dir (fs/path project-root "checkouts" "some-dependency")]

    (testing "symlink and install dependency"
      (let [checkout (stub-fn checkout [project-root scm-spec target-dir])
            sh (stub-fn sh [working-dir cmd])]
        (with-redefs [scm/checkout checkout
                      io/sh sh]
          (core/get-typed-dependency project-root
                                     (first dep-vec)
                                     (last dep-vec))
          (is (invoked? checkout :args {'project-root project-root
                                        'scm-spec (-> dep-vec
                                                      last
                                                      :path)
                                        'target-dir target-dir}))

          (is (invoked? sh :args {'working-dir target-dir
                                  'cmd "lein install"})))))))
