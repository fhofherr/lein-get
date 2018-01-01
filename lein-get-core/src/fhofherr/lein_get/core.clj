(ns fhofherr.lein-get.core
  (:require [fhofherr.lein-get
             [fs :as fs]
             [scm :as scm]
             [io :as io]]
            [fhofherr.lein-get.scm.file]))

(defn find-get-dependency-spec
  "Find the dependency spec in the passed `dependency-vector`. This function
  looks for the keyword `:get` and returns the value immediately following
  it.

  Return the dependency spec if there was one, or `nil` if `dependency-vector`
  did not contain a dependency spec.

  Arguments:

  * `dependency-vector`: a dependency vector which may contain a dependency
    spec."
  [dependency-vector]
  (->> dependency-vector
       (drop-while #(not= % :get))
       second))

(defmulti get-typed-dependency
  "Get a project dependency based on the `dependency-spec`'s type.

  Arguments:

  * `project-root`: path of the project's root directory.
  * `dependency-name`: name of the dependency, i.e. the first item in the
    dependency vector.
  * `dependency-spec`: map specifying how to get the dependency."
  {:arglists '([project-root dependency-name dependency-spec])}
  (fn [_ _ dependency-spec] (:type dependency-spec)))

(defmethod get-typed-dependency
  :leiningen-checkout
  [project-root dependency-name dependency-spec]
  (let [target-dir (as-> dependency-name $
                     (name $)
                     (.replace $ "/" "-")
                     (fs/path project-root "checkouts" $))]
    (when-not (fs/exists? target-dir)
      (scm/checkout project-root (:path dependency-spec) target-dir)
      ;; TODO log stdout of invoked command
      (io/sh target-dir "lein install"))))

(defn get-dependency
  "Obtain the dependency specified by `dependency-vector` if it contains
  a dependency spec. Do nothing if `dependency-vector` does not contain
  a dependency spec.

  Calls [[get-typed-dependency]] with the `project-root` and the found
  dependency spec.

  Arguments:

  * `project-root`: path of the project's root directory.
  * `dependency-vector`: vector specifiying a dependency."
  [project-root dependency-vector]
  (when-let [dependency-spec (find-get-dependency-spec dependency-vector)]
    (cond
      (string? dependency-spec) (get-typed-dependency
                                 project-root
                                 (first dependency-vector)
                                 {:type :leiningen-checkout
                                  :path {:scm :file
                                         :uri dependency-spec}})
      (map? dependency-spec) (get-typed-dependency project-root
                                                   (first dependency-vector)
                                                   dependency-spec)
      :else (throw (IllegalArgumentException.
                    "Depencency spec must be either string or map.")))))
