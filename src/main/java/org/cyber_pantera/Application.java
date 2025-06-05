package org.cyber_pantera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        System.out.println("Hello and welcome!");
        SpringApplication.run(Application.class, args);
    }
}