package com.app.edit.domain.appreciate;

import com.app.edit.domain.comment.Comment;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppreciateRepository extends JpaRepository<Appreciate, AppreciateId> {

    @Query(value = "select a from Appreciate a where a.userInfo.id = :userInfoId and a.comment = :comment and a.state = :state")
    Optional<Appreciate> findByUserInfoAndCommentAndState(@Param("userInfoId") Long userInfoId, @Param("comment") Comment comment, @Param("state") State state);
}
