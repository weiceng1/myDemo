package com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.demo"})
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MainApplication.class);
        application.run(args);
    }
}
