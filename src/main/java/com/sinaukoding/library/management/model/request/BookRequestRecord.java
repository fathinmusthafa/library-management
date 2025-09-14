package com.sinaukoding.library.management.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

public record BookRequestRecord(String id,
                                @NotBlank(message = "Isbn tidak boleh kosong") String isbn,
                                @NotBlank(message = "Title tidak boleh kosong") String title,
                                @NotBlank(message = "Publisher tidak boleh kosong") String publisher,
                                @NotNull(message = "PublisherYear tidak boleh kosong") Integer publishedYear,
                                @NotNull(message = "Total Qty tidak boleh kosong") Integer totalQuantity,
                                @NotNull(message = "Avail Qty tidak boleh kosong") Integer availableQuantity,
                                String description,
                                String location,
                                @NotBlank(message = "Category Id tidak boleh kosong") String categoryId,
                                @NotEmpty(message = "Author Id tidak boleh kosong") List<String> authorId) {
}
