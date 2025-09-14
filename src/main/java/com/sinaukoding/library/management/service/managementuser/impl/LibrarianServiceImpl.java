package com.sinaukoding.library.management.service.managementuser.impl;

import com.sinaukoding.library.management.builder.CustomBuilder;
import com.sinaukoding.library.management.entity.managementuser.Librarian;
import com.sinaukoding.library.management.entity.managementuser.User;
import com.sinaukoding.library.management.mapper.managementuser.LibrarianMapper;
import com.sinaukoding.library.management.model.app.AppPage;
import com.sinaukoding.library.management.model.app.SimpleMap;
import com.sinaukoding.library.management.model.enums.Role;
import com.sinaukoding.library.management.model.enums.Status;
import com.sinaukoding.library.management.model.filter.LibrarianFilterRecord;
import com.sinaukoding.library.management.model.request.LibrarianRequestRecord;
import com.sinaukoding.library.management.repository.managementuser.LibrarianRepository;
import com.sinaukoding.library.management.repository.managementuser.UserRepository;
import com.sinaukoding.library.management.service.app.ValidatorService;
import com.sinaukoding.library.management.service.managementuser.LibrarianService;
import com.sinaukoding.library.management.util.FilterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LibrarianServiceImpl implements LibrarianService {

    private final LibrarianRepository librarianRepository;
    private final UserRepository userRepository;
    private final ValidatorService validatorService;
    private final LibrarianMapper librarianMapper;

    @Override
    public void add(LibrarianRequestRecord request) {

        validatorService.validator(request);

        if (librarianRepository.existsByUserId(request.userId())) {
            log.error("User already registered as librarian");
            throw new RuntimeException("User sudah terdaftar sebagai librarian");
        }

        if (librarianRepository.existsByPhone(request.phone())) {
            log.error("Phone number already exist");
            throw new RuntimeException("Nomor telepon " + request.phone() + " sudah digunakan");
        }

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", request.userId());
                    return new RuntimeException("User tidak ditemukan");
                });

        user.setStatus(Status.AKTIF);
        user.setRole(Role.LIBRARIAN);
        var librarian = librarianMapper.requestToEntity(request);
        librarian.setUser(user);

        librarianRepository.save(librarian);
        log.info("Librarian berhasil ditambahkan");
    }

    @Override
    public void edit(LibrarianRequestRecord request) {
        validatorService.validator(request);

        var librarian = librarianRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("Librarian tidak ditemukan"));

        if (librarianRepository.existsByPhoneAndIdNot(request.phone(), request.userId())) {
            throw new RuntimeException("Nomor telepon " + request.phone() + " sudah digunakan");
        }

        librarian.setPhone(request.phone());
        librarian.setAddress(request.address());
        librarian.setStatus(request.status());

        librarianRepository.save(librarian);
        log.info("Librarian berhasil diupdate");
    }


    @Override
    public Page<SimpleMap> findAll(LibrarianFilterRecord filterRequest, Pageable pageable) {
        CustomBuilder<Librarian> builder = new CustomBuilder<>();

        FilterUtil.builderConditionNotBlankEqual("userId", filterRequest.userId(), builder);
        FilterUtil.builderConditionNotBlankLike("phone", filterRequest.phone(), builder);
        FilterUtil.builderConditionNotNullEqual("status", filterRequest.status(), builder);

        Page<Librarian> librarians = librarianRepository.findAll(builder.build(), pageable);

        List<SimpleMap> listData = librarians.stream().map(librarian -> {
            SimpleMap data = new SimpleMap();
            data.put("id", librarian.getId());
            data.put("phone", librarian.getPhone());
            data.put("address", librarian.getAddress());
            data.put("status", librarian.getStatus());
            if (librarian.getUser() != null) {
                SimpleMap userData = new SimpleMap();
                userData.put("id", librarian.getUser().getId());
                userData.put("username", librarian.getUser().getUsername());
                userData.put("name", librarian.getUser().getName());
                userData.put("email", librarian.getUser().getEmail());
                userData.put("status", librarian.getUser().getStatus());
                data.put("user", userData);
            }

            return data;
        }).toList();

        return AppPage.create(listData, pageable, librarians.getTotalElements());
    }

    @Override
    public SimpleMap findByUserId(String userId) {
        var librarian = librarianRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Librarian tidak ditemukan"));
        SimpleMap data = new SimpleMap();
        data.put("id", librarian.getId());
        data.put("phone", librarian.getPhone());
        data.put("address", librarian.getAddress());
        data.put("status", librarian.getStatus());
        data.put("createdDate", librarian.getCreatedDate());
        data.put("modifiedDate", librarian.getModifiedDate());
        if (librarian.getUser() != null) {
            SimpleMap userData = new SimpleMap();
            userData.put("id", librarian.getUser().getId());
            userData.put("username", librarian.getUser().getUsername());
            userData.put("name", librarian.getUser().getName());
            userData.put("email", librarian.getUser().getEmail());
            userData.put("status", librarian.getUser().getStatus());
            data.put("user", userData);
        }

        return data;
    }

    @Override
    public void delete(String id) {
        var librarian = librarianRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Librarian tidak ditemukan"));

        librarianRepository.delete(librarian);
        log.info("Librarian berhasil dihapus");
    }


}
