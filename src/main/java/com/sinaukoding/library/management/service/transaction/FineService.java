package com.sinaukoding.library.management.service.transaction;

import com.sinaukoding.library.management.model.app.SimpleMap;
import com.sinaukoding.library.management.model.filter.FineFilterRecord;
import com.sinaukoding.library.management.model.request.FineRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FineService {

    void createFine(FineRequestRecord request);
    void payFine(String fineId);
    void waiveFine(String fineId);
    Page<SimpleMap> findAll(FineFilterRecord filterRequest, Pageable pageable);
    SimpleMap findById(String id);
    Page<SimpleMap> findMemberFines(String memberId, Pageable pageable);
}
