package com.sinaukoding.library.management.service.master;

import com.sinaukoding.library.management.model.app.SimpleMap;
import com.sinaukoding.library.management.model.filter.BookFilterRecord;
import com.sinaukoding.library.management.model.filter.MemberFilterRecord;
import com.sinaukoding.library.management.model.request.BookRequestRecord;
import com.sinaukoding.library.management.model.request.MemberRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    void add(BookRequestRecord request);

    void edit(BookRequestRecord request);

    Page<SimpleMap> findAll(BookFilterRecord filterRequest, Pageable pageable);

    SimpleMap findById(String id);

    void delete(String id);
}
