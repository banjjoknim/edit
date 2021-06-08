package com.app.edit.domain.comment;

import com.app.edit.domain.coverletter.CoverLetter;
import com.app.edit.enums.IsAdopted;
import com.app.edit.enums.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /*
     * 자소서에 달린 코멘트 조회 쿼리
     **/
    @Query(value = "select c from Comment c where c.coverLetter = :coverLetter and c.state = :state order by c.createdAt desc")
    Page<Comment> findCommentsByCoverLetter(Pageable pageable, @Param("coverLetter") CoverLetter coverLetter, @Param("state") State state);

    /**
     * 자소서에서 채택된 코멘트 조회 쿼리
     * @param coverLetter
     * @param isAdopted
     * @return
     */
    @Query(value = "select c from Comment c where c.coverLetter = :coverLetter and c.isAdopted = :isAdopted")
    Optional<Comment> findAdoptedCommentByCoverLetter(@Param("coverLetter") CoverLetter coverLetter,
                                                      @Param("isAdopted") IsAdopted isAdopted);

    /**
     * 자소서에서 채택되지 않은 코멘트 조회 쿼리
     * @param coverLetter
     * @param isAdopted
     * @return
     */
    @Query(value = "select c from Comment c where c.coverLetter = :coverLetter and c.isAdopted = :isAdopted and c.state = :state")
    Page<Comment> findNotAdoptedCommentsByCoverLetter(Pageable pageable, @Param("coverLetter") CoverLetter coverLetter,
                                                      @Param("isAdopted") IsAdopted isAdopted,
                                                      @Param("state") State state);

    /**
     * 내 코멘트 조회
     * @param pageable
     * @param userInfoId
     * @return
     */
    @Query(value = "select c from Comment c where c.userInfo.id = :userInfoId and c.state = :state order by c.createdAt desc")
    Page<Comment> findByUser(Pageable pageable,@Param("userInfoId") Long userInfoId, @Param("state") State state);

    Optional<Comment> findByIdAndState(Long commentId,State state);

    /**
     * 내가 작성한 코멘트 수 구하기
     * @param userInfoId
     * @param state
     * @return
     */
    @Query(value = "select count(c) from Comment c where c.userInfo.id = :userInfoId and c.state = :state")
    Long findByUserAndState(@Param("userInfoId") Long userInfoId,@Param("state") State state);

    /**
     * 내가 채택한 코멘트 수 구하기
     * @param userInfoId
     * @param state
     * @param isAdopted
     * @return
     */
    @Query(value = "select count(c) from Comment c where c.userInfo.id = :userInfoId and c.state = :state and c.isAdopted = :isAdopted")
    Long findUserAndStateAndAdopt(@Param("userInfoId") Long userInfoId, @Param("state") State state,@Param("isAdopted") IsAdopted isAdopted);
}
