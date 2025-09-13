package com.sinaukoding.library.management.model.enums;

import lombok.Getter;

@Getter
public enum Status {

    AKTIF("Aktif"),
    PENDING("Pending"),
    TIDAK_AKTIF("Tidak Aktif");

    private final String label;

    Status(String label) {
        this.label = label;
    }

}
