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
   (ed/linked
    [(ed/editor! ".before" :mode "knotation")
     (ed/editor! ".after" :mode "turtle")])))
