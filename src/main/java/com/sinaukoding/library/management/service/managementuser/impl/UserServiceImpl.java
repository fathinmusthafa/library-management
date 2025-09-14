package com.sinaukoding.library.management.service.managementuser.impl;

import com.sinaukoding.library.management.builder.CustomBuilder;
import com.sinaukoding.library.management.entity.managementuser.User;
import com.sinaukoding.library.management.mapper.managementuser.UserMapper;
import com.sinaukoding.library.management.model.app.AppPage;
import com.sinaukoding.library.management.model.app.SimpleMap;
import com.sinaukoding.library.management.model.enums.Role;
import com.sinaukoding.library.management.model.enums.Status;
import com.sinaukoding.library.management.model.filter.UserFilterRecord;
import com.sinaukoding.library.management.model.request.UserRequestRecord;
import com.sinaukoding.library.management.repository.managementuser.MemberRepository;
import com.sinaukoding.library.management.repository.managementuser.UserRepository;
import com.sinaukoding.library.management.service.app.ValidatorService;
import com.sinaukoding.library.management.service.managementuser.UserService;
import com.sinaukoding.library.management.util.FilterUtil;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ValidatorService validatorService;
    private final MemberRepository memberRepository;


    @Override
    public void add(UserRequestRecord request) {
        try {
            log.trace("Masuk ke menu tambah data user");
            log.debug("Request data user: {}", request);

            validatorService.validator(request);

            if (userRepository.existsByEmail(request.email().toLowerCase())) {
                log.info("Email sudah digunakan");
                throw new DuplicateRequestException("Email [" + request.email() + "] sudah digunakan");
            }
            if (userRepository.existsByUsername(request.username().toLowerCase())) {
                log.info("Username sudah digunakan");
                throw new DuplicateRequestException("Username [" + request.username() + "] sudah digunakan");
            }

            var user = userMapper.requestToEntity(request);
            user.setRole(Role.MEMBER);
            user.setStatus(Status.PENDING);
            user.setPassword(passwordEncoder.encode(request.password()));
            userRepository.save(user);
            log.info("User {} berhasil ditambahkan", request.name());
            log.trace("Tambah data user berhasil dan selesai");

        } catch (Exception e){
            log.error("Tambah data user gagal: {}", e.getMessage());
            throw new DuplicateRequestException(e.getMessage());
        }

    }

    @Override
    public void edit(UserRequestRecord request) {
        validatorService.validator(request);

        var userExisting = userRepository.findById(request.id()).orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        log.info("user ?");

        if (userRepository.existsByEmailAndIdNot(request.email().toLowerCase(), request.id())) {
            throw new RuntimeException("Email [" + request.email() + "] sudah digunakan");
        }
        if (userRepository.existsByUsernameAndIdNot(request.username().toLowerCase(),  request.id())) {
            throw new RuntimeException("Username [" + request.username() + "] sudah digunakan");
        }

        var user = userMapper.requestToEntity(request);
        user.setId(userExisting.getId());
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    @Override
    public Page<SimpleMap> findAll(UserFilterRecord filterRequest, Pageable pageable) {
        CustomBuilder<User> builder = new CustomBuilder<>();

        FilterUtil.builderConditionNotBlankLike("name", filterRequest.name(), builder);
        FilterUtil.builderConditionNotBlankLike("email", filterRequest.email(), builder);
        FilterUtil.builderConditionNotBlankLike("username", filterRequest.username(), builder);
        FilterUtil.builderConditionNotNullEqual("status", filterRequest.status(), builder);
        FilterUtil.builderConditionNotNullEqual("role", filterRequest.role(), builder);

        Page<User> listUser = userRepository.findAll(builder.build(), pageable);
        List<SimpleMap> listData = listUser.stream().map(user -> {
            SimpleMap data = new SimpleMap();
            data.put("id", user.getId());
            data.put("name", user.getName());
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("role", user.getRole().getLabel());
            data.put("status", user.getStatus().getLabel());
            return data;
        }).toList();

        return AppPage.create(listData, pageable, listUser.getTotalElements());
    }

    @Override
    public SimpleMap findById(String id) {
        var user = userRepository.findById(id).orElseThrow(() ->  new RuntimeException("Data user tidak ditemukan"));
        SimpleMap data = new SimpleMap();
        data.put("id", user.getId());
        data.put("name", user.getName());
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("status", user.getStatus().getLabel());
        data.put("role", user.getRole().getLabel());
        return data;
    }

    @Override
    public void delete(String id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));


        if (memberRepository.existsByUserId(id) ) {
            throw new RuntimeException("Tidak dapat menghapus user yang masih menjadi member");
        }

        userRepository.delete(user);
        log.info("Category berhasil dihapus");
    }

}
