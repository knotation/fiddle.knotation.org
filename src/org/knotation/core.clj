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
           [:script {:type "text/javascript" :src "/static/js/fiddle.js"}]
           [:style
            ".highlight { background-color: #f4f142; }"]]
          [:body
           [:div {:class "col-md-6"}
            [:textarea {:class "before"}
             "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>
@prefix obo: <http://purl.obolibrary.org/obo/>
@prefix kn: <https://knotation.org/>
@prefix ex: <https://example.com/>

: rdfs:label
rdfs:label: label

: kn:predicate/default-datatype
label: default datatype
default datatype: kn:datatype/datatype

: rdf:type
label: type
default datatype: kn:datatype/link

: obo:NCBITaxon_9615
label: Canis lupus familiaris

: obo:BFO_0000050
label: part of
default datatype: link

: obo:UBERON_0000033
label: head

: ex:laika
label: Laika
label; @ru: Лайка
type: Canis lupus familiaris

: ex:laikas-head
label: Laika's head
type: head
part of: Laika
"]]
           [:div {:class "col-md-6"}
            [:textarea {:class "after"}
             "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>
@prefix obo: <http://purl.obolibrary.org/obo/>
@prefix kn: <https://knotation.org/>
@prefix ex: <https://example.com/>

ex:laika
  rdfs:label \"Laika\" ;
  rdfs:label \"Лайка\"@ru ;
  rdf:type obo:NCBITaxon_9612 .

ex:laikas-head
  rdfs:label \"Laika's head\" ;
  rdf:type obo:UBERON_0000033 ;
  obo:BFO_0000050 ex:laika .
"]]])})

(defroutes main-routes
  (GET "/" [] fiddle)
  (route/resources "/static/"))

(defn -main
  ([] (-main "8000"))
  ([port]
   (println "Listening on port" port "...")
   (server/run-server main-routes {:port (read-string port)})))
