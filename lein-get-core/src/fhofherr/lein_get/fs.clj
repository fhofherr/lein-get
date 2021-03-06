(ns fhofherr.lein-get.fs
  (:refer-clojure :exclude [remove])
  (:import [java.nio.file Files
            Paths
            Path
            LinkOption
            SimpleFileVisitor
            FileVisitResult]
           [java.nio.file.attribute FileAttribute]))

(defn path?
  "Check if `x` is an instance of `java.nio.file.Path`.

  Arguments:

  * `x`: object that may pa a `java.nio.file.Path`"
  [x]
  (instance? Path x))

(defn path
  "Convert `p` into a `java.nio.file.Path`.

  Returns `p` if it was an instance of `java.nio.file.Path` already.
  If `ps` is given `p` and `ps` will be joined to form the returned `Path`.

  Arguments:

  * `p`: an instance of `java.nio.file.Path` or a `String`.
  * `ps`: further path components to join."
  [p & ps]
  {:pre [(or (path? p) (string? p))
         (every? #(or (path? %) (string? %)) ps)]}
  (if (string? p)
    (recur (Paths/get p (make-array String 0)) ps)
    (reduce (fn [resolved x] (.resolve resolved x))
            p
            ps)))

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
  [p & {:keys [recursive] :or {recursive false}}]
  (if (and recursive (directory? p))
    (Files/walkFileTree (path p)
                        (proxy [SimpleFileVisitor] []
                          (visitFile [file _]
                            (Files/delete file)
                            FileVisitResult/CONTINUE)
                          (postVisitDirectory [dir _]
                            (Files/delete dir)
                            (FileVisitResult/CONTINUE))))
    (Files/delete (path p))))

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
  "Create a directory under path `p` and all missing intermediary directories.
  Do nothing if `p` already exists.

  Arguments:

  * `p`: the directory to create. Either a `String` or a
    `java.nio.file.Path`."
  [p]
  (Files/createDirectories (path p) (make-array FileAttribute 0)))
