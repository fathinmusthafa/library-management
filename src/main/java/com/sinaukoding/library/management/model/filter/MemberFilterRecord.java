package com.sinaukoding.library.management.model.filter;

import com.sinaukoding.library.management.entity.managementuser.User;

public record MemberFilterRecord(String userId,
                                 String name,
                                 String phone,
                                 String address,
                                 String status,
                                 String username) {
}
