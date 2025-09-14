package com.sinaukoding.library.management.model.filter;


import java.util.List;

public record BookFilterRecord(String title,
                               String isbn,
                               String publisher,
                               Integer publishedYear,
                               String location,
                               String categoryId) {
}
