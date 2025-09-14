package com.sinaukoding.library.management.service.master.impl;

import com.sinaukoding.library.management.builder.CustomBuilder;
import com.sinaukoding.library.management.entity.master.Category;
import com.sinaukoding.library.management.mapper.master.CategoryMapper;
import com.sinaukoding.library.management.model.app.AppPage;
import com.sinaukoding.library.management.model.app.SimpleMap;
import com.sinaukoding.library.management.model.filter.CategoryFilterRecord;
import com.sinaukoding.library.management.model.request.CategoryRequestRecord;
import com.sinaukoding.library.management.repository.master.BookRepository;
import com.sinaukoding.library.management.repository.master.CategoryRepository;
import com.sinaukoding.library.management.service.app.ValidatorService;
import com.sinaukoding.library.management.service.master.CategoryService;
import com.sinaukoding.library.management.util.FilterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final ValidatorService validatorService;
    private final CategoryMapper categoryMapper;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public void add(CategoryRequestRecord request) {

        validatorService.validator(request);

        if (categoryRepository.existsByName(request.name())) {
            log.error("Category already exist");
            throw new RuntimeException("Kategori dengan nama " + request.name() + " sudah terdaftar");
        }

        var category = categoryMapper.requestToEntity(request);
        categoryRepository.save(category);
        log.info("Category berhasil ditambahkan");
        log.trace("Tambah data category berhasil dan selesai");

    }

    @Override
    public void edit(CategoryRequestRecord request) {

        validatorService.validator(request);

        var categoryExisting = categoryRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan"));
        log.info("Edit category dengan ID: {}", request.id());

        if (categoryRepository.existsByNameAndIdNot(request.name(), request.id())) {
            throw new RuntimeException("Kategori dengan nama " + request.name() + " sudah terdaftar");
        }

        categoryExisting.setName(request.name());
        categoryExisting.setDescription(request.description());
        categoryRepository.save(categoryExisting);
        log.info("Category berhasil diupdate");
    }

    @Override
    public Page<SimpleMap> findAll(CategoryFilterRecord filterRequest, Pageable pageable) {
        CustomBuilder<Category> builder = new CustomBuilder<>();

        FilterUtil.builderConditionNotBlankLike("name", filterRequest.name(), builder);

        Page<Category> categories = categoryRepository.findAll(builder.build(), pageable);
        List<SimpleMap> listData = categories.stream().map(category -> {
            SimpleMap data = new SimpleMap();
            data.put("id", category.getId());
            data.put("name", category.getName());
            data.put("description", category.getDescription());
            if (category.getBooks() != null) {
                List<SimpleMap> booksData = category.getBooks().stream().map(book -> {
                    SimpleMap bookData = new SimpleMap();
                    bookData.put("id", book.getId());
                    bookData.put("title", book.getTitle());
                    bookData.put("isbn", book.getIsbn());
                    bookData.put("publisher", book.getPublisher());
                    bookData.put("publishedYear", book.getPublishedYear());
                    return bookData;
                }).toList();
                data.put("books", booksData);
            } else {
                data.put("books", Collections.emptyList());
            }
            return data;
        }).toList();

        return AppPage.create(listData, pageable, categories.getTotalElements());
    }

    @Override
    public SimpleMap findById(String id) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data kategori tidak ditemukan"));

        SimpleMap data = new SimpleMap();
        data.put("id", category.getId());
        data.put("name", category.getName());
        data.put("description", category.getDescription());

        if (category.getBooks() != null) {
            List<SimpleMap> booksData = category.getBooks().stream().map(book -> {
                SimpleMap bookData = new SimpleMap();
                bookData.put("id", book.getId());
                bookData.put("title", book.getTitle());
                bookData.put("isbn", book.getIsbn());
                return bookData;
            }).toList();
            data.put("books", booksData);
        } else {
            data.put("books", Collections.emptyList());
        }

        return data;
    }

    @Override
    public void delete(String id) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan"));

        if (bookRepository.countByCategoryId(id) > 0) {
            throw new RuntimeException("Tidak dapat menghapus kategori yang masih memiliki buku");
        }

        categoryRepository.delete(category);
        log.info("Category berhasil dihapus");
    }
}
