(ns bulk-trader.clj.handler
  (:require [bulk-trader.clj.routes :refer [bt-routes]]
            [bulk-trader.clj.middleware :refer [load-middleware]]

            [compojure.core :refer [defroutes]]
            [compojure.route :as route]
            [noir.response :refer [redirect]]
            [noir.util.middleware :refer [app-handler]]))

(defroutes
  app-routes
  (route/resources "/")
  (route/not-found "")) ; todo

(def app (app-handler [bt-routes app-routes]
                      :middleware (load-middleware)
                      :session-options {:timeout (* 60 30)
                                        :timeout-response (redirect "/")}
                      :formats [:json-kw :edn]))
