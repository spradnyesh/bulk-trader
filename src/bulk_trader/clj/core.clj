(ns bulk-trader.clj.core
  (:require [bulk-trader.clj.globals :as g]
            [bulk-trader.clj.server :as s]

            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.rotor :as rotor]))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []
  (timbre/set-config!
    [:appenders :rotor]
    {:min-level :info,
     :enabled? true,
     :async? false,
     :max-message-per-msecs nil,
     :fn rotor/appender-fn})
  (timbre/set-config!
    [:shared-appender-config :rotor]
    {:path "clj.log", :max-size (* 512 1024), :backlog 10})
  (timbre/info "clj started successfully"))

(defn -main [& args]
  (init)
  (s/restart-server args)
  (timbre/info "server (re)started on port" g/port))
