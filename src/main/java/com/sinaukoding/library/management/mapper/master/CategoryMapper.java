package com.sinaukoding.library.management.mapper.master;

import com.sinaukoding.library.management.entity.master.Book;
import com.sinaukoding.library.management.entity.master.Category;
import com.sinaukoding.library.management.model.request.CategoryRequestRecord;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public Category requestToEntity(CategoryRequestRecord request) {
        return Category.builder()
                .name(request.name().toUpperCase())
                .description(request.description())
                .build();

    }
}
