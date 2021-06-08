package com.app.edit.domain.changerolecategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ChangeRoleCategoryRepository extends JpaRepository<ChangeRoleCategory,Long> {
    Optional<ChangeRoleCategory> findByName(String changeContent);
}
