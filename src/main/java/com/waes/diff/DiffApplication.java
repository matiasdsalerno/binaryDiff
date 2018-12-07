package com.waes.diff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.waes.diff.*"})
public class DiffApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiffApplication.class, args);
    }
}
