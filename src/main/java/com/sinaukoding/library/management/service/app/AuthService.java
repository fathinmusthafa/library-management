package com.sinaukoding.library.management.service.app;

import com.sinaukoding.library.management.entity.managementuser.User;
import com.sinaukoding.library.management.model.app.SimpleMap;
import com.sinaukoding.library.management.model.request.LoginRequestRecord;

public interface AuthService {

    SimpleMap login (LoginRequestRecord request);

    void logout(User userLoggedIn);
}
