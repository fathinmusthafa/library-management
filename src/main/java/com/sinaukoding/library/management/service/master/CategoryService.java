package com.sinaukoding.library.management.service.master;

import com.sinaukoding.library.management.model.app.SimpleMap;
import com.sinaukoding.library.management.model.filter.CategoryFilterRecord;
import com.sinaukoding.library.management.model.request.CategoryRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    void add(CategoryRequestRecord request);

    void edit(CategoryRequestRecord request);

    Page<SimpleMap> findAll(CategoryFilterRecord filterRequest, Pageable pageable);

    SimpleMap findById(String id);

    void delete(String id);
}
