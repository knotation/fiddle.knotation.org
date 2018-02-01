(ns org.knotation.fiddle.components)

(defn tabs
  ([tabs-map] (tabs {} tabs-map))
  ([{:keys [menu-type] :or {menu-type :tabs}} tabs-map]
   (let [tabs-map (partition 2 tabs-map)]
     [:span {:class "tab-container"}
      (case menu-type
        :tabs [:ul {:class "nav nav-tabs" :role "tablist"}
               (map
                (fn [[name tab]]
                  [:li {:role "presentation" :class (when (:active? tab) "active")}
                   [:a {:role "tab" :data-toggle "tab"
                        :href (str "#" name) :aria-controls name}
                    (:title tab)]])
                tabs-map)]
        :dropdown (let [id (name (gensym "dropdown"))]
                    [:div {:class "dropdown"}
                     [:button {:class "btn btn-default dropdown-toggle"
                               :type "button" :id id :data-toggle "dropdown"
                               :aria-haspopup true :aria-expanded true}
                      [:span {:class "current"} "Select a View"]
                      [:span {:class "caret"}]]
                     [:ul {:class "dropdown-menu" :aria-labelledby id}
                      (map
                       (fn [[name tab]]
                         [:li [:a {:role "tab" :data-toggle "tab" :href (str "#" name) :aria-controls name}
                               (:title tab)]])
                       tabs-map)]]))
      [:div {:class "tab-content"}
       (map
        (fn [[name tab]]
          [:div {:role "tabpanel" :class (str "tab-pane active" (if (:active? tab) "" " hideAfterRendering")) :id name}
           (:content tab)])
        tabs-map)]])))
