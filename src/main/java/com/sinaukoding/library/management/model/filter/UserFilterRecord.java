package com.sinaukoding.library.management.model.filter;

import com.sinaukoding.library.management.model.enums.Role;
import com.sinaukoding.library.management.model.enums.Status;

public record UserFilterRecord (String name,
                               String email,
                               String username,
                               Status status,
                               Role role){
}
