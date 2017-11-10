(defproject org.knotation.fiddle "0.1.0-SNAPSHOT"
  :description "A web-based interactive editor for Knotation"
  :url "https://github.com/knotation/fiddle.knotation.org"
  :license {:name "BSD 3-Clause License"
            :url "https://opensource.org/licenses/BSD-3-Clause"}
  :plugins [[lein-cljsbuild "1.1.6"]]
  :hooks [leiningen.cljsbuild]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.521"]
                 [knotation-editor "0.0.13"]]

  :cljsbuild {:builds [{:source-paths ["src/org/knotation/fiddle.cljs"]
                        :compiler {:output-to "resources/fiddle.js"
                                   :optimizations :whitespace
                                   :pretty-print true}
                        :jar true}]})
