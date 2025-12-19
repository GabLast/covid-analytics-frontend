package com.myorg.config.security;

import com.myorg.encapsulations.User;
import com.myorg.encapsulations.UserSetting;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
@Component
@VaadinSessionScope
public class MyVaadinSession implements Serializable {

    public enum SessionVariables {
        USER("VAADINUSER"), USERSETTINGS("USERSETTINGS");

        private final String name;

        SessionVariables(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    private User        user;
    private UserSetting userSetting;
}
