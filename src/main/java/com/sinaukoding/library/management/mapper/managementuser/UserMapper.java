package com.sinaukoding.library.management.mapper.managementuser;

import com.sinaukoding.library.management.entity.managementuser.User;
import com.sinaukoding.library.management.model.enums.Role;
import com.sinaukoding.library.management.model.enums.Status;
import com.sinaukoding.library.management.model.request.UserRequestRecord;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    public User requestToEntity(UserRequestRecord request) {
        return User.builder()
                .name(request.name().toUpperCase())
                .username(request.username().toLowerCase())
                .email(request.email().toLowerCase())
                .status(request.status())
                .role(request.role())
                .build();
    }
}
