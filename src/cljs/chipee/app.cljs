(ns chipee.app
 ; (:use [chipee.gates])
  (:require
   [reagent.core :as reagent :refer [atom]]
   [chipee.gates :as g]))

(defn some-component []
  [:div
   [:h3 "I am a component!"]
   [:p.someclass
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red"]
    " text."
   (str (g/not* 1))]])

(defn calling-component []
  [:div "Parent component"
   [some-component]])

(defn init []
  (reagent/render-component [calling-component]
                            (.getElementById js/document "container")))
