package com.app.edit.domain.profileemotion;

import com.app.edit.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProfileEmotionRepository extends JpaRepository<ProfileEmotion,Long> {

    Optional<ProfileEmotion> findByStateAndName(State active, String emotionName);
}
