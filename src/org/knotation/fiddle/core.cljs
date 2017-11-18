(ns org.knotation.fiddle.core
  (:import [goog.async Debouncer])
  (:require [clojure.set :as set]
            [clojure.string :as string]

            [org.knotation.api :as api]
            [org.knotation.state :as st]

            [knotation-editor.editor :as ed]))

(defn debounce [f interval]
  (let [dbnc (Debouncer. f interval)]
    ;; We use apply here to support functions of various arities
    (fn [& args] (.apply (.-fire dbnc) dbnc (to-array args)))))

(defn current-line
  [ed]
  (.-line (.getCursor ed)))

(defn highlight-by-subject!
  [editor line]
  (when (ed/knotation-mode? editor)
    (letfn [(handle-of [ln] (.getLineHandle editor ln))
            (subject-of [ln] (:subject @(.-stateAfter (handle-of ln))))
            (blank? [ln] (empty? (.-text (handle-of ln))))]
      (if-let [subject (and (not (blank? line)) (subject-of line))]
        (doseq [i (ed/line-range editor)]
          (when (and (= subject (subject-of i)) (not (blank? i)))
            (ed/highlight-line! editor i "current-subject")))))))

(defn cross->highlight!
  [line-map editor-a editor-b]
  (fn [_]
    (ed/clear-line-highlights! editor-a editor-b)
    (let [ln-from (current-line editor-a)]
      (when-let [ln-to (get line-map ln-from)]
        (ed/highlight-line! editor-a ln-from)
        (ed/highlight-line! editor-b ln-to)
        (ed/scroll-into-view! editor-b :line ln-to)))))

(defn cross<->highlight!
  [line-map editor-a editor-b]
  (.on editor-a "cursorActivity"
       (cross->highlight! @line-map editor-a editor-b))
  (.on editor-b "cursorActivity"
       (cross->highlight! (set/map-invert @line-map) editor-b editor-a)))

(def line-map (atom {}))

(defn compile-content-to
  [source target]
  (let [processed (api/run-operations [(api/kn (.getValue source)) {::api/operation-type :render ::st/format :ttl}])
        result (string/join "\n" (filter identity (map (fn [e] (->> e ::st/output ::st/lines first)) processed)))
        line-pairs (map (fn [e] [(->> e ::st/input ::st/line-number) (->> e ::st/output ::st/line-number)]) processed)]
    (.setValue target result)
    (reset! line-map (into {} line-pairs))))

(.addEventListener
 js/document "DOMContentLoaded"
 (fn []
   (let [editor-a (ed/editor! ".before" :mode "knotation")
         editor-b (ed/editor! ".after" :mode "turtle")]

     (compile-content-to editor-a editor-b)

     ;; (let [opts {::api/operation-type :render ::st/format :ttl}
     ;;       processed (api/run-operations [(api/kn (.getValue editor-a)) opts])
     ;;       result (string/join "\n" (filter identity (map (fn [e] (->> e ::st/output ::st/lines first)) processed)))
     ;;       line-pairs (map (fn [e] [(->> e ::st/input ::st/line-number) (->> e ::st/output ::st/line-number)]) processed)]
     ;;   (.setValue editor-b result)
     ;;   (reset! line-map (into {} (map (fn [e][(->> e ::st/input ::st/line-number) (->> e ::st/output ::st/line-number)]) processed)))
     ;;   ;; (.log js/console "NEW LINE-MAP" (clj->js ))
     ;;   (doseq [p line-pairs]
     ;;     (.log js/console "  " (clj->js p))))

     (.on editor-a "changes"
          (debounce
           (fn [cs]
             (let [ln (current-line editor-a)]
               (compile-content-to editor-a editor-b)
               (ed/highlight-line! editor-b ln)
               (ed/scroll-into-view! editor-b :line ln)))
           500))

     (cross<->highlight! line-map editor-a editor-b)
     (.on editor-a "cursorActivity"
          (fn [ed] (highlight-by-subject! ed (.-line (.getCursor ed))))))))
