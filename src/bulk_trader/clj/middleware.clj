(ns bulk-trader.clj.middleware
  (:require [taoensso.timbre :as timbre]
            [environ.core :refer [env]]
            [noir-exception.core
              :refer [wrap-internal-error wrap-exceptions]]))

(defn log-request [handler]
  (fn [req]
    (timbre/debug req)
    (handler req)))

(def development-middleware
  [log-request
   wrap-exceptions])

(def production-middleware
  [#(wrap-internal-error % :log (fn [e] (timbre/error e)))])

(defn load-middleware []
  (concat (when (env :dev) development-middleware)
          production-middleware))
