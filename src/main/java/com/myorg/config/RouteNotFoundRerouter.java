package com.myorg.config;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.Route;
import jakarta.servlet.http.HttpServletResponse;

@Route("no-route-found-error")
// Optional: you can give it a route, but it works without for error handling
public class RouteNotFoundRerouter extends Div
        implements HasErrorParameter<NotFoundException> {

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
            ErrorParameter<NotFoundException> parameter) {
        // Log the missing path or perform other logic
//        String missingPath = event.getLocation().getPath();
//        System.out.println("User tried to access non-existent path: " + missingPath);

        // Reroute to a specific existing route, e.g., the application root ("")
        // This happens on the server side and triggers a new navigation phase
        event.rerouteTo(""); // Reroutes to the route defined by @Route("")

        // You must return an HTTP status code. Since you are handling the error,
        // you might return 200 (OK) if the reroute is successful or 302 (Found)
        // depending on exact requirements. Vaadin handles the actual response
        // after the reroute.
        // Returning HttpServletResponse.SC_NOT_FOUND (404) is standard for the initial error.
        return HttpServletResponse.SC_NOT_FOUND;
    }
}
