(defproject org.knotation.fiddle "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
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
