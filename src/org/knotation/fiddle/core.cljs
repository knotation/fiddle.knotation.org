(ns org.knotation.fiddle.core
  (:import [goog.async Debouncer])
  (:require [clojure.string :as string]

            [org.knotation.editor.core :as ed]
            [org.knotation.editor.util :as edu]
            [org.knotation.editor.highlight :as high]
            [org.knotation.editor.update :as update]))

(edu/dom-loaded
 (fn []
   (let [context (ed/editor! "#context textarea" :mode "knotation")
         content (ed/editor! "#content textarea" :mode "knotation")
         ttl (ed/editor! "#ttl-editor" :mode "turtle")
         nq (ed/editor! "#nq-editor" :mode "ntriples")
         rdfa (ed/editor! "#rdfa-editor" :mode "sparql")]
     (.setOption ttl "readOnly" true)
     (ed/linked-editors :env [context] :input content :ttl ttl :nq nq :rdfa rdfa)
     (.each (js/$ ".hideAfterRendering") #(.removeClass (js/$ %2) "active")))))
