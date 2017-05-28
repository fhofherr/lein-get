(ns fhofherr.lein-get.scm.file
  (:require [fhofherr.lein-get
             [scm :as scm]
             [fs :as fs]])
  (:import [java.nio.file FileAlreadyExistsException]))

(defmethod scm/checkout :file [project-root scm-spec target-dir]
  (let [resolved-target-dir (fs/resolve-path project-root target-dir)]
    (when (fs/exists? resolved-target-dir)
      (throw (FileAlreadyExistsException. (str resolved-target-dir))))
    (fs/mkdir-p (.getParent resolved-target-dir))
    (fs/symlink (:uri scm-spec) resolved-target-dir)))
