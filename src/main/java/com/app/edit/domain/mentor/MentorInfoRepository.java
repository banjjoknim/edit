package com.app.edit.domain.mentor;

import com.app.edit.domain.user.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface MentorInfoRepository extends JpaRepository<MentorInfo, UserInfo> {
}
