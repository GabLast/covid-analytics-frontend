package com.myorg.utils;

import com.myorg.config.security.AuthenticatedUser;
import com.myorg.service.CovidAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public final class SecurityUtils {

    private final AuthenticatedUser authenticatedUser;
    private final CovidAnalyticsService service;
    private Set<String> permits;

    public Set<String> getPermits() {
        if(permits == null || permits.isEmpty()) {
            permits = service.getPermits().permits();
            log.info("Permit list is empty");
        }

        return this.permits;
    }

    public boolean isAccessGranted(String permit) {
//        final Set<String> list = service.getPermits().permits();

//        return authenticatedUser.get().get().grantedAuthorities().contains(permit);
        return true;
    }

}
