(ns bulk-trader.clj.traders.geojit
  (:import [java.net URI URLEncoder])
  (:require [bulk-trader.clj.globals :as g]

            [clojure.string :as str]
            [org.httpkit.client :as http]
            [cognitect.transit :as t]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; definitions

(defonce login-url "https://flip.geojitbnpparibas.com/faces/lite/auth/login.jsp")
(defonce auth-url  "https://flip.geojitbnpparibas.com/faces/lite/auth/FlipSecureCode.jsp")
(defonce logout-url "https://flip.geojitbnpparibas.com/faces/lite/common/logout.jsp")
(defonce home-url "https://flip.geojitbnpparibas.com/faces/lite/auth/../../lite/common/home.jsp")

;; todo: remove
(def r1 (atom nil))
(def r2 (atom nil))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; utils

(defn url-encode [s]
  (URLEncoder/encode (str s) "utf8"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn build-headers [sessionid referer]
  {"Content-Type" "application/x-www-form-urlencoded"
   "Cookie" (str "JSESSIONID=" sessionid)
   "Host" "flip.geojitbnpparibas.com"
   "Origin" "https://flip.geojitbnpparibas.com"
   "Referer" referer
   "User-Agent" "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36"})

(defn get-sessionid []
  (let [rslt (http/post login-url
                        {:headers {"Content-Type" "application/x-www-form-urlencoded"}})]
    (if (= 200 (:status @rslt))
      (let [sessionid (try (-> @rslt
                               :headers
                               :set-cookie
                               (str/split #"; ")
                               first
                               (str/split #"=")
                               last)
                           (catch NullPointerException npe nil))]
        (if sessionid
          [true sessionid]
          [nil "NullPointerException"]))
      [nil "fail: sessionid"])))

(defn do-login [sessionid username passwd]
  (http/post (str login-url ";jsessionid=" sessionid)
             {:headers (build-headers sessionid login-url)
              :form-params {"loginform" "loginform"
                            (url-encode "loginform:userText") username
                            (url-encode "loginform:PassText") passwd
                            (url-encode "loginform:source") "lite"
                            (url-encode "loginform:reqType") 0
                            (url-encode "loginform:button1") "Login"
                            (url-encode "javax.faces.ViewState") (url-encode "j_id1:j_id2")}}))

(defn do-auth [sessionid username passwd pan]
  (http/post auth-url
             {:headers (build-headers sessionid (str login-url ";jsessionid=" sessionid))
              :form-params {"flipsecure" "flipsecure"
                            (url-encode "flipsecure:secureGroup") 2
                            (url-encode "flipsecure:TfaText") pan
                            (url-encode "flipsecure:j_id_id193") ""
                            (url-encode "flipsecure:usercode") username
                            (url-encode "flipsecure:sessionkey") sessionid
                            (url-encode "flipsecure:source") "lite"
                            (url-encode "flipsecure:pass") passwd
                            (url-encode "flipsecure:button1") "Login"
                            (url-encode "javax.faces.ViewState") (url-encode "j_id1:j_id3")}}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; main

(defn login [{:keys [username passwd pan] :as params}]
  (let [ue-username (url-encode username)
        ue-passwd (url-encode passwd)
        ue-pan (url-encode pan)
        [status sessionid] (get-sessionid)]
    (if status
      (let [rslt-1 (do-login sessionid ue-username ue-passwd)]
        (reset! r1 @rslt-1) ; todo: remove
        (if (= 200 (:status @rslt-1))
          (let [rslt-2 (do-auth sessionid ue-username ue-passwd ue-pan)]
            (reset! r2 @rslt-2) ; todo: remove
            (if (and (= 302 (:status @rslt-2))
                     (= home-url (:location @rslt-2)))
              {:logged-in? true :error nil :sessionid sessionid}
              {:logged-in? nil :error (:error @rslt-2) :msg "2"}))
          {:logged-in? nil :error (:error @rslt-1) :msg "1"}))
      {:logged-in? nil :error sessionid :msg "0"})))

(defn logout [{:keys [sessionid] :as params}]
  (http/get logout-url
            {:headers (build-headers sessionid home-url)}))
