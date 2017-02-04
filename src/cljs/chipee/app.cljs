(ns chipee.app
 ; (:use [chipee.gates])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [chipee.gates :as gates]
   [chipee.model :as model]
   [reagent.core :as reagent :refer [atom]]
   [cljs.core.async :refer [<! put! chan]]
   ))

(defn calc-atom [model c-atom]
  (model/eval-str-chan @model
                       c-atom))

(defn some-component [default model c-atom]
  [:div
   [:h3 [:span {:style {:color "red"}} "I am a "]
    [:strong " modeling component!:"]]
   [:textarea {:defaultValue default :rows 5 :cols 40
               :onChange 
               #(let [value (reset! model (-> % .-target .-value))]
                  (go (<! (calc-atom model c-atom)))
                  )}]])


; can't put (go) in component that would get re-rendered recursively
(defn report-component [c-atom]
  [:div
   [:h4 "And I am dynamic:"]
   [:p.someclass
    [:strong (str @c-atom)]
    ]])

(defn calling-component [ch-model default model c-atom]
  [:div "Parent component"
   [some-component default model c-atom]   
   [report-component c-atom]])

                                        ;";(in-ns 'chipee.gates)\n"
                                        ;goog.require('cljs.core')
                                        ;.require('chipee.gates'); 'chipee.gates)"
                                        ;"(inc 1)" ;              
(def model-str
  (str
;   "(chipee.gates/and* \n 0 1)"
;   "(let [a 1]\n (chipee.gates/nand* 0 1))"
; (let [a 1 b 1 c chipee.app/model-str] (chipee.gates/nand* a b))
   "(let [a 1 b 1]
 (chipee.gates/nand* a b))"
   ))

; can't put (go) in component that would get re-rendered recursively!
(defn init []
  (let [model (atom model-str)
        c-atom (atom "... please wait ...")
        ch-model (chan)
        ch (go (<! (calc-atom model c-atom)))]
    (reagent/render-component
     [calling-component ch-model model-str model c-atom]
     (.getElementById js/document "container"))))
