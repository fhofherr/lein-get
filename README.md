# lein-get

The `lein-get` plugin aims to obtain dependencies from sources other
than Maven repositories.

## Usage

Put `[fhofherr/lein-get "0.1.0-SNAPSHOT"]` into the `:plugins` vector of
your `project.clj`. To execute the plugin issue the following command:

```bash
$ lein get
```

## Leiningen configuration

`lein-get` inspects your project's dependency vectors to determine
whether it has to perform an action. `lein-get` performs its actions
when the `lein get` command is called.

### Link to checkout dependencies

The most basic functionality of `lein-get` is to link to checkout
dependencies on one of the local file systems. If it encounters
a dependency vector like the following


```clojure
[some-dependency "0.10.15" :get "../relative/path/on/file/system"]
```

it resolves the relative path after the `:get` keyword. `lein-get` uses
the project's root directory, i.e. the directory containing the
`project.clj` file as basis for the resolution of the relative path. It
then executes the following actions:

1.  Check if the resolved path exists and is a directory
2.  Check if the resolved path contains a `project.clj` file
3.  Create a symbolic link to the resolved path in the current project's
    `checkouts` directory. The last component of the resolved path --
    `system` in the above example -- is used as the name of the symbolic
    link.
4.  Change into the symlinked directory and execute `lein install` in
    a new process.

If the path after `:get` is an absolute path `lein-get` skips the path
resolution and performs the listed actions.

The dependency vector

```clojure
[some-dependency "0.10.15" :get {:type :leiningen-checkout
                                 :path {:scm :file
                                        :uri "../relative/path/on/file/system"}}]
```

is the verbose form of the dependency vector shown above. The relative
path referenced by `:uri` is resolved to a full `file://` URI using the
project's root directory.

### Download checkout dependencies from git

In addition to obtaining checkout dependencies from the local file
system checkout dependencies can be directly downloaded from git. To
achieve this specify the following dependency vector:

```clojure
[some-dependency "0.10.15" :get {:type :leiningen-checkout
                                 :path {:scm :git
                                        :uri "https://url/of/git/repo"}}]
```

This makes `lein-get` perform the following actions:

1. Clone the specified git repository into a sub-directory of the
   current project's `referenced-projects` directory. Just as with
   a normal `git clone` the last part of the repository's url will be
   used as the target directory of the clone. If the repository has
   already been cloned by a previous invocation of `lein get` a `git
   pull` is performed instead.
2. If the cloned or updated repository has a tag whose name ends with
   the version specified in the dependency vector this tag is checked
   out. Otherwise the repository's default branch is used.
3. Perform the same steps as in [Link to checkout
   dependencies](#link-to-checkout-dependencies) but use
   `referenced-projects/repo` as the directory of the referenced
   project.

If a different branch or tag is desired it can be specified by adding
`:branch` or `:tag` to the map specifying the path. The `:branch` and
`:tag` keywords are treated in exactly the same way by `lein-get`, thus
a tag could be referenced by using `:branch` and vice versa. Adding
either `:branch` or `:tag` makes `lein-get` checkout the referenced
branch or tag before executing `lein install`.

Example:

```clojure
[some-dependency "0.10.15" :get {:type :leiningen-checkout
                                 :path {:scm :git
                                        :uri "https://url/of/git/repo"
                                        :tag "v0.1.0"}}]
```

### Reference maven/gradle projects from the local file system

### Reference maven/gradle projects from git

### Execute additional set-up commands after obtaining dependencies

## License

Copyright © 2018 Ferdinand Hofherr

Distributed under the MIT License.
