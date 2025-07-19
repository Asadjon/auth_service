package org.cyber_pantera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class Application {
    public static void main(String[] args) {
        System.out.println("Hello and welcome!");
        SpringApplication.run(Application.class, args);
    }
}