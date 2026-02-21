package com.rj.business.html;

public record FilterField(String name, String placeholder, String value) {
    public FilterField(String name, String placeholder) {
        this(name, placeholder, "");
    }
}
