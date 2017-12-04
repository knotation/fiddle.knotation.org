(ns org.knotation.fiddle.core
  (:import [goog.async Debouncer])
  (:require [clojure.string :as string]

            [org.knotation.editor.core :as ed]
            [org.knotation.editor.util :as edu]
            [org.knotation.editor.highlight :as high]
            [org.knotation.editor.update :as update]))

(edu/dom-loaded
 (fn []
   (let [kn (ed/editor! ".before" :mode "knotation")
         ttl (ed/editor! ".after" :mode "turtle")]
     (.setOption ttl "readOnly" true)
     (ed/linked [kn ttl]))))
