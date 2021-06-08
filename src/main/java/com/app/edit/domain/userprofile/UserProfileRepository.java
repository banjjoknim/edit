package com.app.edit.domain.userprofile;

import com.app.edit.domain.user.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, UserInfo> {
}
