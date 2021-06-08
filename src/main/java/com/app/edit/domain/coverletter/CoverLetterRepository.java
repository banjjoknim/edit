package com.app.edit.domain.coverletter;

import com.app.edit.domain.user.UserInfo;
import com.app.edit.enums.CoverLetterType;
import com.app.edit.enums.IsAdopted;
import com.app.edit.enums.State;
import com.app.edit.response.coverletter.GetCoverLettersRes;
import com.app.edit.response.sympathize.GetSympathizeCoverLetterRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CoverLetterRepository extends JpaRepository<CoverLetter, Long> {

    /*
     * 오늘의 문장 조회 쿼리
     **/
    @Query(value = "select cl from CoverLetter cl where cl.createdAt >= :startOfToday and cl.createdAt < :startOfTomorrow and cl.state = :state and cl.type = :type")
    Page<CoverLetter> findCoverLettersOnToday(Pageable pageable, @Param("startOfToday") LocalDateTime startOfToday,
                                              @Param("startOfTomorrow") LocalDateTime startOfTomorrow, @Param("state") State state, @Param("type") CoverLetterType type);

    /*
     * 코멘트를 기다리고 있어요 조회 쿼리
     **/
    @Query(value = "select cl from CoverLetter cl where size(cl.comments) = 0 and cl.state = :state and cl.type = :type")
    Page<CoverLetter> findCoverLettersHasNotComment(Pageable pageable, @Param("state") State state, @Param("type") CoverLetterType type);

    /*
     * 채택이 완료되었어요 조회 쿼리
     **/
    @Query(value = "select cl from CoverLetter cl where cl.state = :state and exists(select c from Comment c where c.isAdopted = :isAdopted and c.coverLetter = cl and c.state = :state) and cl.type = :type")
    Page<CoverLetter> findCoverLettersHasAdoptedComment(Pageable pageable, @Param("isAdopted") IsAdopted isAdopted, @Param("state") State state, @Param("type") CoverLetterType type);

    /*
     * 많은 분들이 공감하고 있어요 조회 쿼리
     **/
    @Query(value = "select cl from CoverLetter cl where cl.createdAt >= :beforeThreeDays and cl.state = :state and cl.type = :type order by size(cl.sympathies) desc ")
    Page<CoverLetter> findCoverLettersHasManySympathies(Pageable pageable, @Param("beforeThreeDays") LocalDateTime beforeThreeDays, @Param("state") State state, @Param("type") CoverLetterType type);

    /**
     * 내가 등록했지만 아직 완성되지 않은 자소서 목록 조회
     *
     * @param pageable
     * @param type
     * @return
     */
    @Query(value = "select cl " +
            "from CoverLetter cl " +
            "where cl.userInfo.id = :userInfoId and cl.state = :state and cl.type = :type " +
            "and not exists(select cl2 from CoverLetter cl2 where cl.id = cl2.originalCoverLetterId and cl2.state = :state and cl2.type = 'COMPLETING') " +
            "order by cl.createdAt desc ")
    Page<CoverLetter> findMyCoverLettersNotCompleted(Pageable pageable, @Param("userInfoId") Long userInfoId, @Param("state") State state, @Param("type") CoverLetterType type);

    /*
     * 내가 등록한/완성한 자소서 목록 조회 쿼리
     **/
    @Query(value = "select c from CoverLetter c where c.userInfo.id = :userInfoId and c.state = :state and c.type = :type order by c.createdAt desc ")
    Page<CoverLetter> findMyCoverLetters(Pageable pageable, @Param("userInfoId") Long userInfoId,
                                         @Param("state") State state, @Param("type") CoverLetterType type);

    /*
     * 내가 공감한 자소서 정보 조회
     */
    @Query(value = "select new com.app.edit.response.sympathize.GetSympathizeCoverLetterRes(c.id, c.content, c.coverLetterCategory.name, true) from CoverLetter c where c.id = :coverLetterId and c.state = :state")
    GetSympathizeCoverLetterRes findBySympathizeCoverLetter(@Param("coverLetterId") Long coverLetterId, @Param("state") State state);

    /**
     * 내가 공감한 자소서 조회
     * @param userInfoId
     * @param state
     * @return
     */
    @Query(value = "select cl from CoverLetter cl inner join Sympathy s on cl.id = s.coverLetter.id where s.userInfo.id = :userInfoId and s.state = :state and cl.state = :state and cl.type = :type order by s.updatedAt desc")
    Page<CoverLetter> findMySympathizeCoverLetters(Pageable pageable, @Param("userInfoId") Long userInfoId, @Param("state") State state, @Param("type") CoverLetterType type);

    /**
     * 유저가 오늘 작성한 자소서 개수 조회 쿼리
     *
     * @param userInfoId
     * @param startOfToday
     * @param startOfTomorrow
     * @param state
     * @return
     */
    @Query(value = "select count(cl) from CoverLetter cl where cl.userInfo.id = :userInfoId and cl.createdAt >= :startOfToday and cl.createdAt < :startOfTomorrow and cl.state = :state")
    Long getTodayWritingCoverLetterCount(@Param("userInfoId") Long userInfoId, @Param("startOfToday") LocalDateTime startOfToday,
                                         @Param("startOfTomorrow") LocalDateTime startOfTomorrow, @Param("state") State state);

    /**
     * 유저가 작성한 자소서 개수 조회
     * @param userInfo
     * @return
     */
    Long countByUserInfoAndState(UserInfo userInfo, State active);

    /**
     * 유저가 작성한 완성된 자소서 개수 조회
     * @param userInfo
     * @param active
     * @param
     * @return
     */
    Long countByUserInfoAndStateAndType(UserInfo userInfo, State active,CoverLetterType coverLetterType);
}
