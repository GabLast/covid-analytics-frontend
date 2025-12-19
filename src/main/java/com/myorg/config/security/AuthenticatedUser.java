package com.myorg.config.security;

import com.myorg.encapsulations.User;
import com.myorg.views.authentication.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticatedUser {

    public Optional<User> get() {
        User user = (User) VaadinSession.getCurrent().getAttribute(MyVaadinSession.SessionVariables.USER.toString());
        return Optional.ofNullable(user);
    }

    public boolean isUserLoggedIn() {
        return get().isPresent();
    }

    public void logout() {
        try {
            VaadinServletRequest.getCurrent().getHttpServletRequest().logout();

            UI.getCurrent().getSession().setAttribute(MyVaadinSession.SessionVariables.USER.toString(), null);
            UI.getCurrent().getSession().setAttribute(MyVaadinSession.SessionVariables.USERSETTINGS.toString(), null);

            UI.getCurrent().navigate(LoginView.class);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
