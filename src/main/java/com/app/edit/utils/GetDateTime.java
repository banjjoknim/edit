package com.app.edit.utils;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class GetDateTime {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DateTimeFormatter GetFormatter(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }
    public String getDataTime(){
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.format(formatter);
    }

    public String getCustomDataTime(String operation, Long number){
        if(operation.equals("plus")){
            return LocalDateTime.now().plusMinutes(number).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }else{
            return LocalDateTime.now().minusMinutes(number).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    public String getToday(){
        LocalDate date = LocalDate.now();
        DateTimeFormatter todayFormatter = DateTimeFormatter.ofPattern("yyyyMMdd ");
        return date.format(todayFormatter);
    }

}
