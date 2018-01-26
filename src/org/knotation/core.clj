(ns org.knotation.core
  (:require [org.httpkit.server :as server]
            [compojure.route :as route]
            [hiccup.page :as pg]

            [org.knotation.fiddle.components :as comp])

  (:use [compojure.core :only [defroutes GET POST DELETE ANY context]])
  (:gen-class))

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
           [:script {:type "text/javascript" :src "/static/js/fiddle.js"}]
           [:script {:type "text/javascript" :src "/static/js/bootstrap.min.js"}]]
          [:body
           [:div {:class "col-md-6"}
            (comp/tabs
             ["context" {:title "Context"
                         :content
                         [:textarea
                          "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>
@prefix xsd: <http://www.w3.org/2001/XMLSchema#>
@prefix owl: <http://www.w3.org/2002/07/owl#>
@prefix obo: <http://purl.obolibrary.org/obo/>
@prefix knd: <https://knotation.org/datatype/>
@prefix knp: <https://knotation.org/predicate/>
@prefix ex: <https://example.com/>

: rdfs:label
rdfs:label: label

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

: obo:IAO_0000115
label: definition

: obo:IAO_0000118
label: alternative term

: obo:BFO_0000050
label: part of
default datatype: link

: obo:RO_0002162
label: in taxon
default datatype: link

: obo:NCBITaxon_56313
label: Tyto alba

: obo:UBERON_0011796
label: primary remex feather
definition: A remex feather that is connected to the manus

: ex:0000001
label: birth date
default datatype: xsd:date

: ex:0000002
label: length (cm)
default datatype: xsd:real

: ex:0000003
label: coloration"]}

              "content" {:active? true
                         :title "Content"
                         :content [:textarea
                                   ": ex:0000111
label: barn owl primary remex feather
type: owl:Class
definition: A primary remex feather of a barn owl
subclass of: 'primary remex feather' and
 ('in taxon' some 'Tyto alba')
alternative term; @fr: grange hibou primaire remex plume

: ex:0002222
label: barn owl 2222
type: Tyto alba
birth date: 2016-05-04

: ex:0033333
label: sample feather 33333
type: barn owl primary remex feather
part of: barn owl 2222
length (cm): 25.0
coloration: light brown with darker bands"]}])]
           [:div {:class "col-md-6"}
            (comp/tabs
             {:menu-type :dropdown}
             ["help" {:title "Help"
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
