package com.sinaukoding.library.management.service.managementuser;

import com.sinaukoding.library.management.model.app.SimpleMap;
import com.sinaukoding.library.management.model.filter.MemberFilterRecord;
import com.sinaukoding.library.management.model.filter.UserFilterRecord;
import com.sinaukoding.library.management.model.request.MemberRequestRecord;
import com.sinaukoding.library.management.model.request.UserRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {

    void add(MemberRequestRecord request);

    void edit(MemberRequestRecord request);

    Page<SimpleMap> findAll(MemberFilterRecord filterRequest, Pageable pageable);

    SimpleMap findById(String id);

    void delete(String id);
}
