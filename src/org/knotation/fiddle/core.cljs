(ns org.knotation.fiddle.core
  (:import [goog.async Debouncer])
  (:require [clojure.string :as string]

            [org.knotation.editor.core :as ed]
            [org.knotation.editor.util :as edu]
            [org.knotation.editor.highlight :as high]
            [org.knotation.editor.update :as update]))

(def line-map (atom {}))

(edu/dom-loaded
 (fn []
   (let [editor-a (ed/editor! ".before" :mode "knotation")
         editor-b (ed/editor! ".after" :mode "turtle")]

     (update/compile-content-to line-map editor-a editor-b)
     (update/cross->update! line-map editor-a editor-b)

     (high/cross<->highlight! line-map editor-a editor-b)
     (high/subject-highlight-on-move! editor-a))))
