package com.sinaukoding.library.management.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BorrowTransactionRequestRecord(@NotBlank(message = "User ID harus diisi")
                                             String userId,
                                             @NotBlank(message = "Book ID harus diisi")
                                             String bookId,
                                             @NotNull(message = "Tanggal peminjaman harus diisi")
                                             LocalDate borrowDate,
                                             @NotNull(message = "Tanggal jatuh tempo harus diisi")
                                             LocalDate dueDate,
                                             String notes) {
}
