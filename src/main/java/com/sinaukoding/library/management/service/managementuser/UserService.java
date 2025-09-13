package com.sinaukoding.library.management.service.managementuser;

import com.sinaukoding.library.management.model.app.SimpleMap;
import com.sinaukoding.library.management.model.filter.UserFilterRecord;
import com.sinaukoding.library.management.model.request.UserRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

public interface UserService {

    void add(UserRequestRecord request);

    void edit(UserRequestRecord request);

    Page<SimpleMap> findAll(UserFilterRecord filterRequest, Pageable pageable);

    SimpleMap findById(String id);

}
