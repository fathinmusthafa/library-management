package com.sinaukoding.library.management.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.List;

public record AuthorRequestRecord(String id,
                                  @NotBlank(message = "Name tidak boleh kosong") String name,
                                  String biography,
                                  String description,
                                  LocalDate birthDate,
                                  List<String> books)  {
}
