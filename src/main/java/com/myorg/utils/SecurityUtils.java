package com.myorg.utils;

import com.myorg.config.security.AuthenticatedUser;
import com.myorg.dto.response.security.PermitResponse;
import com.myorg.service.CovidAnalyticsService;
import com.myorg.views.generics.notifications.ErrorNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public final class SecurityUtils {

    private final AuthenticatedUser authenticatedUser;
    private final CovidAnalyticsService service;

    public SecurityUtils(AuthenticatedUser authenticatedUser,
            CovidAnalyticsService service, Set<String> permits) {
        this.authenticatedUser = authenticatedUser;
        this.service = service;
    }

    public Set<String> getPermits() {
        try {
            PermitResponse response = service.getPermits();

            if (response == null) {
                throw new RuntimeException("No response from server");
            }

            if (response.data() == null && response.responseInfo() != null) {
                throw new RuntimeException(response.responseInfo().message());
            }
            return response.data().permits();
        } catch (Exception e) {
            new ErrorNotification(e.getMessage());
            return null;
        }
    }

    public boolean isAccessGranted(String permit) {
        if(authenticatedUser.get().isPresent()) {
            return authenticatedUser.get().get().grantedAuthorities().contains(permit);
        } else {
            return false;
        }
    }
}
