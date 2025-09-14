package com.sinaukoding.library.management.model.filter;

import com.sinaukoding.library.management.model.enums.Status;

public record LibrarianFilterRecord(String id,
                                    String userId,
                                    String phone,
                                    String address,
                                    Status status) {
}
