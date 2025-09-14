package com.sinaukoding.library.management;

import com.sinaukoding.library.management.model.enums.Role;
import com.sinaukoding.library.management.model.enums.Status;
import com.sinaukoding.library.management.model.request.UserRequestRecord;
import com.sinaukoding.library.management.service.managementuser.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void addUserTest() {
        UserRequestRecord request = new UserRequestRecord(null,
                "Test User",
                "testuser",
                "1234",
                "test@gmail.com",
                Role.MEMBER,
                Status.AKTIF
        );
        userService.add(request);
    }
}
