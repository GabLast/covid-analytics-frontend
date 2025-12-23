package com.myorg.views.general;

import com.myorg.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import jakarta.annotation.security.PermitAll;

@Route(value = "about", layout = MainLayout.class)
@PermitAll
public class AboutView extends VerticalLayout implements HasDynamicTitle {

    public AboutView() {
        setSpacing(false);

        Image img = new Image("images/acme-logo.jpg", "placeholder");
//        Image img = new Image(GlobalConstants.LOGO, "placeholder");
        img.setWidth("200px");
        add(img);

        H2 header = new H2("Covid Analytics Management");
        header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
        add(header);
        add(new Paragraph("Developed By" + ":"));
        add(new Paragraph("Gabriel José Marte Lantigua\t"));

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    @Override
    public String getPageTitle() {
        return "About Us";
    }
}
