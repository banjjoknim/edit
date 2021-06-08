package com.app.edit.config;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class EditRestControllerAdvice {

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public BaseResponse missingParameterHandler() {
        return new BaseResponse(BaseResponseStatus.REQUEST_PARAMETER_MISSING);
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public BaseResponse argumentTypeMismatchHandler() {
        return new BaseResponse(BaseResponseStatus.REQUEST_PARAMETER_MISMATCH);
    }

    @ExceptionHandler(value = BaseException.class)
    public BaseResponse baseHandler(BaseException e) {
        return new BaseResponse(e.getStatus());
    }

    /*
     * @RequestBody @Valid 적용된 요청 객체 필드 검증
     **/
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public BaseResponse<List<String>> methodArgumentNotValidHandler(MethodArgumentNotValidException e) {
        List<String> errorMessages = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach(objectError -> errorMessages.add(objectError.getDefaultMessage()));
        return new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR, errorMessages);
    }
}