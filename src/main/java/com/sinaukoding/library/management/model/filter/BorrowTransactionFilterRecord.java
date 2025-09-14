package com.sinaukoding.library.management.model.filter;

import com.sinaukoding.library.management.model.enums.BorrowStatus;

import java.time.LocalDate;

public record BorrowTransactionFilterRecord(String userId,
                                            String bookId,
                                            BorrowStatus status,
                                            LocalDate borrowDateFrom,
                                            LocalDate borrowDateTo,
                                            LocalDate dueDateFrom,
                                            LocalDate dueDateTo) {
}
