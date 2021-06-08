package com.app.edit.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    // 1000 : 요청 성공
    SUCCESS(true, 1000, "요청에 성공하였습니다."),
    SUCCESS_READ_USERS(true, 1010, "회원 전체 정보 조회에 성공하였습니다."),
    SUCCESS_READ_USER(true, 1011, "회원 정보 조회에 성공하였습니다."),
    SUCCESS_POST_USER(true, 1012, "회원가입에 성공하였습니다."),
    SUCCESS_LOGIN(true, 1013, "로그인에 성공하였습니다."),
    SUCCESS_JWT(true, 1014, "JWT 검증에 성공하였습니다."),
    SUCCESS_DELETE_USER(true, 1015, "회원 탈퇴에 성공하였습니다."),
    SUCCESS_PATCH_USER(true, 1016, "회원정보 수정에 성공하였습니다."),
    SUCCESS_READ_SEARCH_USERS(true, 1017, "회원 검색 조회에 성공하였습니다."),

    // 2000 : Request 오류
    REQUEST_ERROR(false, 2000, "요청 바디의 입력값을 확인해주세요."),
    EMPTY_USERID(false, 2001, "유저 아이디 값을 확인해주세요."),
    EMPTY_JWT(false, 2010, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2011, "유효하지 않은 JWT입니다."),
    EMPTY_EMAIL(false, 2020, "이메일을 입력해주세요."),
    INVALID_EMAIL(false, 2021, "이메일 형식을 확인해주세요."),
    EMPTY_PASSWORD(false, 2030, "비밀번호를 입력해주세요."),
    EMPTY_CONFIRM_PASSWORD(false, 2031, "확인 비밀번호를 입력해주세요."),
    WRONG_PASSWORD(false, 2032, "비밀번호를 다시 입력해주세요."),
    DO_NOT_MATCH_PASSWORD(false, 2033, "비밀번호와 비밀번호확인 값이 일치하지 않습니다."),
    EMPTY_NICKNAME(false, 2040, "닉네임을 입력해주세요."),
    INVALID_PHONENUMBER(false, 2041, "전화번호 형식을 확인해주세요."),
    EXPIRED_JWT(false, 2042, "토큰이 만료되었습니다."),
    EMPTY_RECEIVER(false, 2043, "이메일 수신자가 없습니다."),
    AUTHENTICATION_TIME_EXPIRED(false, 2044, "인증 시간이 만료되었습니다."),
    EMPTY_CONTENT(false, 2045, "이메일이나 닉네임을 입력하세요."),
    INVAILD_CONTENT(false, 2046, "이메일이나 닉네임 중 하나만 입력하세요."),
    UNAUTHORIZED_AUTHORITY(false, 2047, "권한이 없습니다."),
    EMPTY_JOBNAME(false, 2048, "직군이 비었습니다."),
    EMPTY_ETCJOBNAME(false, 2049, "기타 직군이 비었습니다."),
    EMPTY_PHONENUMBER(false, 2500, "핸드폰 번호가 비었습니다."),
    EMPTY_COLORNAME(false, 2501, "색상 이름이 비었습니다."),
    EMPTY_EMOTIONNAME(false, 2502, "감정표현 이름이 비었습니다."),
    ALREADY_LOGOUT(false, 2503, "로그아웃 처리된 JWT 입니다."),
    EMPTY_NAME(false, 2504, "이름이 비었습니다."),
    INVALID_NICKNAME(false, 2505, "닉네임 형식을 확인해주세요."),
    INVALID_NAME(false, 2506, "이름 형식을 확인해주세요."),
    INVALID_PASSWORD(false, 2507, "비밀번호 형식을 확인해주세요."),
    INVALID_CONFIRM_PASSWORD(false, 2508, "확인 비밀번호 형식을 확인해주세요."),
    EMPTY_USER_RANK(false, 2509, "랭킹을 조회할 유저가 없습니다."),



    // colt
    REQUEST_PARAMETER_MISSING(false, 2050, "요청에 필수 파라미터가 누락되어 있습니다."),
    REQUEST_PARAMETER_MISMATCH(false, 2051, "요청 파라미터 타입이 맞지 않습니다."),
    COVER_LETTER_CONTENT_LENGTH_CAN_NOT_BE_GREATER_THAN_LENGTH_LIMIT(false, 2100, "자소서의 내용 길이는 90자를 초과할 수 없습니다."),
    ETC_CHANGE_ROLE_CONTENT_CAN_NOT_BE_EMPTY(false, 2110, "역할 변경 '기타' 사유의 '기타 의견'을 입력해주세요."),
    ETC_CHANGE_ROLE_CONTENT_LENGTH_MUST_GREATER_THAN_MINIMUM_LENGTH(false, 2120, "역할 변경 '기타' 사유의 '기타 의견'의 길이는 10자 이상이어야 합니다."),

    // 3000 : Response 오류
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),
    NOT_FOUND_USER(false, 3010, "존재하지 않는 회원입니다."),
    DUPLICATED_USER(false, 3011, "이미 존재하는 회원입니다."),
    FAILED_TO_GET_USER(false, 3012, "회원 정보 조회에 실패하였습니다."),
    FAILED_TO_POST_USER(false, 3013, "회원가입에 실패하였습니다."),
    FAILED_TO_LOGIN(false, 3014, "로그인 정보가 없습니다."),
    FAILED_TO_DELETE_USER(false, 3015, "회원 탈퇴에 실패하였습니다."),
    FAILED_TO_PATCH_USER(false, 3016, "개인정보 수정에 실패하였습니다."),
    FAILED_TO_SEND_EMAIL(false, 3017, "이메일 전송에 실패했습니다."),
    FAILED_TO_AUTHENTICATION_CODE(false, 3018, "인증 번호 인증에 실패했습니다."),
    FAILED_TO_ENCRYPT_PASSWORD(false, 3019, "비밀 번호 암호화에 실패했습니다."),
    FAILED_TO_UPDATE_USER(false, 3020, "비밀번호 변경에 실패했습니다."),
    FAILED_TO_POST_CERTIFICATION_REQUEST(false, 3021, "멘토 변경 인증 생성에 실패했습니다."),
    NOT_FOUND_COLOR(false, 3022, "색상 조회에 실패했습니다."),
    NOT_FOUND_EMOTION(false, 3023, "이모티콘 조회에 실패했습니다."),
    FAILED_TO_POST_USER_PROFILE(false, 3024, "유저 프로필 등록에 실패했습니다."),
    FAILED_TO_GET_JOB(false, 3025, "직군 조회에 실패했습니다."),
    FAILED_TO_GET_CERTIFICATION_REQUEST(false, 3026, "인증 요청 조회에 실패했습니다."),
    FAILED_TO_GET_ROLE(false, 3027, "역할을 찾을수 없습니다."),
    FAILED_TO_POST_CAHNGE_ROLE_REQUEST(false, 3028, "역할 변경 요청 등록에 실패했습니다."),
    FAILED_TO_GET_CAHNGE_ROLE_CATEGORY(false, 3029, "역할 카테고리 조회에 실패했습니다."),
    FAILED_TO_POST_COMMENT(false, 3030, "코멘트 등록에 실패 했습니다."),
    ALREADY_ROLE_MENTEE(false, 3031, "이미 멘티 입니다."),
    FAILED_TO_GET_SYMPATHIES_COVERLETTER(false, 3032, "공감한 자소서 조회에 실패 했습니다."),
    NOT_FOUND_TEMPORARY_COMMENT(false, 3033, "임시 코멘트가 비었습니다."),
    FAILED_TO_POST_TEMPORARY_COMMENT(false, 3034, "코멘트 임시저장에 실패 했습니다."),
    FAILED_TO_DELETE_COMMENT(false, 3035, "채택된 코멘트는 삭제할 수 없습니다."),


    // colt
    TOKEN_INFORMATION_IS_NOT_EQUALS_IN_SERVER(false, 3050, "토큰의 정보가 서버의 정보와 다릅니다. 로그인을 통해 갱신해주세요."),
    NOT_FOUND_COVER_LETTER(false, 3100, "존재하지 않는 자소서입니다."),
    CAN_NOT_CREATE_COVER_LETTER_MORE_THAN_TODAY_LIMIT_COUNT(false, 3110, "하루에 자소서를 10개 이상 작성할 수 없습니다."),
    NOT_FOUND_COVER_LETTER_DECLARATION(false, 3150, "존재하지 않는 자소서 신고입니다."),
    ALREADY_PROCESSED_COVER_LETTER_DECLARATION(false, 3170, "이미 처리된 자소서 신고입니다."),
    NOT_FOUND_USER_INFO(false, 3200, "존재하지 않는 유저입니다."),
    ALREADY_DELETED_USER(false, 3250, "이미 탈퇴한 유저입니다."),
    USER_ROLE_IS_NOT_MENTEE(false, 3270, "멘티가 아닙니다."),
    NOT_FOUND_COMMENT(false, 3300, "존재하지 않는 코멘트입니다."),
    NOT_FOUND_ADOPTED_COMMENT(false, 3330, "해당 자소서에 채택된 코멘트가 존재하지 않습니다."),
    NOT_FOUND_COMMENT_DECLARATION(false, 3350, "존재하지 않는 코멘트 신고입니다."),
    ALREADY_PROCESSED_COMMENT_DECLARATION(false, 3370, "이미 처리된 코멘트 신고입니다."),
    NOT_FOUND_COVER_LETTER_CATEGORY(false, 3400, "존재하지 않는 자소서 종류입니다."),
    CAN_NOT_ADOPT_COMMENT_MORE_THAN_ONE(false, 3500, "코멘트는 하나만 채택할 수 있습니다."),
    DO_NOT_HAVE_PERMISSION(false, 3600, "권한이 없습니다."),
    ALREADY_DELETED_COVER_LETTER(false, 3700, "이미 삭제된 자소서입니다."),
    ALREADY_DELETED_COMMENT(false, 3800, "이미 삭제된 코멘트입니다."),
    NOT_FOUND_TEMPORARY_COVER_LETTER(false, 3900, "존재하지 않는 임시 자소서입니다."),
    FOUND_COVER_LETTER_TYPE_IS_NOT_WRITING(false, 3930, "작성중인 임시 자소서가 아닙니다."),
    FOUND_COVER_LETTER_TYPE_IS_NOT_COMPLETING(false, 3960, "완성중인 임시 자소서가 아닙니다."),
    ALREADY_EXIST_CHANGE_ROLE_REQUEST(false, 3970, "아직 처리되지 않은 역할 변경 신청이 존재합니다."),
    ALREADY_DELETED_TEMPORARY_COVER_LETTER(false, 4000, "이미 삭제된 임시 자소서입니다.");

    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
