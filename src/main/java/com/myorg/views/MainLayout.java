package com.myorg.views;

import com.myorg.config.AppInfo;
import com.myorg.config.security.AuthenticatedUser;
import com.myorg.config.security.MyVaadinSession;
import com.myorg.encapsulations.User;
import com.myorg.encapsulations.UserSetting;
import com.myorg.service.CovidAnalyticsService;
import com.myorg.utils.PermitConstants;
import com.myorg.utils.SecurityUtils;
import com.myorg.views.authentication.LoginView;
import com.myorg.views.general.AboutView;
import com.myorg.views.general.HomeView;
import com.myorg.views.generics.navigation.MySideNavItem;
import com.myorg.views.general.TabDashboard;
import com.myorg.views.tabs.process.TabCovidHeader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout implements AfterNavigationObserver
        ,BeforeEnterObserver {

    private final AppInfo               appInfo;
    private final AuthenticatedUser     authenticatedUser;
    private final CovidAnalyticsService service;
    private final SecurityUtils         securityUtils;

    private UserSetting settings;

    private H2 viewTitle;

    public MainLayout(AppInfo appInfo, AuthenticatedUser authenticatedUser,
            CovidAnalyticsService service, SecurityUtils securityUtils) {
        this.appInfo = appInfo;
        this.authenticatedUser = authenticatedUser;
        this.service = service;
        this.securityUtils = securityUtils;
        this.settings = (UserSetting) VaadinSession.getCurrent()
                .getAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString());

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {

        Span appName = new Span("Covid Analytics");
        appName.getStyle().set("padding-left", "20px");

        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD,
                LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);
        header.addClickListener(event -> {
            UI.getCurrent().navigate(HomeView.class);
        });

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private Div createNavigation() {
        Div div = new Div();

        SideNav dashboard = new SideNav();
        dashboard.addItem(
                new MySideNavItem("Dashboard", TabDashboard.class, VaadinIcon.DASHBOARD.create(),
                        securityUtils.isAccessGranted(PermitConstants.DASHBOARD)));
        div.add(dashboard);

        //***************************************************************

        SideNav processesModule = new SideNav();
        processesModule.setCollapsible(true);
        processesModule.setExpanded(true);
        processesModule.setLabel("Processes");
        processesModule.setVisible(securityUtils.isAccessGranted(PermitConstants.PROCESSES_MODULE));
        processesModule.addItem(
                new MySideNavItem("Covid Data Load",
                        TabCovidHeader.class, VaadinIcon.FILE.create(),
                        securityUtils.isAccessGranted(PermitConstants.MENU_LOAD_COVID_DATA)));
        div.add(processesModule);

        //***************************************************************

        SideNav securityModule = new SideNav("Security");
        securityModule.setCollapsible(true);
        securityModule.setExpanded(false);
        //        securityModule.setVisible(securityUtils.isAccessGranted("PROCESSES_MODULE"));
        //        securityModule.addItem(
        //                new MySideNavItem("Load Covid Data",
        //                        VerticalLayout.class, VaadinIcon.PAPERCLIP.create(),
        //                        securityUtils.isAccessGranted("PROCESSES_MODULE")));
        div.add(securityModule);

        //***************************************************************

        SideNav aboutNav = new SideNav();
        aboutNav.addItem(
                new MySideNavItem("About", AboutView.class, VaadinIcon.BOOKMARK.create(),
                        true));
        div.add(aboutNav);

        return div;
    }

    private VerticalLayout createFooter() {
        VerticalLayout footer = new VerticalLayout();

        if (authenticatedUser.get().isEmpty()) {
            Anchor loginLink = new Anchor("login", "Login");
            footer.add(loginLink);
            footer.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER,
                    loginLink);
        }

        if (authenticatedUser.get().isPresent()) {
            User user = authenticatedUser.get().get();

            Avatar avatar = new Avatar(user.name());
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar menuHolder = new MenuBar();
            menuHolder.setThemeName("tertiary-inline contrast");

            MenuItem menu = menuHolder.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.name());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            menu.add(div);

//            menu.getSubMenu().addItem("Settings", event -> {
//                new FormUserSetting(service, userSetting -> {
//                    UI.getCurrent().getPage().reload();
//                    try {
//                        if (userSetting != null) {
//                            UI.getCurrent().getSession().setAttribute(
//                                    MyVaadinSession.SessionVariables.USERSETTINGS.toString(),
//                                    userSetting);
//                            UI.getCurrent().getPage().reload();
//                            this.settings = (UserSetting) VaadinSession.getCurrent()
//                                    .getAttribute(
//                                            MyVaadinSession.SessionVariables.USERSETTINGS.toString());
//                        }
//                    } catch (Exception e) {
//                        new ErrorNotification(e.getMessage());
//                    }
//
//                });
//            });

            menu.getSubMenu().addItem("Sign Out", e -> {
                authenticatedUser.logout();
            });
            menu.getSubMenu().getItems().getLast().getStyle().set("color", "#de3b3b");

            footer.add(menuHolder);
            footer.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER,
                    menuHolder);
        }

        return footer;
    }

    private void setTheme(boolean dark) {
        var js = "document.documentElement.setAttribute('theme', $0)";

        getElement().executeJs(js, dark ? Lumo.DARK : Lumo.LIGHT);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        viewTitle.setText(getCurrentPageTitle());
        setTheme(settings.darkMode());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isEmpty()) {
            event.rerouteTo(LoginView.class);
        }
    }

    private String getCurrentPageTitle() {
        String title = "Title";

        Component content = getContent();
        if (content instanceof HasDynamicTitle) {
            title = ((HasDynamicTitle) content).getPageTitle();
        }

        return title;
    }
}
