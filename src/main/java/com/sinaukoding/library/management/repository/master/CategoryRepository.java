package com.sinaukoding.library.management.repository.master;

import com.sinaukoding.library.management.entity.managementuser.Member;
import com.sinaukoding.library.management.entity.master.Category;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryRepository extends JpaRepository<Category, String>, JpaSpecificationExecutor<Category> {
    Boolean existsByName(String name);

    Boolean existsByNameAndIdNot(String name, String id);
}
