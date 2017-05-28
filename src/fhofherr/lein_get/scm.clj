(ns fhofherr.lein-get.scm)

(defmulti checkout
  "Checkout a dependency from source control. If `scm-spec` contains a
  `:branch` or a `:tag` keyword the the checkout will be switched to the
  respective branch or tag.

  Arguments:

  * `project-root`: root directory of the project. May be either a `String` or
    a `java.nio.file.Path`.
  * `scm-spec`: Map containing at least the keywords `:scm` and `:uri`. Defines
    location and type of the remote scm repository. 
  * `target-dir`: destination directory of the checkout. May be either a
    `String` or a `java.nio.file.Path`."
  {:arglists '([project-root scm-spec target-dir])}
  (fn [_ scm-spec _] (:scm scm-spec)))
