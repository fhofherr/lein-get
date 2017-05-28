(ns fhofherr.lein-get.fs
  (:refer-clojure :exclude [remove])
  (:import [java.nio.file Files
            Paths
            Path
            LinkOption]
           [java.nio.file.attribute FileAttribute]))

(defn path
  "Convert `p` into a `java.nio.file.Path`.

  Returns `p` if it was an instance of `java.nio.file.Path` already.
  If `ps` is given `p` and `ps` will be joined to form the returned `Path`.

  Arguments:

  * `p`: an instance of `java.nio.file.Path` or a `String`.
  * `ps`: further path components to join."
  [p & ps]
  (if (instance? Path p)
    (reduce (fn [resolved x] (.resolve resolved x))
            p
            ps)
    (Paths/get p (into-array String ps))))

(defn get-cwd
  "Get the current working directory.

  Returns a java.nio.file."
  []
  (.. (path ".")
      (toAbsolutePath)
      (normalize)))

(defn exists?
  "Check if the path exists.

  Returns `true` if `p` exists.

  Arguments:

  * `p`: path to check. May be either a `String` or a `java.nio.file.Path`."
  [p]
  (Files/exists (path p) (make-array LinkOption 0)))

(defn directory?
  "Check if the path points to a directory.

  Returns `true` if `p` points to a directory.

  Arguments:

  * `p`: path to check. May be either a `String` or a `java.nio.file.Path`."
  [p]
  (Files/isDirectory (path p) (make-array LinkOption 0)))

(defn file?
  "Check if the path points to a file.

  Returns `true` if `p` points to a file.

  Arguments:

  * `p`: path to check. May be either a `String` or a `java.nio.file.Path`."
  [p]
  (Files/isRegularFile (path p) (make-array LinkOption 0)))

(defn symlink?
  "Check if the path is a symlink.

  Returns `true` if `p` is a symlink.

  Arguments:

  * `p`: path to check. May be either a `String` or a `java.nio.file.Path`."
  [p]
  (Files/isSymbolicLink p))

(defn symlink
  "Create a symlink from `link-target` to `link`.

  Returns the path to the created symbolic link.

  Arguments:

  * `link-target`: the path the link should point to. May be either a `String`
    or a `java.nio.file.Path`.
  * `link`: the path of the link. May be either a `String` or a
    `java.nio.file.Path`."
  [link-target link]
  (Files/createSymbolicLink (path link)
                            (path link-target)
                            (make-array FileAttribute 0)))

(defn remove
  "Delete the path `p`.

  Arguments:

  * `p`: the path to delete. May be either a `String` or a
    `java.nio.file.Path`."
  [p]
  (Files/delete (path p)))

(defn resolve-path
  "Resolve `relative-path` against `root-path`.

  Returns a normalized but not necessarily absolute instance of
  `java.nio.file.Path`.

  Arguments:

  * `root-path`: the root path to resolve against. Either a `String` or a
    `java.nio.file.Path`.
  * `relative-path`: the relative path to resolve. Either a `String` or a
    `java.nio.file.Path`."
  [root-path relative-path]
  (.. (path root-path relative-path)
      (normalize)))

(defn mkdir-p
  [p])
