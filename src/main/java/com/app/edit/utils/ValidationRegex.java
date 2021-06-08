package com.app.edit.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {

    /**
     * - 실명입력
     * - 한글/영어 한글자 이상
     * - 띄어쓰기 미포함
     * - 2글자 이상 10자 이내(이하)
     * @param testString
     * @return
     */
    public static boolean isRegexName(String testString){
        String regex = "^[가-힣a-zA-Z]{2,10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(testString);
        return matcher.find();
    }

    /**
     * - 한글,영문,숫자 6자 이내(이하)
     * - 띄어쓰기 미포함
     * - 최소 2글자 이상
     * @param
     */
    public static boolean isRegexNickName(String testString){
        String regex = "^[가-힣ㄱ-ㅎa-zA-Z0-9]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(testString);
        return matcher.find();
    }

    /**
     * - '-' 제외
     * - 띄어쓰기 미포함
     * - 숫자만 반영
     * - 11자 고정
     * @param testString
     */
    public static boolean isRegexPhoneNumber(String testString){
        String regex = "^01(?:0|1|[6-9])(\\d{3}|\\d{4})(\\d{4})$";//"^010?([0-9]{4})?([0-9]{4})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(testString);
        return matcher.find();
    }

    /**
     * - 띄어쓰기 미포함
     * - '@' 이메일 형식 필수
     * (email@naver.com)
     * - 이메일 아이디 영어 소문자
     * @param testString
     */
    public static boolean isRegexEmail(String testString){
        String regex = "^[a-zA-Z0-9._%-+]{1,256}+@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}+\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(testString);
        return matcher.find();
    }

    /**
     * - 문자(한글/영어),숫자,기호 중
     *   2가지 포함하여 작성
     * - 기호 :  ~`!@#$%\^&*()-+=
     * - 8자 이상 15자 이하(검토중)
     * @param testString
     */
    public static boolean isRegexPassword(String testString){
        String regex = "^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z~!`@#$%^&*()_+=])(?=.*[0-9!@#$%^&*]).{8,15}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(testString);
        return matcher.find();
    }

}
