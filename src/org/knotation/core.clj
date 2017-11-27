(ns org.knotation.core
  (:require [org.httpkit.server :as server]
            [compojure.route :as route]
            [hiccup.page :as pg])

  (:use [compojure.core :only [defroutes GET POST DELETE ANY context]])
  (:gen-class))

(defn fiddle
  [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (pg/html5
          {:lang "en"}
          [:head
           [:title "fiddle.knotation"]
           [:link {:rel "stylesheet" :href "/static/css/bootstrap.min.css" :media "screen"}]
           [:script {:type "text/javascript" :src "/static/js/fiddle.js"}]]
          [:body
           [:div {:class "col-md-6"}
            [:select [:option "Input 1"]]
            [:textarea {:class "before"}
             "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>
@prefix owl: <http://www.w3.org/2002/07/owl#>
@prefix obo: <http://purl.obolibrary.org/obo/>
@prefix knd: <https://knotation.org/datatype/>
@prefix knp: <https://knotation.org/predicate/>
@prefix ex: <https://example.com/>

: rdfs:label
rdfs:label: label
rdfs:comment: Multi
 line
 
  string

: knd:link
label: link

: knd:omn
label: OWL Manchester Syntax

: knp:default-datatype
label: default datatype
default datatype; link: link

: rdf:type
label: type
default datatype: link

: rdfs:subClassOf
label: subclass of
default datatype: OWL Manchester Syntax

: obo:RO_0002162
label: in taxon

: obo:NCBITaxon_56313
label: Tyto alba

: obo:UBERON_0000033
label: head

: ex:owl-head
label: owl head
type: owl:Class
subclass of: head and
 ('in taxon' some 'Tyto alba')"]]
           [:div {:class "col-md-6"}
            [:select
             [:option "Turtle"]
             [:option "NQuads"]]
            [:textarea {:class "after"}]]])})

(defroutes main-routes
  (GET "/" [] fiddle)
  (route/resources "/static/"))

(defn -main
  ([] (-main "8000"))
  ([port]
   (println "Listening on port" port "...")
   (server/run-server main-routes {:port (read-string port)})))
