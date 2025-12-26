package com.myorg.views.authentication;

import com.myorg.config.AppInfo;
import com.myorg.config.security.AuthenticatedUser;
import com.myorg.config.security.MyVaadinSession;
import com.myorg.dto.request.security.LoginRequest;
import com.myorg.dto.response.configuration.UserSettingResponse;
import com.myorg.dto.response.security.LoginResponse;
import com.myorg.encapsulations.User;
import com.myorg.encapsulations.UserSetting;
import com.myorg.service.CovidAnalyticsService;
import com.myorg.utils.GlobalConstants;
import com.myorg.views.general.HomeView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AnonymousAllowed
@Route(value = "login")
public class LoginView extends Div implements BeforeEnterObserver, HasDynamicTitle {

    private final AuthenticatedUser     authenticatedUser;
    private final CovidAnalyticsService service;

    private final LoginOverlay loginComponent;

    public LoginView(AppInfo appInfo, AuthenticatedUser authenticatedUser,
            CovidAnalyticsService service) {
        this.setId("LoginView");
        this.service = service;
        this.authenticatedUser = authenticatedUser;

        getStyle().set("background-color", "var(--lumo-contrast-5pct)")
                .set("display", "flex").set("justify-content", "center")
                .set("padding", "var(--lumo-space-l)");

        loginComponent = new LoginOverlay();
        loginComponent.setOpened(true);
        loginComponent.setForgotPasswordButtonVisible(false);
        loginComponent.setTitle("Covid Analytics Manager");
        loginComponent.setDescription("");

        Paragraph appVersion = new Paragraph("Version: " + appInfo.getAppVersion());
        appVersion.getStyle().set("text-align", "center");
        loginComponent.getFooter().add(appVersion);

        loginComponent.addLoginListener(loginEvent -> {
            login(loginEvent.getUsername(), loginEvent.getPassword());
        });
//        loginComponent.addForgotPasswordListener()
        add(loginComponent);
    }

    private void login(String username, String password) {

        try {
            LoginResponse response = service.authenticateUser(
                    LoginRequest.builder().usernameMail(username.trim())
                            .password(password).build());

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
            UI.getCurrent().getSession().setAttribute(MyVaadinSession.SessionVariables.USER.toString(), user);


            UserSettingResponse settingResponse = service.getRequestUserSetting();
            UserSetting userSetting;
            if (settingResponse == null || settingResponse.data() == null) {
                userSetting = UserSetting.builder()
                        .id(0L)
                        .darkMode(false)
                        .language("en")
                        .dateFormat("dd/MM/yyyy")
                        .dateTimeFormat("dd/MM/yyyy hh:mm a")
                        .timeZoneString(GlobalConstants.DEFAULT_TIMEZONE)
                        .build();
            } else {
                userSetting = UserSetting.builder()
                        .id(settingResponse.data().id())
                        .darkMode(settingResponse.data().darkMode())
                        .language(settingResponse.data().language())
                        .dateFormat(settingResponse.data().dateFormat())
                        .dateTimeFormat(settingResponse.data().dateTimeFormat())
                        .timeZoneString(settingResponse.data().timeZoneString())
                        .build();
            }

            UI.getCurrent().getSession().setAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString(), userSetting);
            UI.getCurrent().navigate(HomeView.class);

        } catch (Exception e) {
            loginComponent.showErrorMessage("Error", e.getMessage());
            loginComponent.setError(true);
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
