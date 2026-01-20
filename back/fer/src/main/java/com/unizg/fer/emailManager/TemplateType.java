package com.unizg.fer.emailManager;

public enum TemplateType {
    STREAK("streak.json"),
    PLAN("plan.json"),
    INACTIVE("inactivity.json");

    private final String fileName;

    TemplateType(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }
}
