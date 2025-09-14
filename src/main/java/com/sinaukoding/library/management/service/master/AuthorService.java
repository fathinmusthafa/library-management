package com.sinaukoding.library.management.service.master;

import com.sinaukoding.library.management.model.app.SimpleMap;
import com.sinaukoding.library.management.model.filter.AuthorFilterRecord;
import com.sinaukoding.library.management.model.filter.CategoryFilterRecord;
import com.sinaukoding.library.management.model.request.AuthorRequestRecord;
import com.sinaukoding.library.management.model.request.CategoryRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuthorService {

    void add(AuthorRequestRecord request);

    void addBookToAuthor(String authorId, String bookId);

    void addBooksToAuthor(String authorId, List<String> bookIds);

    void removeBookFromAuthor(String authorId, String bookId);

    void edit(AuthorRequestRecord request);

    Page<SimpleMap> findAll(AuthorFilterRecord filterRequest, Pageable pageable);

    SimpleMap findById(String id);

    void delete(String id);
}
