package com.app.edit.controller;

import com.app.edit.config.BaseException;
import com.app.edit.config.BaseResponse;
import com.app.edit.config.BaseResponseStatus;
import com.app.edit.provider.RankProvider;
import com.app.edit.provider.UserProvider;
import com.app.edit.response.rank.GetRankMenteeRes;
import com.app.edit.response.rank.GetRankMentorRes;
import com.app.edit.response.user.GetRankRes;
import com.app.edit.response.user.GetUserRes;
import com.app.edit.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.app.edit.config.BaseResponseStatus.SUCCESS;

@Slf4j
@RequestMapping("/api")
@RestController
public class RankController {

    public final RankProvider rankProvider;
    public final JwtService jwtService;

    @Autowired
    public RankController(RankProvider rankProvider,
                          JwtService jwtService){
        this.rankProvider = rankProvider;
        this.jwtService = jwtService;
    }


    @GetMapping("/ranks")
    public BaseResponse<GetRankRes> getRanks(
            @RequestParam(value = "requestRankByRole") String requestRole) {

        try{
            GetRankRes getRankRes = rankProvider.getRank(requestRole);
            return new BaseResponse<>(SUCCESS, getRankRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 멘토 랭킹 상세 조회
     * @param userId
     * @param rankId
     * @return
     */
    @GetMapping("/ranks-mentor/users/{userId}")
    public BaseResponse<GetRankMentorRes> getRankMentor(
            @PathVariable Long userId,
            @RequestParam(value = "rankId") Long rankId) throws BaseException{

        try{
            GetRankMentorRes getRankMentorRes = rankProvider.getRankMentor(userId,rankId);
            return new BaseResponse<>(SUCCESS, getRankMentorRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 멘티 랭킹 상세 조회
     * @param userId
     * @param rankId
     * @return
     */
    @GetMapping("/ranks-mentee/users/{userId}")
    public BaseResponse<GetRankMenteeRes> getRankMentee(
            @PathVariable Long userId,
            @RequestParam(value = "rankId") Long rankId) throws BaseException{

        try{
            GetRankMenteeRes getRankMenteeRes = rankProvider.getRankMentee(userId,rankId);
            return new BaseResponse<>(SUCCESS, getRankMenteeRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
