(ns org.knotation.fiddle.core
  (:require [clojure.set :as set]

            [knotation-editor.editor :as ed]))

(defn clear-line-highlights!
  [& editors]
  (doseq [editor editors]
    (doseq [i (range 0 (- (.lineCount editor) 1))]
      (.removeLineClass editor i "background"))))

(defn highlight-line!
  ([editor line] (highlight-line! editor line "highlight"))
  ([editor line class]
   (.addLineClass editor line "background" class)
   (.scrollIntoView editor (clj->js {:line line :ch 0}))))

(defn cross->highlight!
  [line-map editor-a editor-b]
  (fn [_]
    (clear-line-highlights! editor-a editor-b)
    (let [ln-from (.-line (.getCursor editor-a))]
      (when-let [ln-to (get line-map ln-from)]
        (highlight-line! editor-a ln-from)
        (highlight-line! editor-b ln-to)))))

(defn cross<->highlight!
  [line-map editor-a editor-b]
  (.on editor-a "cursorActivity"
       (cross->highlight! line-map editor-a editor-b))
  (.on editor-b "cursorActivity"
       (cross->highlight! (set/map-invert line-map) editor-b editor-a)))

(.addEventListener
 js/document "DOMContentLoaded"
 (fn []
   (let [before (ed/editor! ".before" :mode "knotation")
         after (ed/editor! ".after" :mode "turtle")
         line-map {0 0 1 1 2 2 3 3 4 4
                   27 6 28 7 29 8 30 9
                   32 11 33 12 34 13 35 14}]
     (cross<->highlight! line-map before after))))
