(ns bulk-trader.clj.routes
  (:use compojure.core)
  (:import (java.io ByteArrayOutputStream))
  (:require [bulk-trader.clj.globals :as g]
            [bulk-trader.clj.traders.geojit :as geojit]

            ;; [taoensso.timbre :as timbre]
            [cognitect.transit :as t]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

;;;;;;;;;;;;;;;;;;;;;;;;;
;; https://github.com/JulianBirch/cljs-ajax/blob/master/dev/user.clj

(defn write-transit [x]
  (let [baos (ByteArrayOutputStream.)
        w (t/writer baos :json)
        _ (t/write w x)
        ret (.toString baos)]
    (.reset baos)
    ret))

(defn transit-response [response]
  {:status 200
   :headers {"Content-Type" "application/transit+json; charset=utf-8"}
   :body (write-transit response)})
;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; views

(defn login [req]
  (let [params (:params req)
        trader (:trader params)
        rslt ((resolve (symbol (str "bulk-trader.clj.traders." trader "/login"))) params)]
    (transit-response rslt)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; routes

(defroutes bt-routes
  (POST "/login" [] login))
