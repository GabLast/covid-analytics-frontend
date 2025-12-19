package com.myorg.encapsulations;

import lombok.Builder;

@Builder
public record UserSetting(boolean darkMode, String language, String dateFormat) {
}
