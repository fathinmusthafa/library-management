package com.sinaukoding.library.management.model.filter;

import java.time.LocalDate;

public record AuthorFilterRecord(String name,
                                 LocalDate birtDate) {
}
