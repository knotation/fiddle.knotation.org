(ns org.knotation.fiddle.core
  (:require [knotation-editor.editor :as ed]))

(.addEventListener
 js/document "DOMContentLoaded"
 (fn []
   (.log js/console "Hello from knotation.fiddle!")

   (ed/editor! ".before" :mode "knotation")
   (ed/editor! ".after" :mode "ntriples")))
