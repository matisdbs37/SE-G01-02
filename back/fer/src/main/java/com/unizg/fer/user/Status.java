package com.unizg.fer.user;

public enum Status {
    ACTIVE("active"),
    BLOCKED("blocked"),
    DELETED("deleted");

    private final String value;

    Status(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
