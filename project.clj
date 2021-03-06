(defproject bulk-trader "0.1.0-SNAPSHOT"
  :description "perform bulk trade in indian equities stock markets"
  :url "https://github.com/spradnyesh/bulk-trader"
  :license {:name "Apache License"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.nrepl "0.2.10"]

                 ;; backend
                 [environ "1.0.0"]
                 [http-kit "2.1.18"]
                 [clj-time "0.9.0"]
                 [hiccup "1.0.5"]
                 [lib-noir "0.8.4"]
                 [noir-exception "0.2.2"]
                 [com.cognitect/transit-cljs "0.8.220"]
                 [ring-transit "0.1.3"]
                 [com.taoensso/timbre "3.4.0"]

                 ;; frontend
                 [org.clojure/clojurescript "0.0-3211"]
                 [sablono "0.3.4"]
                 [org.omcljs/om "0.8.8"]
                 [cljs-ajax "0.3.13"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]

  :plugins [[cider/cider-nrepl "0.9.1-SNAPSHOT" :exclusions [org.clojure/tools.nrepl]]
            ;; backend
            [lein-environ "1.0.0"]
            ;; frontend
            [lein-cljsbuild "1.0.5"]
            [lein-figwheel "0.3.3"]]

  :source-paths ["src"]
  :main bulk-trader.clj.core
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/bulk_trader/cljs"]
                        :figwheel { :on-jsload "bulk-trader.cljs.core/on-js-reload" }
                        :compiler {:main bulk-trader.cljs.core
                                   :asset-path "js/compiled/out"
                                   :output-to "resources/public/js/compiled/bulk_trader.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :source-map-timestamp true }}
                       {:id "min"
                        :source-paths ["src"]
                        :compiler {:output-to "resources/public/js/compiled/bulk_trader.js"
                                   :main bulk-trader.cljs.core
                                   :optimizations :advanced
                                   :pretty-print false}}]}

  :figwheel {;; :http-server-root "public" ;; default and assumes "resources"
             ;; :server-port 3449 ;; default
             :css-dirs ["resources/public/css"] ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process
             :nrepl-port 7888

             ;; Server Ring Handler (optional)
             ;; if you want to embed a ring handler into the figwheel http-kit
             ;; server, this is for simple ring servers, if this
             ;; doesn't work for you just run your own server :)
             ;; :ring-handler hello_world.server/handler

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             ;; if you want to disable the REPL
             ;; :repl false

             ;; to configure a different figwheel logfile path
             ;; :server-logfile "tmp/logs/figwheel-logfile.log"
             })
