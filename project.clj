(defproject org.knotation.fiddle "0.1.0-SNAPSHOT"
  :description "A web-based interactive editor for Knotation"
  :url "https://github.com/knotation/fiddle.knotation.org"
  :license {:name "BSD 3-Clause License"
            :url "https://opensource.org/licenses/BSD-3-Clause"}
  :plugins [[lein-cljsbuild "1.1.6"]]
  :hooks [leiningen.cljsbuild]
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]

                 [http-kit "2.1.18"]
                 [compojure "1.5.1"]
                 [javax.servlet/servlet-api "2.5"]

                 [cheshire "5.6.3"]
                 [hiccup "1.0.5"]
                 [markdown-clj "1.0.2"]

                 [cljsjs/bootstrap-treeview "1.2.0-1"]
                 [org.knotation/knotation-editor "1.0.0-SNAPSHOT"]]

  :cljsbuild {:builds [{:source-paths ["src/org/knotation/fiddle"]
                        :compiler {:output-to "resources/public/js/fiddle.js"
                                   :optimizations :whitespace
                                   :pretty-print true
                                   :language-in :es5}
                        :jar true}]}
  :main org.knotation.core
  :aot [org.knotation.core])
