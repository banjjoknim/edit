package com.app.edit.response.user;

import com.app.edit.response.rank.GetUserRankRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GetRankRes {

    private final List<GetUserRankRes> getUserRankResList;
}
