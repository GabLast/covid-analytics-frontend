package com.myorg.views.generics.navigation;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.sidenav.SideNavItem;

public class MySideNavItem extends SideNavItem {
    public MySideNavItem(String label, Class<? extends Component> view, Component prefixComponent, boolean visible) {
        super(label);
        setPath(view);
        setLabel(label);
        setPrefixComponent(prefixComponent);
        this.setVisible(visible);
    }
}
