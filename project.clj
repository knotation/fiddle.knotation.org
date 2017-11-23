(defproject org.knotation.fiddle "0.1.0-SNAPSHOT"
  :description "A web-based interactive editor for Knotation"
  :url "https://github.com/knotation/fiddle.knotation.org"
  :license {:name "BSD 3-Clause License"
            :url "https://opensource.org/licenses/BSD-3-Clause"}
  :plugins [[lein-cljsbuild "1.1.6"]]
  :hooks [leiningen.cljsbuild]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.521"]

                 [http-kit "2.1.18"]
                 [compojure "1.5.1"]
                 [javax.servlet/servlet-api "2.5"]

                 [cheshire "5.6.3"]
                 [hiccup "1.0.5"]

                 [org.knotation/knotation-editor "0.0.15-SNAPSHOT"]]

  :cljsbuild {:builds [{:source-paths ["src/org/knotation/fiddle"]
                        :compiler {:output-to "resources/public/js/fiddle.js"
                                   :optimizations :whitespace
                                   :pretty-print true
                                   :language-in :es5}
                        :jar true}]}
  :main org.knotation.core
  :aot [org.knotation.core])
