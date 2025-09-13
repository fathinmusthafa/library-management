package com.sinaukoding.library.management.model.enums;

import lombok.Getter;

@Getter
public enum Role {

    MEMBER("Member"),
    LIBRARIAN("Librarian"),
    ADMIN("Admin");

    private final String label;

    Role(String label) {
        this.label = label;
    }
}
