package com.alvanklaveren;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

// THIS COMMENT IS ADDED TO SHOW HOW A FIX ON STABLE BRANCH CAN BE PATCHED ON MASTER BRANCH
// SO LET ME KNOW WHEN YOU'RE GOOD TO GO, I SAID "HOLD UP" ("WAIT A MINUTE")
//@SpringBootApplication
//public class Application {
//
//    public static void main(String[] args) {
//        SpringApplication.run(Application.class, args);
//    }
//}

@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}