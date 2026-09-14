package com.vehicle.backend.security.oauth2;

import java.util.Map;

public class AppleOAuth2UserInfo extends OAuth2UserInfo {

    public AppleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        String firstName = getFirstName();
        String lastName = getLastName();
        if (firstName != null || lastName != null) {
            return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
        }
        return (String) attributes.get("email");
    }

    @Override
    public String getFirstName() {
        if (attributes.containsKey("firstName")) {
            return (String) attributes.get("firstName");
        }
        return "Apple";
    }

    @Override
    public String getLastName() {
        if (attributes.containsKey("lastName")) {
            return (String) attributes.get("lastName");
        }
        return "User";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}
