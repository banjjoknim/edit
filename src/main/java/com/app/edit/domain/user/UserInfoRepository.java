package com.app.edit.domain.user;

import com.app.edit.enums.CoverLetterType;
import com.app.edit.enums.IsAdopted;
import com.app.edit.enums.State;
import com.app.edit.enums.UserRole;
import com.app.edit.response.user.GetProfileRes;
import com.app.edit.response.user.GetSympathizeUserRes;
import com.app.edit.response.user.GetUserInfosRes;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo,Long> {
    List<UserInfo> findByState(State state);

    List<UserInfo> findByStateAndEmailIsContaining(State active, String email);

    Optional<UserInfo> findByStateAndEmailAndPassword(State active, String email, String password);

    Optional<UserInfo> findByStateAndNameAndPhoneNumber(State active, String name, String phoneNumber);

    Optional<UserInfo> findByStateAndNameAndPhoneNumberAndEmail(State active, String name, String phoneNumber, String email);

    Optional<UserInfo> findByStateAndPassword(State active, String password);

    Optional<UserInfo> findByStateAndId(State active, Long userId);

    List<UserInfo> findByStateAndNickNameIsContaining(State active, String nickName);

    /**
     * 멘토 랭킹 조회
     */
    @Query(value = "select u from UserInfo u join fetch Comment c on u.id = c.userInfo.id" +
            "     where u.userRole = :role and u.state = :state " +
            "     group by u.id" +
            "     order by u.isAdoptedCommentCount + count(c.id) DESC")
    List<UserInfo> findByAdoptAndState(Pageable pageable,
                                       @Param("role") UserRole role ,
                                       @Param("state") State state);

    /**
     * 멘티 랭킹 조회
     */
    @Query(value = "select u from UserInfo u join fetch CoverLetter c on u.id = c.userInfo.id"+
            " where u.userRole = :role and u.state = :state group by u.id " +
            " order by u.completeCoverLetterCount + count(c.id) DESC")
    List<UserInfo> findByCoverLetterAndState(Pageable pageRequest,
                                             @Param("role") UserRole role,
                                             @Param("state") State state);


    /**
     * 내 프로필 조회
     * @param userInfoId
     * @param state
     * @return
     */
    @Query(value = "select new com.app.edit.response.user.GetProfileRes(u.nickName,p.profileEmotion.name,p.profileColor.name,u.userRole) " +
            "from UserInfo u join fetch UserProfile p " +
            "on u.id = p.userInfo.id where u.id = :userInfoId and u.state = :state group by u.id")
    Optional<GetProfileRes> findProfileByUser(@Param("userInfoId") Long userInfoId,@Param("state") State state);

    /**
     * 내가 공감한 유저 정보 조회
     * @param userInfoId
     * @param state
     * @return
     */
    @Query(value = "select " +
            "new com.app.edit.response.user.GetUserInfosRes" +
            "(u.nickName,p.profileEmotion.name,p.profileColor.name,u.userRole, u.job.name) " +
            "from UserInfo u join fetch UserProfile p on u.id = p.userInfo.id " +
            "where u.id = :userInfoId and u.state = :state group by u.id")
    Optional<GetUserInfosRes> findProfileBySympathizeUser(@Param("userInfoId") Long userInfoId, @Param("state") State state);
}
