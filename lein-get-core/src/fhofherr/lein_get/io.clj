(ns fhofherr.lein-get.io
  (:require [clojure.string :refer [split]]
            [clojure.java.shell :as shell])
  (:import [java.nio.file Path]))

(defn sh
  "Execute a command using `leiningen.core.eval/sh`

  Arguments:

  * `working-dir`: working directory to execute the process in. May be either
    a `String` or a `java.nio.file.Path`.
  * `cmd`: the command to execute. May be either a `String` or a sequential
    collection of `String`s."
  [working-dir cmd]
  {:pre [(or (string? working-dir)
             (instance? Path working-dir))
         (or (string? cmd)
             (and (sequential? cmd) (every? string? cmd)))]}
  (let [cmdv (if (string? cmd)
               (split cmd #"\s+")
               cmd)]
    (shell/with-sh-dir (str working-dir)
      (apply shell/sh cmdv))))
