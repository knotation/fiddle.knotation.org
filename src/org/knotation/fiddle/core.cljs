(ns org.knotation.fiddle.core
  (:import [goog.async Debouncer])
  (:require [clojure.string :as string]
            [markdown.core :as md]

            [org.knotation.fiddle.vizlite]

            [cljsjs.bootstrap-treeview]
            ;; [cljsjs.jquery]
            [org.knotation.info :as info]
            [org.knotation.editor.core :as ed]
            [org.knotation.editor.util :as edu]
            [org.knotation.editor.highlight :as high]
            [org.knotation.editor.update :as update]))

(defn setup-tabs! [tab-container]
  (let [dropdown (.find (js/$ tab-container) "select")
        tab-content (.find (js/$ tab-container) ".tab-content")]
    (.change dropdown
             (fn [ev]
               (let [tab-name (.val (js/$ (.-target ev)))]
                 (.removeClass (.find tab-content ".active") "active")
                 (.addClass (.find tab-content (str "#" tab-name)) "active"))))))

(defn get-example!
  [content context help example-name]
  (.get js/$ (str "/example/" example-name)
        (fn [data]
          (try (.setValue context (.-context data))
               (catch js/Error e e))
          (try (.setValue content (.-content data))
               (catch js/Error e e))
          (.html help (md/md->html (.-readme data))))))

(defn page-hash [] (subs (->> js/window .-location .-hash) 1))

(defn load-example-from-page-hash!
  [content context about]
  (let [hash (page-hash)
        ex-name (if (empty? hash) "default" hash)]
    (get-example! content context about ex-name)))

(def firefox? (> (.search (.-userAgent js/navigator) "Firefox") -1))

(defn apply-hacks!
  [refresh-all-editors!]
  ;; CodeMirror doesn't re-render when hidden, so we need to start by showing
  ;; all of them, refreshing then hiding afterwards
  (.removeClass (js/$ ".hideAfterRendering") "active")

  ;; The select-box tabs implementation doesn't work outside of Firefox
  ;; for some reason. In order to be cross-browser, wer need to implement the
  ;; behavior using jQuery
  (when (not firefox?)
    (.each (js/$ ".tab-container")
           (fn [ix el] (setup-tabs! el))))

  ;; CodeMirror doesn't initially render while hidden, so we need to hit any
  ;; newly shown editors with .refresh() otherwise they stay empty until they
  ;; get focus
  (.each
   (js/$ ".nav.nav-tabs li")
   (fn [ix elem]
     (.click (js/$ elem) #(refresh-all-editors!))))

  ;; Dropdowns in general aren't opening for some reason (they do seem to
  ;; close properly, at least). This manually sets up desired behavior with jQuery
  (.each
   (js/$ ".dropdown")
   (fn [ix elem]
     (let [el (js/$ elem)]
       (.click el #(.addClass el "open")))))

  ;; Button dropdowns don't remove the .active class after getting hidden, so
  ;; we need to compensate for them.
  (.each
   (js/$ ".dropdown-menu")
   (fn [ix elem]
     (let [el (js/$ elem)
           label (js/$ (.find (.siblings el ".dropdown-toggle") ".current"))]
       (.click (js/$ (.find el "li"))
               (fn [ev]
                 (refresh-all-editors!)
                 (this-as ths (.html label (.-innerText ths)))
                 (.removeClass (.find el "li.active") "active")))))))

(edu/dom-loaded
 (fn []
   (let [context (ed/editor! "#context textarea" :mode "knotation")
         content (ed/editor! "#content textarea" :mode "knotation")
         help (js/$ "#help-content")
         ttl (ed/editor! "#ttl-editor" :mode "turtle" :read-only true)
         nq (ed/editor! "#nq-editor" :mode "ntriples" :read-only true)
         rdfa (ed/editor! "#rdfa-editor" :mode "sparql" :read-only true)
         tree (ed/editor! "#tree-editor" :mode "tree.json" :read-only true)
         dot (ed/editor! "#dot-editor" :mode "dot" :read-only true)]


     (.treeview (js/$ "#tree-content") (clj->js {"data" [] "showBorder" false}))
     (.on content "cursorActivity"
          (fn [ed]
            (->> ed
                 .-doc
                 .getCursor
                 .-line
                 (.getCompiledLine (.-knotation ed))
                 info/help
                 info/html
                 (.html help))))
     (.html help)
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

     (load-example-from-page-hash! content context (js/$ "#about-content"))
     (.on (js/$ js/window) "hashchange"
          #(load-example-from-page-hash! content context (js/$ "#about-content")))

     (apply-hacks!
      #(js/setTimeout
        (fn []
          (doseq [e [context content ttl nq rdfa tree dot]]
            (.refresh e)))
        100)))))
