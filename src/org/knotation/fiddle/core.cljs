(ns org.knotation.fiddle.core
  (:import [goog.async Debouncer])
  (:require [clojure.string :as string]

            [cljsjs.bootstrap-treeview]
            [org.knotation.info :as info]
            [org.knotation.editor.core :as ed]
            [org.knotation.editor.util :as edu]
            [org.knotation.editor.highlight :as high]
            [org.knotation.editor.update :as update]))

(def help-message
  "<div id='content_message' class='hidden'>
  <h3>Knotation is ...</h3>
  <ul>
    <li>a text format that's easy for people and machines to read and write</li>
    <li>a tool for working wth <a href='https://linkeddata.org'>Linked Data</a> and <a href='https://)) en.wikipedia.org/wiki/Ontology_(information_science'>ontologies</a></li>
    <li>a concrete syntax for <a href='https://www.w3.org/RDF/'>RDF</a> and <a href='https://www.w3.) org/OWL/'>OWL</a></li>
    <li>free and open source</li>
    <li><strong>work in progress!</strong></li>
  </ul>
  <h4>Knotation combines the best features of ...</h4>
  <ul>
    <li><a href='https://en.wikipedia.org/wiki/Turtle_(syntax'>Turtle</a>: prefixed names, subject stanzas, multiline strings, comments</li>
    <li><a href='https://json-ld.org'>JSON-LD</a>: labels, default datatypes, contexts</li>
    <li><a href='https://www.w3.org/TR/owl2-manchester-syntax/'>Manchester</a>: human-readable OWL expressions</li>
    <li><a href='https://yaml.org'>YAML</a>: lightweight line-based syntax</li>
  </ul>
  <p>Use the interactive editor to learn more. Click on a line for details.</p>
  <p>Please give us your feedback on our <a href='https://groups.google.com/d/forum/knotation'>mailing list</a> or <a href='https://github.com/knotation/knotation-cljc'>issue tracker</a>.</p>
</div>")

(edu/dom-loaded
 (fn []
   (let [context (ed/editor! "#context textarea" :mode "knotation")
         content (ed/editor! "#content textarea" :mode "knotation")
         ttl (ed/editor! "#ttl-editor" :mode "turtle")
         nq (ed/editor! "#nq-editor" :mode "ntriples")
         rdfa (ed/editor! "#rdfa-editor" :mode "sparql")
         tree (ed/editor! "#tree-editor" :mode "tree.json")
         dot (ed/editor! "#dot-editor" :mode "dot")]
     (.setOption ttl "readOnly" true)
     (.setOption nq "readOnly" true)
     (.setOption rdfa "readOnly" true)
     (.setOption tree "readOnly" true)
     (.setOption dot "readOnly" true)
     (.treeview (js/$ "#tree-content") (clj->js {"data" [] "showBorder" false}))
     (.html (js/$ "#help-content") help-message)
     (.on content "cursorActivity"
          (fn [ed]
            (->> ed
                 .-doc
                 .getCursor
                 .-line
                 (.getCompiledLine (.-knotation ed))
                 info/help
                 info/html
                 (.html (js/$ "#help-content")))))
     (.html (js/$ "#help-content"))
     (.on rdfa "compiled-to"
          (fn [ed content]
            (.html (js/$ "#rdfa-content") content)))
     (.on tree "compiled-to"
          (fn [ed content]
            (.treeview (js/$ "#tree-content") "remove")
            (.treeview (js/$ "#tree-content")
                       (clj->js {"data" (.parse js/JSON content)
                                 "showBorder" false}))
            (.treeview (js/$ "#tree-content") "expandAll")))
     (.on dot "compiled-to"
          (fn [ed content]
            (.html (js/$ "#dot-content") (js/Viz content))))
     (ed/link! context content ttl nq rdfa tree dot)
     (.each (js/$ ".hideAfterRendering") #(.removeClass (js/$ %2) "active")))))
