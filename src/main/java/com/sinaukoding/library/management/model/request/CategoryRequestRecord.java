package com.sinaukoding.library.management.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CategoryRequestRecord(String id,
                                    @NotBlank(message = "Name tidak boleh kosong") String name,
                                    String description,
                                    List<String> bookId) {
}
