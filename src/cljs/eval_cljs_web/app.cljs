(ns eval-cljs-web.app
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.tools.reader :refer [read-string]]
            [cljs.js :refer [empty-state eval js-eval]]
            [cljs.env :refer [*compiler*]]
            [cljs.pprint :refer [pprint]]))

(defn eval-str [s]
  (eval (empty-state)
        (read-string s)
        {:eval       js-eval
         :source-map true
         :context    :expr}
        (fn [result] result)))

(defn home-page []
  (let [input (atom nil)
        output (atom nil)]
    (fn []
      [:div
       ;; [:button {:on-click #(eval-str "(println \"hello world!\")")} "let's compile!"]
       ;; [:button {:on-click #(reset! output (eval-str @input))} "let's compile!"]
       ;; [:p]
       [:h3 "Enter to evalue cljs input"]
       [:input
        {:on-key-press (fn [e] (when (= 13 (.-charCode e)) 
                                 (reset! output (eval-str @input))))
         :on-change #(reset! input (-> % .-target .-value))
         :value @input :type "text"}]
       [:p] [:h3 (str "input=" @input)]
       [:p] [:h3 (str "output=" @output)]
       [:p] [:h3 (str "debug=" )]
       ])))

(defn calling-component []
  [:div 
   [home-page]])

(defn init []
  (reagent/render-component [calling-component]
                            (.getElementById js/document "container")))

