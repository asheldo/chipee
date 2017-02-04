(ns chipee.model
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.tools.reader :refer [read-string]]
            [cljs.js :refer [empty-state eval eval-str js-eval]]
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
       ; (let [val (<! (load-cache st "chipee.gates" {}))] st)
      (cljs.js/empty-state)
      (read-string
       (str code)
      )
      {:eval       js-eval
       :def-emits-var true
       :source-map true
       :context    :statement}
      (fn [result] result)))))

(comment

  :ns (current-ns)
    :context (or (:context user-opts) :expr)
    :source-map false
    :def-emits-var true
    :load (:load-fn! user-opts)
    :eval (make-js-eval-fn user-opts)
    :verbose (or (:verbose user-opts) false)
    :*compiler* (set! env/*compiler* st)
    :static-fns (or (:static-fns user-opts) false)

  )


