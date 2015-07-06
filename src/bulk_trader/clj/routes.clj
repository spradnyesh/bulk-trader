(ns bulk-trader.clj.routes
  (:use compojure.core)
  (:require [bulk-trader.clj.globals :as g]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; views

(defn login []
  nil)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; routes

(defroutes bt-routes
  (GET "/login" [] (login)))
