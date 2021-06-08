package com.app.edit.config;

import org.springframework.data.domain.Sort;

import static com.app.edit.config.Constant.ONE;
import static com.app.edit.config.Constant.ZERO;

public class PageRequest {

    public static org.springframework.data.domain.PageRequest of(int page, int size, Sort sort) {
        page = setPage(page);
        return org.springframework.data.domain.PageRequest.of(page, size, sort);
    }

    public static org.springframework.data.domain.PageRequest of(int page, int size) {
        page = setPage(page);
        return org.springframework.data.domain.PageRequest.of(page, size);
    }

    private static int setPage(int page) {
        if (page <= ONE) {
            return ZERO;
        }
        return page - ONE;
    }
}
