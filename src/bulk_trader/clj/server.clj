(ns bulk-trader.clj.server
  (:require [bulk-trader.clj.globals :as g]
            [bulk-trader.clj.handler :as h]

            [ring.middleware.reload :as reload]
            [org.httpkit.server :as http-kit]
            [taoensso.timbre :as timbre]))

(defonce args (atom nil))
(defonce server (atom nil))

(defn- dev? [args] (some #{"-dev"} args))

(defn- start-server [new-args]
  (reset! args new-args)
  (reset! server (http-kit/run-server (if (dev? new-args)
                                        (reload/wrap-reload h/app)
                                        h/app)
                                      {:port g/port})))

(defn- stop-server []
  (when-not (nil? @server)
    (@server)))

(defn restart-server [args]
  (stop-server)
  (start-server args))
