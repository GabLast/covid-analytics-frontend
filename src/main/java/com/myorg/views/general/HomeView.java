package com.myorg.views.general;

import com.myorg.config.security.MyVaadinSession;
import com.myorg.encapsulations.User;
import com.myorg.views.MainLayout;
import com.myorg.views.authentication.LoginView;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.security.PermitAll;

@Route(value = "", layout = MainLayout.class)
@PermitAll
public class HomeView extends VerticalLayout implements HasDynamicTitle, BeforeEnterListener{

    public HomeView() {
//        Image imgLogo = new Image(GlobalConstants.LOGO, "LOGO");
        Image imgLogo = new Image("images/acme-logo.jpg", "LOGO");
        imgLogo.setWidth(25, Unit.PERCENTAGE);
        imgLogo.setHeight(25, Unit.PERCENTAGE);

        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        setPadding(false);
        setMargin(false);
        setSpacing(false);
        add(imgLogo);
    }

    @Override
    public String getPageTitle() {
        return "Home";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        User user = (User) VaadinSession.getCurrent().getAttribute(MyVaadinSession.SessionVariables.USER.toString());
        if(user == null || StringUtils.isBlank(user.token())) {
            event.rerouteTo(LoginView.class);
        }
    }
}
