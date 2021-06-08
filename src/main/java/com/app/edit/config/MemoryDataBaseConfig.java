package com.app.edit.config;

import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@Configuration
public class MemoryDataBaseConfig {

    @Bean
    public HashMap<String, String> AuthenticationRepository(){
        return new HashMap<>();
    }
}
