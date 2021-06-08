package com.app.edit.domain.profilecolor;

import com.app.edit.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProfileColorRepository extends JpaRepository<ProfileColor,Long> {

    Optional<ProfileColor> findByStateAndName(State active, String colorName);
}
