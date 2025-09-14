package com.sinaukoding.library.management.service.managementuser;

import com.sinaukoding.library.management.model.app.SimpleMap;
import com.sinaukoding.library.management.model.filter.LibrarianFilterRecord;
import com.sinaukoding.library.management.model.request.LibrarianRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LibrarianService {

    void add(LibrarianRequestRecord request);

    void edit(LibrarianRequestRecord request);

    Page<SimpleMap> findAll(LibrarianFilterRecord filterRequest, Pageable pageable);


    SimpleMap findByUserId(String id);

    void delete(String id);
}
