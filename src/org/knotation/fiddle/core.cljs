(ns org.knotation.fiddle.core
  (:import [goog.async Debouncer])
  (:require [clojure.set :as set]
            [clojure.string :as string]

            [org.knotation.api :as api]
            [org.knotation.state :as st]

            [org.knotation.editor.core :as ed]
            [org.knotation.editor.util :as edu]
            [org.knotation.editor.highlight :as high]))

(def line-map (atom {}))

(defn compile-content-to
  [source target]
  (let [processed (api/run-operations [(api/input :kn (.getValue source)) (api/output :ttl)])
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
          (edu/debounce
           (fn [cs]
             (let [ln (edu/current-line editor-a)]
               (compile-content-to editor-a editor-b)
               (high/highlight-line! editor-b ln)
               (high/scroll-into-view! editor-b :line ln)))
           500))

     (high/cross<->highlight! line-map editor-a editor-b)
     (.on editor-a "cursorActivity"
          (fn [ed] (high/highlight-by-subject! ed (.-line (.getCursor ed))))))))
