package com.manejocaatingueiro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ManejoCaatingueiroApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManejoCaatingueiroApplication.class, args);
    }
}
