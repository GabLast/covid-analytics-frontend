package com.myorg.views.authentication;

import com.myorg.config.AppInfo;
import com.myorg.config.security.AuthenticatedUser;
import com.myorg.config.security.MyVaadinSession;
import com.myorg.dto.response.dashboard.DashboardOneResponse;
import com.myorg.dto.response.security.LoginResponse;
import com.myorg.dto.request.security.LoginRequest;
import com.myorg.encapsulations.User;
import com.myorg.encapsulations.UserSetting;
import com.myorg.service.CovidAnalyticsService;
import com.myorg.views.general.HomeView;
import com.myorg.views.generics.notifications.ErrorNotification;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@AnonymousAllowed
@Route(value = "login")
public class LoginView extends Div implements BeforeEnterObserver, HasDynamicTitle {

    private final AuthenticatedUser     authenticatedUser;
    private final CovidAnalyticsService service;

    private TextField     tfUsername;
    private PasswordField tfPassword;

    public LoginView(AppInfo appInfo, AuthenticatedUser authenticatedUser,
            CovidAnalyticsService service) {
        this.setId("LoginView");
        this.service = service;
        this.authenticatedUser = authenticatedUser;

        tfUsername = new TextField("Username / E-mail");
        tfUsername.setRequired(true);
        tfUsername.setRequiredIndicatorVisible(true);
        tfUsername.setPlaceholder("Username or E-mail");
        tfUsername.setSizeFull();
        tfUsername.setAutofocus(true);

        tfPassword = new PasswordField("Password");
        tfPassword.setRequired(true);
        tfPassword.setRequiredIndicatorVisible(true);
        tfPassword.setSizeFull();

        Button btnSave = new Button("Sign In");
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnSave.addClickListener(buttonClickEvent -> login());
        btnSave.addClickShortcut(Key.ENTER);

        FormLayout formLayout = new FormLayout();
        formLayout.setMaxHeight("100%");
        formLayout.setMaxWidth("25%");
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 1),
                new FormLayout.ResponsiveStep("900px", 1),
                new FormLayout.ResponsiveStep("1200px", 1));

        formLayout.add(tfUsername);
        formLayout.add(tfPassword);
        formLayout.add(new Div());
        formLayout.add(btnSave);

        VerticalLayout vl = new VerticalLayout();
        vl.setMargin(false);
        vl.setPadding(true);
        vl.setSizeFull();
        vl.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        vl.setAlignItems(FlexComponent.Alignment.CENTER);
        vl.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        vl.getStyle().set("padding-top", "18%");
        vl.getStyle().set("padding-bottom", "18%");

        HorizontalLayout hl = new HorizontalLayout();
        hl.setPadding(false);
        hl.setHeightFull();
        hl.setWidthFull();
        hl.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        setSizeFull();

        hl.add(formLayout);

        Span title = new Span("Covid Analytics Manager");
        title.getStyle().set("font-weight", "bold");

        vl.add(title);
        vl.add(hl);
        vl.add(new Hr());
        vl.add(new Span("Version: " + appInfo.getAppVersion()));
        vl.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.CONTRAST);
        vl.addClassName("my-layout-with-border");

        add(vl);
    }

    private void login() {

        try {
            LoginResponse response = service.authenticateUser(
                    LoginRequest.builder().usernameMail(tfUsername.getValue().trim())
                            .password(tfPassword.getValue()).build());

            if (response == null) {
                throw new RuntimeException("No response from server");
            }

            if (response.data() == null
                    && response.responseInfo() != null) {
                throw new RuntimeException(response.responseInfo().message());
            }

            User user = User.builder()
                    .token(response.data().token())
                    .name(response.data().name())
                    .grantedAuthorities(response.data().grantedAuthorities())
                    .build();

            UserSetting userSetting = UserSetting.builder()
                    .darkMode(false)
                    .language("en")
                    .dateFormat("dd/MM/yyyy")
                    .build();

            UI.getCurrent().getSession().setAttribute(MyVaadinSession.SessionVariables.USER.toString(), user);
            UI.getCurrent().getSession().setAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString(), userSetting);

            UI.getCurrent().navigate(HomeView.class);

        } catch (Exception e) {

            new ErrorNotification(e.getMessage());
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            UI.getCurrent().navigate(HomeView.class);
        }
    }

    @Override
    public String getPageTitle() {
        return "Login";
    }
}
