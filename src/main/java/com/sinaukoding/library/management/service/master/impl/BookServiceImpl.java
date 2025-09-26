package com.sinaukoding.library.management.service.master.impl;

import com.sinaukoding.library.management.builder.CustomBuilder;
import com.sinaukoding.library.management.entity.master.Author;
import com.sinaukoding.library.management.entity.master.Book;
import com.sinaukoding.library.management.entity.master.Category;
import com.sinaukoding.library.management.mapper.master.BookMapper;
import com.sinaukoding.library.management.model.app.AppPage;
import com.sinaukoding.library.management.model.app.SimpleMap;
import com.sinaukoding.library.management.model.filter.BookFilterRecord;
import com.sinaukoding.library.management.model.request.BookRequestRecord;
import com.sinaukoding.library.management.repository.master.AuthorRepository;
import com.sinaukoding.library.management.repository.master.BookRepository;
import com.sinaukoding.library.management.repository.master.CategoryRepository;
import com.sinaukoding.library.management.service.app.ValidatorService;
import com.sinaukoding.library.management.service.master.BookService;
import com.sinaukoding.library.management.util.FilterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ValidatorService validatorService;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;


    @Override
    public void add(BookRequestRecord request) {

        validatorService.validator(request);

        if (bookRepository.existsByIsbn(request.isbn())) {
            log.error("Book already exist");
            throw new RuntimeException("Buku dengan ISBN " + request.isbn() + " sudah terdaftar");
        }

        if (request.totalQuantity() < 0) {
            log.error("Quantity book tidak boleh kurang dari 0");
            throw new RuntimeException("Quantity buku tidak boleh kurang dari 0");
        }

        // Validasi dan ambil category
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> {
                    log.error("Category not found with ID: {}", request.categoryId());
                    return new RuntimeException("Kategori tidak ditemukan");
                });

        // Validasi dan ambil authors
        List<Author> authors = new ArrayList<>();
        if (request.authorId() != null && !request.authorId().isEmpty()) {
            authors = authorRepository.findAllById(request.authorId());
            if (authors.size() != request.authorId().size()) {
                log.error("authors not found");
                throw new RuntimeException("Penulis tidak ditemukan");
            }
        }

        var book = bookMapper.requestToEntity(request);
        book.setCategory(category);
        book.setAuthors(authors);
        // untuk buku baru, available = total qty
        book.setAvailableQuantity(request.totalQuantity());

        bookRepository.save(book);
        log.info("Book berhasil ditambahkan");
        log.trace("Tambah data book berhasil dan selesai");

    }

    @Override
    public void edit(BookRequestRecord request) {

        validatorService.validator(request);

        var bookExisting = bookRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Buku tidak ditemukan"));
        log.info("Edit book dengan ID: {}", request.id());

        if (bookRepository.existsByIsbnAndIdNot(request.isbn(), request.id())) {
            log.error("ISBN already exist");
            throw new RuntimeException("Buku dengan ISBN " + request.isbn() + " sudah terdaftar");
        }

        if (request.totalQuantity() < 0) {
            log.error("Quantity book tidak boleh kurang dari 0");
            throw new RuntimeException("Quantity buku tidak boleh kurang dari 0");
        }

        // Validasi dan ambil category jika berbeda
        if (!bookExisting.getCategory().getId().equals(request.categoryId())) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan"));
            bookExisting.setCategory(category);
        }

        // Validasi dan ambil authors jika berbeda
        Set<String> existingAuthorIds = bookExisting.getAuthors().stream()
                .map(Author::getId)
                .collect(Collectors.toSet());

        Set<String> requestAuthorId = new HashSet<>(request.authorId());

        if (!existingAuthorIds.equals(requestAuthorId)) {
            List<Author> authors = authorRepository.findAllById(request.authorId());
            if (authors.size() != request.authorId().size()) {
                throw new RuntimeException("Satu atau lebih penulis tidak ditemukan");
            }
            bookExisting.setAuthors(authors);
        }

        bookExisting.setIsbn(request.isbn());
        bookExisting.setTitle(request.title());
        bookExisting.setPublisher(request.publisher());
        bookExisting.setPublishedYear(request.publishedYear());

        // Handle perubahan quantity
        if (!bookExisting.getTotalQuantity().equals(request.totalQuantity())) {
            int difference = request.totalQuantity() - bookExisting.getTotalQuantity();
            bookExisting.setTotalQuantity(request.totalQuantity());
            bookExisting.setAvailableQuantity(bookExisting.getAvailableQuantity() + difference);

            if (bookExisting.getAvailableQuantity() < 0) {
                throw new RuntimeException("Available quantity tidak boleh negatif setelah penyesuaian");
            }
        }

        bookExisting.setDescription(request.description());
        bookExisting.setLocation(request.location());

        bookRepository.save(bookExisting);
        log.info("Book berhasil diupdate");

    }

    @Override
    public Page<SimpleMap> findAll(BookFilterRecord filterRequest, Pageable pageable) {
        CustomBuilder<Book> builder = new CustomBuilder<>();

        FilterUtil.builderConditionNotBlankLike("isbn", filterRequest.isbn(), builder);
        FilterUtil.builderConditionNotBlankLike("title", filterRequest.title(), builder);
        FilterUtil.builderConditionNotBlankLike("publisher", filterRequest.publisher(), builder);
        FilterUtil.builderConditionNotNullEqual("publishedYear", filterRequest.publishedYear(), builder);
        FilterUtil.builderConditionNotNullEqual("categoryId", filterRequest.categoryId(), builder);
        FilterUtil.builderConditionNotNullEqual("location", filterRequest.location(), builder);

        Page<Book> books = bookRepository.findAll(builder.build(), pageable);
        List<SimpleMap> listData = books.stream().map(book -> {
            SimpleMap data = new SimpleMap();
            data.put("id", book.getId());
            data.put("isbn", book.getIsbn());
            data.put("title", book.getTitle());
            data.put("publisher", book.getPublisher());
            data.put("publishedYear", book.getPublishedYear());
            data.put("totalQuantity", book.getTotalQuantity());
            data.put("availableQuantity", book.getAvailableQuantity());
            data.put("description", book.getDescription());
            data.put("location", book.getLocation());

            // Data category
            if (book.getCategory() != null) {
                SimpleMap categoryData = new SimpleMap();
                categoryData.put("id", book.getCategory().getId());
                categoryData.put("name", book.getCategory().getName());
                data.put("category", categoryData);
            } else {
                data.put("category", null);
            }

            if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                List<SimpleMap> authorsData = book.getAuthors().stream().map(author -> {
                    SimpleMap authorData = new SimpleMap();
                    authorData.put("id", author.getId());
                    authorData.put("name", author.getName());
                    authorData.put("biography", author.getBiography());
                    authorData.put("birthDate", author.getBirthDate());
                    return authorData;
                }).toList();
                data.put("authors", authorsData);
            } else {
                data.put("authors", Collections.emptyList());
            }

            return data;
        }).toList();

        return AppPage.create(listData, pageable, books.getTotalElements());
    }

    @Override
    public SimpleMap findById(String id) {
        var book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data buku tidak ditemukan"));

        SimpleMap data = new SimpleMap();
        data.put("id", book.getId());
        data.put("isbn", book.getIsbn());
        data.put("title", book.getTitle());
        data.put("publisher", book.getPublisher());
        data.put("publishedYear", book.getPublishedYear());
        data.put("totalQuantity", book.getTotalQuantity());
        data.put("availableQuantity", book.getAvailableQuantity());
        data.put("description", book.getDescription());
        data.put("location", book.getLocation());

        // Data category
        if (book.getCategory() != null) {
            SimpleMap categoryData = new SimpleMap();
            categoryData.put("id", book.getCategory().getId());
            categoryData.put("name", book.getCategory().getName());
            categoryData.put("description", book.getCategory().getDescription());
            data.put("category", categoryData);
        } else {
            data.put("category", null);
        }

        // Data authors
        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            List<SimpleMap> authorsData = book.getAuthors().stream().map(author -> {
                SimpleMap authorData = new SimpleMap();
                authorData.put("id", author.getId());
                authorData.put("name", author.getName());
                authorData.put("biography", author.getBiography());
                authorData.put("birthDate", author.getBirthDate());
                return authorData;
            }).toList();
            data.put("authors", authorsData);
        } else {
            data.put("authors", Collections.emptyList());
        }

        return data;
    }

    @Override
    public void delete(String id) {
        var book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buku tidak ditemukan"));

        if (book.getAvailableQuantity() < book.getTotalQuantity()) {
            throw new RuntimeException("Tidak dapat menghapus buku yang masih dipinjam");
        }

        bookRepository.delete(book);
        log.info("Book berhasil dihapus");
    }
}
