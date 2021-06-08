package com.app.edit.response.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class GetNotAdoptedCommentContentsRes {

    private List<String> notAdoptedCommentContents;
}
