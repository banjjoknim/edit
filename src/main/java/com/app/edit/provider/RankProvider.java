package com.app.edit.provider;

import com.app.edit.config.BaseException;
import com.app.edit.config.BaseResponseStatus;
import com.app.edit.domain.user.UserInfo;
import com.app.edit.domain.user.UserInfoRepository;
import com.app.edit.enums.CoverLetterType;
import com.app.edit.enums.IsAdopted;
import com.app.edit.enums.State;
import com.app.edit.enums.UserRole;
import com.app.edit.response.rank.GetRankMenteeRes;
import com.app.edit.response.rank.GetRankMentorRes;
import com.app.edit.response.user.GetRankRes;
import com.app.edit.response.rank.GetUserRankRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.app.edit.config.BaseResponseStatus.EMPTY_USER_RANK;
import static com.app.edit.config.BaseResponseStatus.NOT_FOUND_USER;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Transactional(readOnly = true)
@Service
@Slf4j
public class RankProvider {

    private final UserInfoRepository userInfoRepository;
    private final CommentProvider commentProvider;
    private final CoverLetterProvider coverLetterProvider;

    @Autowired
    public RankProvider(UserInfoRepository userInfoRepository, CommentProvider commentProvider, CoverLetterProvider coverLetterProvider) {
        this.userInfoRepository = userInfoRepository;
        this.commentProvider = commentProvider;
        this.coverLetterProvider = coverLetterProvider;
    }

    /**
     * 멘토 / 멘티 랭킹 조회
     * @param requestRole
     * @return
     */
    public GetRankRes getRank(String requestRole) throws BaseException {

        Pageable pageRequest = PageRequest.of(0,100);

        List<UserInfo> userInfo;

        if(requestRole.equals("MENTOR")) {

            userInfo = userInfoRepository
                    .findByAdoptAndState(pageRequest, UserRole.MENTOR, State.ACTIVE);

        }else if(requestRole.equals("MENTEE")){

            userInfo = userInfoRepository
                    .findByCoverLetterAndState(pageRequest, UserRole.MENTEE, State.ACTIVE);
        }else {
            throw new BaseException(BaseResponseStatus.FAILED_TO_GET_ROLE);
        }

        //userInfo.forEach(userInfo1 -> System.out.println(userInfo1.getId()));

        if(userInfo.size() == 0)
            throw new BaseException(EMPTY_USER_RANK);

        AtomicLong count = new AtomicLong(1);

        return GetRankRes.builder()
                .getUserRankResList(userInfo.stream()
                        .map(userInfo1 -> GetUserRankRes.builder()
                                .rankId(count.getAndIncrement())
                                .userId(userInfo1.getId())
                                .colorName(userInfo1.getUserProfile().getProfileColor().getName())
                                .emotionName(userInfo1.getUserProfile().getProfileEmotion().getName())
                                .nickName(userInfo1.getNickName())
                                .jobName(userInfo1.getJob().getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * 멘토 랭킹 상세 조회
     * @param userInfoId
     * @param rankId
     */
    public GetRankMentorRes getRankMentor(Long userInfoId, Long rankId) throws BaseException{

        UserInfo userInfo = userInfoRepository.findByStateAndId(State.ACTIVE,userInfoId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        return GetRankMentorRes.builder()
                .rankId(rankId)
                .nickName(userInfo.getNickName())
                .colorName(userInfo.getUserProfile().getProfileColor().getName())
                .emotionName(userInfo.getUserProfile().getProfileEmotion().getName())
                .userRole(userInfo.getUserRole())
                .commentCount(commentProvider.retrieveMyCommentCount(userInfoId))
                .commentAdoptCount(userInfo.getIsAdoptedCommentCount())
                .build();
    }

    /**
     * 멘티 랭킹 상세 조회
     * @param userInfoId
     * @param rankId
     * @return
     */
    public GetRankMenteeRes getRankMentee(Long userInfoId, Long rankId) throws BaseException {
        UserInfo userInfo = userInfoRepository.findByStateAndId(State.ACTIVE,userInfoId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        return GetRankMenteeRes.builder()
                .rankId(rankId)
                .nickName(userInfo.getNickName())
                .colorName(userInfo.getUserProfile().getProfileColor().getName())
                .emotionName(userInfo.getUserProfile().getProfileEmotion().getName())
                .userRole(userInfo.getUserRole())
                .coverLetterCount(coverLetterProvider.getCoverLetterByUser(userInfo))
                .coverLetterCompleteCount(userInfo.getCompleteCoverLetterCount())
                .build();
    }
}