package com.sinaukoding.library.management.mapper.master;

import com.sinaukoding.library.management.entity.master.Author;
import com.sinaukoding.library.management.entity.master.Book;
import com.sinaukoding.library.management.entity.master.Category;
import com.sinaukoding.library.management.model.request.BookRequestRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookMapper {

    public Book requestToEntity(BookRequestRecord request) {
        return Book.builder()
                .title(request.title().toUpperCase())
                .isbn(request.isbn())
                .totalQuantity(request.totalQuantity())
                .availableQuantity(request.availableQuantity())
                .publisher(request.publisher())
                .publishedYear(request.publishedYear())
                .description(request.description())
                .build();
    }
}
