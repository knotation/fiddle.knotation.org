(ns org.knotation.core
  (:require [clojure.java.io :as io]

            [org.httpkit.server :as server]
            [compojure.route :as route]
            [hiccup.page :as pg]

            [org.knotation.fiddle.components :as comp])

  (:use [compojure.core :only [defroutes GET POST DELETE ANY context]])
  (:gen-class))

(defn get-example-dirs
  []
  (map #(.getName %) (.listFiles (io/file (io/resource "public/example")))))

(defn fiddle
  [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (pg/html5
          {:lang "en"}
          [:head
           [:meta {:charset "utf-8"}]
           [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
           [:title "fiddle.knotation"]
           [:link {:rel "stylesheet" :href "/static/css/bootstrap.min.css" :media "screen"}]
           [:link {:rel "stylesheet" :href "/static/css/knotation.css" :media "screen"}]

           ;; Using https://github.com/simonwhitaker/github-fork-ribbon-css
           [:link {:rel "stylesheet" :href "https://cdnjs.cloudflare.com/ajax/libs/github-fork-ribbon-css/0.2.0/gh-fork-ribbon.min.css"}]
           "<!--[if lt IE 9]>
    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/github-fork-ribbon-css/0.2.0/gh-fork-ribbon.ie.min.css\" />
<![endif]-->"

           [:script {:type "text/javascript" :src "/static/js/fiddle.js"}]
           [:script {:type "text/javascript" :src "/static/js/bootstrap.min.js"}]]
          [:body

           [:nav {:class "navbar navbar-default"}
            [:div {:class "container-fluid"}
             [:div {:class "navbar-header"}
              [:button {:type "button" :class "navbar-toggle collapsed" :data-toggle "collapse" :data-target "#navbar" :aria-expanded "false" :aria-controls "navbar"}
               [:span {:class "sr-only"} "Toggle navigation"]
               [:span {:class "icon-bar"}]
               [:span {:class "icon-bar"}]
               [:span {:class "icon-bar"}]]
              [:a {:href "https://knotation.org" :class "navbar-brand"} "Knotation"]]
             [:div {:id "navbar" :class "navbar-collapse collapse"}
              [:ul {:class "nav navbar-nav"}
               [:li [:a {:href "https://fiddle.knotation.org/"} "Fiddle Beta"]]
               [:li [:a {:href "/"} "New"]]
               [:li [:a {:href "#" :class "dropdown-toggle" :data-toggle "dropdown" :role "button" :aria-haspopup "true" :aria-expanded "false"}
                     "Examples" [:span {:class "caret"}]]
                [:ul {:class "dropdown-menu"}
                 (map
                  (fn [dir-name] [:li [:a {:href (str "#" dir-name)} dir-name]])
                  (get-example-dirs))]]]]]]

           ;; Using https://github.com/simonwhitaker/github-fork-ribbon-css
           [:a {:class "github-fork-ribbon"
                :href "https://github.com/knotation/fiddle.knotation.org"
                :data-ribbon "Fork me on GitHub"
                :title "Fork me on GitHub"}
            "Fork me on Github"]


           [:div {:class "col-md-6"}
            (comp/tabs
             ["context" {:title "Context" :content [:textarea]}
              "content" {:active? true :title "Content" :content [:textarea]}])]
           [:div {:class "col-md-6"}
            (comp/tabs
             {:menu-type :dropdown}
             ["about" {:title "About"
                       :content [:div {:id "about-content" :class "html-content"}]}
              "help" {:title "Help"
                      :content [:div {:id "help-content" :class "html-content"}]}
              "turtle" {:title "Turtle"
                        :active? true
                        :content [:textarea {:id "ttl-editor"}]}
              "rdfa-markup" {:title "HTML"
                             :content [:div {:id "rdfa-content" :class "html-content"}]}
              "rdfa" {:title "RDFa"
                      :content [:textarea {:id "rdfa-editor"}]}
              "tree-markup" {:title "Tree"
                             :content [:div {:id "tree-content" :class "html-content"}]}
              "tree" {:title "JSON"
                      :content [:textarea {:id "tree-editor"}]}
              "dot-markup" {:title "Graph"
                            :content [:div {:id "dot-content" :class "html-content"}]}
              "dot" {:title "DOT"
                     :content [:textarea {:id "dot-editor"}]}
              "nquads" {:title "NQuads"
                        :content [:textarea {:id "nq-editor"}]}])]])})

(defroutes main-routes
  (GET "/" [] fiddle)
  (route/resources "/static/"))

(defn -main
  ([] (-main "8000"))
  ([port]
   (println "Listening on port" port "...")
   (server/run-server main-routes {:port (read-string port)})))
