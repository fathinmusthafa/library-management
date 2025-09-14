package com.sinaukoding.library.management.service.master.impl;

import com.sinaukoding.library.management.builder.CustomBuilder;
import com.sinaukoding.library.management.entity.master.Author;
import com.sinaukoding.library.management.entity.master.Book;
import com.sinaukoding.library.management.mapper.master.AuthorMapper;
import com.sinaukoding.library.management.model.app.AppPage;
import com.sinaukoding.library.management.model.app.SimpleMap;
import com.sinaukoding.library.management.model.filter.AuthorFilterRecord;
import com.sinaukoding.library.management.model.request.AuthorRequestRecord;
import com.sinaukoding.library.management.repository.master.AuthorRepository;
import com.sinaukoding.library.management.repository.master.BookRepository;
import com.sinaukoding.library.management.service.app.ValidatorService;
import com.sinaukoding.library.management.service.master.AuthorService;
import com.sinaukoding.library.management.util.FilterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorServiceImpl implements AuthorService {

    private final ValidatorService validatorService;
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final BookRepository bookRepository;

    @Override
    public void add(AuthorRequestRecord request) {

        validatorService.validator(request);

        if (authorRepository.existsByName(request.name())) {
            log.error("Author already exist");
            throw new RuntimeException("Penulis dengan nama " + request.name() + " sudah terdaftar");
        }

        var author = authorMapper.requestToEntity(request);
        authorRepository.save(author);
        log.info("Author berhasil ditambahkan");
        log.trace("Tambah data author berhasil dan selesai");
    }

    @Override
    public void addBookToAuthor(String authorId, String bookId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Penulis tidak ditemukan"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Buku tidak ditemukan"));

        if (author.getBooks() != null && author.getBooks().contains(book)) {
            throw new RuntimeException("Buku sudah ada dalam daftar penulis");
        }

        if (author.getBooks() == null) {
            author.setBooks(new ArrayList<>());
        }
        author.getBooks().add(book);

        if (book.getAuthors() == null) {
            book.setAuthors(new ArrayList<>());
        }
        book.getAuthors().add(author);

        authorRepository.save(author);
        bookRepository.save(book);

        log.info("Buku {} berhasil ditambahkan ke penulis {}", book.getTitle(), author.getName());
    }

    @Override
    public void addBooksToAuthor(String authorId, List<String> bookIds) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Penulis tidak ditemukan"));

        List<Book> books = bookRepository.findAllById(bookIds);
        if (books.size() != bookIds.size()) {
            throw new RuntimeException("Satu atau lebih buku tidak ditemukan");
        }

        if (author.getBooks() == null) {
            author.setBooks(new ArrayList<>());
        }

        for (Book book : books) {
            if (!author.getBooks().contains(book)) {
                author.getBooks().add(book);

                if (book.getAuthors() == null) {
                    book.setAuthors(new ArrayList<>());
                }
                if (!book.getAuthors().contains(author)) {
                    book.getAuthors().add(author);
                    bookRepository.save(book);
                }
            }
        }

        authorRepository.save(author);
        log.info("{} buku berhasil ditambahkan ke penulis {}", books.size(), author.getName());
    }

    @Override
    public void removeBookFromAuthor(String authorId, String bookId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Penulis tidak ditemukan"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Buku tidak ditemukan"));

        if (author.getBooks() == null || !author.getBooks().contains(book)) {
            throw new RuntimeException("Buku tidak ditemukan dalam daftar penulis");
        }

        author.getBooks().remove(book);

        if (book.getAuthors() != null) {
            book.getAuthors().remove(author);
            bookRepository.save(book);
        }

        authorRepository.save(author);
        log.info("Buku {} berhasil dihapus dari penulis {}", book.getTitle(), author.getName());
    }

    @Override
    public void edit(AuthorRequestRecord request) {
        validatorService.validator(request);

        var authorExisting = authorRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Penulis tidak ditemukan"));
        log.info("Edit author dengan ID: {}", request.id());

        if (authorRepository.existsByNameAndIdNot(request.name(), request.id())) {
            throw new RuntimeException("Penulis dengan nama " + request.name() + " sudah terdaftar");
        }

        authorExisting.setName(request.name());
        authorExisting.setBiography(request.biography());
        authorExisting.setBirthDate(request.birthDate());
        authorRepository.save(authorExisting);
        log.info("Author berhasil diupdate");
    }

    @Override
    public Page<SimpleMap> findAll(AuthorFilterRecord filterRequest, Pageable pageable) {
        CustomBuilder<Author> builder = new CustomBuilder<>();

        FilterUtil.builderConditionNotBlankLike("name", filterRequest.name(), builder);

        Page<Author> authors = authorRepository.findAll(builder.build(), pageable);
        List<SimpleMap> listData = authors.stream().map(author -> {
            SimpleMap data = new SimpleMap();
            data.put("id", author.getId());
            data.put("name", author.getName());
            data.put("biography", author.getBiography());
            data.put("birthDate", author.getBirthDate());
            if (author.getBooks() != null) {
                List<SimpleMap> booksData = author.getBooks().stream().map(book -> {
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

        return AppPage.create(listData, pageable, authors.getTotalElements());
    }

    @Override
    public SimpleMap findById(String id) {
        var author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data penulis tidak ditemukan"));

        SimpleMap data = new SimpleMap();
        data.put("id", author.getId());
        data.put("name", author.getName());
        data.put("biography", author.getBiography());
        data.put("birthDate", author.getBirthDate());
        if (author.getBooks() != null) {
            List<SimpleMap> booksData = author.getBooks().stream().map(book -> {
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

    }

    @Override
    public void delete(String id) {
        var author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Penulis tidak ditemukan"));

        if (author.getBooks() != null && !author.getBooks().isEmpty()) {
            throw new RuntimeException("Tidak dapat menghapus penulis yang masih memiliki buku");
        }

        authorRepository.delete(author);
        log.info("Author berhasil dihapus");
    }
}
