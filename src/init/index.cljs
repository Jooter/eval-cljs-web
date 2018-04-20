(ns init.index
  (:refer-clojure :exclude [atom])
  (:require [freactive.core :refer [atom]]
            [freactive.dom :as dom]
            [clojure.string :as s]

            [cljs.tools.reader :refer [read-string]]
            [cljs.js :refer [empty-state eval js-eval]]
            [cljs.env :refer [*compiler*]]
            [cljs.pprint :refer [pprint]]

            [cljs.core.async :refer [put! chan <! >!]])
  (:import [goog.labs.format csv]
           [goog.labs.format.csv ParseError])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]
                   [freactive.macros :refer [rx]]))
                                                   
(defn eval-str [s]
  (eval (empty-state)
        (read-string s)
        {:eval       js-eval
         :source-map true
         :context    :expr}
        (fn [result] result)))

(defn view []
  (let [input (atom "(+ 3 3)")
        output (atom nil)]
      [:div
       [:button {:on-click #(eval-str "(println \"hello world!\")")} "let's compile!"]
       [:button {:on-click #(reset! output (eval-str @input))} "let's compile!"]
       [:p]
       [:h3 "Enter to evalue cljs input"]
       [:input
        {:id "in1"
         :on-keypress (fn [e]
                        (prn :code (.-key e))
                        (reset! input (-> e .-target .-value))
                        (when (= "Enter" (.-key e)) 
                                 (reset! output (eval-str @input))))
         :on-change (fn [e] (prn :ch))
         :value @input :type "text"}]
       [:p] [:h3 (rx (str "input=" @input))]
       [:p] [:h3 (rx (str "output=" @output))]
       [:p] [:h3 (str "debug=" )]
       ]))


(set!
 (.-onload js/window)
 (fn []
   (let [root
         (dom/append-child! (.-body js/document) [:div#root])]

     (aset js/document "title" "Websocket by cljs")

     (dom/mount! root (view last-msg))
     )))
