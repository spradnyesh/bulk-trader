(ns bulk-trader.clj.routes
  (:use compojure.core)
  (:require [bulk-trader.clj.globals :as g]

            [clojure.string :as str]
            [noir.util.route :refer [restricted]]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; views

(defn login []
  nil)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; routes

(defroutes bt-routes
  (GET "/login" [] (login)))
