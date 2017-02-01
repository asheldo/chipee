(ns chipee.model
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.tools.reader :refer [read-string]]
            [cljs.js :refer [empty-state eval js-eval]]
            ;[cljs-http.client :as http]
            [goog.net.XhrIo :as xhr]
            [cljs.core.async :refer [<! put! chan]]
                                        ;[chipee.gates :as gates]
            ))

(def st (cljs.js/empty-state))

(defn GET [path]
  (let [c (chan)]
    (xhr/send
     path #(do
             (put! c
                   (-> % .-target .getResponseText))
             "GET"))
    c))

(defn load-cache "blocks on js GET"
  ([cstate s] (load-cache cstate s {}))
  ([cstate s opts]
   (let [ext (or (:ext opts) :cljs)]
     (go
       (let [path
             (str "js/app.out/"
                  (clojure.string/replace (name s) "." "/") "."
                  (name ext) ".cache.edn")
             cache-edn (<! (GET path))
             cache (read-string cache-edn)]
         (cljs.js/load-analysis-cache! cstate s cache)
         )))))


;; path to Transit encoded analysis cache
; ( "/assets/js/cljs/core.cljs.cache.aot.json")

; https://github.com/clojure/clojurescript/wiki/Optional-Self-hosting

(defn eval-str-chan [code c-atom]
  (go
    (reset!
     c-atom           
     (eval
      ;st ;
      (let [val (<! (load-cache st "chipee.gates" {}))]
        st)
      (read-string code)
      {:eval       js-eval
       :source-map true
       :context    :statement}
      (fn [result] result)))))




