(ns org.knotation.fiddle.components)

(defn tabs
  ([tabs-map] (tabs {} tabs-map))
  ([{:keys [menu-type] :or {menu-type :tabs}} tabs-map]
   (let [tabs-map (partition 2 tabs-map)]
     [:span
      (case menu-type
        :tabs [:ul {:class "nav nav-tabs" :role "tablist"}
               (map
                (fn [[name tab]]
                  [:li {:role "presentation" :class (when (:active? tab) "active")}
                   [:a {:role "tab" :data-toggle "tab"
                        :href (str "#" name) :aria-controls name}
                    (:title tab)]])
                tabs-map)]
        :dropdown [:select {:class "form-control" :role "tablist"}
                   (map
                    (fn [[name tab]]
                      [:option (let [opts {:role "tab" :data-toggle "tab"
                                           :href (str "#" name) :aria-controls name :class (when (:active? tab) "active")}]
                                 (if (:active? tab) (assoc opts :selected "selected") opts))
                       (:title tab)])
                    tabs-map)])
      [:div {:class "tab-content"}
       (map
        (fn [[name tab]]
          [:div {:role "tabpanel" :class (str "tab-pane active" (if (:active? tab) "" " hideAfterRendering")) :id name}
           (:content tab)])
        tabs-map)]])))
