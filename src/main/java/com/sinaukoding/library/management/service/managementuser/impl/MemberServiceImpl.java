package com.sinaukoding.library.management.service.managementuser.impl;

import com.sinaukoding.library.management.builder.CustomBuilder;
import com.sinaukoding.library.management.entity.managementuser.Member;
import com.sinaukoding.library.management.entity.managementuser.User;
import com.sinaukoding.library.management.mapper.managementuser.MemberMapper;
import com.sinaukoding.library.management.model.app.AppPage;
import com.sinaukoding.library.management.model.app.SimpleMap;
import com.sinaukoding.library.management.model.enums.Status;
import com.sinaukoding.library.management.model.filter.MemberFilterRecord;
import com.sinaukoding.library.management.model.request.MemberRequestRecord;
import com.sinaukoding.library.management.repository.managementuser.MemberRepository;
import com.sinaukoding.library.management.repository.managementuser.UserRepository;
import com.sinaukoding.library.management.service.app.ValidatorService;
import com.sinaukoding.library.management.service.managementuser.MemberService;
import com.sinaukoding.library.management.util.FilterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ValidatorService validatorService;
    private final MemberMapper memberMapper;
    private final UserRepository userRepository;


    @Override
    public void add(MemberRequestRecord request) {
        validatorService.validator(request);

        if (memberRepository.existsByPhone(request.phone().toLowerCase())) {
            log.error("Phone number already exist");
            throw new RuntimeException("Phone [" + request.phone() + "] sudah digunakan");
        }

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", request.userId());
                    return new RuntimeException("User not found with id: " + request.userId());
                });

        user.setStatus(Status.AKTIF);
        var member = memberMapper.requestToEntity(request);
        member.setMembershipDate(LocalDate.now());
        member.setUser(user);
        memberRepository.save(member);
        log.info("Member berhasil ditambahkan");
        log.trace("Tambah data user berhasil dan selesai");

    }

    @Override
    public void edit(MemberRequestRecord request) {
        validatorService.validator(request);

        var memberExisting = memberRepository.findByUserId(request.userId()).orElseThrow(() -> new RuntimeException("Member tidak ditemukan"));
        log.info("user ?");

        if (memberRepository.existsByPhoneAndIdNot(request.phone().toLowerCase(), request.userId())) {
            throw new RuntimeException("Phone [" + request.phone() + "] sudah digunakan");
        }

        memberExisting.setPhone(request.phone().toLowerCase());
        memberExisting.setAddress(request.address());
        memberExisting.setStatus(request.status());
        memberRepository.save(memberExisting);
    }

    @Override
    public Page<SimpleMap> findAll(MemberFilterRecord filterRequest, Pageable pageable) {
        CustomBuilder<Member> builder = new CustomBuilder<>();

        FilterUtil.builderConditionNotBlankLike("user.id", filterRequest.userId(), builder);
        FilterUtil.builderConditionNotBlankLike("user.username", filterRequest.username(), builder);
        FilterUtil.builderConditionNotBlankLike("user.name", filterRequest.name(), builder);
        FilterUtil.builderConditionNotBlankLike("phone", filterRequest.phone(), builder);
        FilterUtil.builderConditionNotNullEqual("status", filterRequest.status(), builder);

        Page<Member> members = memberRepository.findAll(builder.build(), pageable);
        List<SimpleMap> listData = members.stream().map(member -> {
            SimpleMap data = new SimpleMap();
            data.put("id", member.getId());
            data.put("phone", member.getPhone());
            data.put("address", member.getAddress());
            data.put("status", member.getStatus());

            if (member.getUser() != null) {
                SimpleMap userData = new SimpleMap();
                userData.put("userId", member.getUser().getId());
                userData.put("name", member.getUser().getName());
                userData.put("username", member.getUser().getUsername());
                userData.put("email", member.getUser().getEmail());
                userData.put("status", member.getUser().getStatus());

                data.put("user", userData);
            } else {
                data.put("user", null);
            }
            return data;
        }).toList();

        return AppPage.create(listData, pageable, members.getTotalElements());
    }

    @Override
    public SimpleMap findById(String id) {
        var member = memberRepository.findByUserId(id).orElseThrow(() ->  new RuntimeException("Data member tidak ditemukan"));
        SimpleMap data = new SimpleMap();
        data.put("id", member.getId());
        data.put("phone", member.getPhone());
        data.put("address", member.getAddress());
        data.put("status", member.getStatus());

        if (member.getUser() != null) {
            SimpleMap userData = new SimpleMap();
            userData.put("userId", member.getUser().getId());
            userData.put("name", member.getUser().getName());
            userData.put("username", member.getUser().getUsername());
            userData.put("email", member.getUser().getEmail());
            userData.put("status", member.getUser().getStatus());

            data.put("user", userData);
        } else {
            data.put("user", null);
        }
        return data;
    }

    @Override
    public void delete(String id) {
        var member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member tidak ditemukan"));



        memberRepository.delete(member);
        log.info("Category berhasil dihapus");
    }

}
