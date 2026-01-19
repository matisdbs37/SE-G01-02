package com.unizg.fer.emailManager;

public enum JSONKeys {
    FROM("mon-app-notifications@gmail.com"),
    SUBJECT("subject"),
    IS_HTML("isHtml"),
    BODY("body");

    private final String value;

    JSONKeys(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
