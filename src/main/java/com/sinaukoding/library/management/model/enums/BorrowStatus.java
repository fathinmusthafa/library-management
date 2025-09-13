package com.sinaukoding.library.management.model.enums;

import lombok.Getter;

@Getter
public enum BorrowStatus {

    REQUESTED("Requested"),
    BORROWED("Borrowed"),
    RETURNED("Returned"),
    OVERDUE("Overdue");

    private final String label;

    BorrowStatus(String label) {
        this.label = label;
    }
}
