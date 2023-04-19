package com.demo.service;

import com.demo.aspect.EnableAutoLog;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @EnableAutoLog
    public String test(String str) {
        return str.substring(0,2);
    }
}
