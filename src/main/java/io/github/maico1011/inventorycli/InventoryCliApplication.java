package io.github.maico1011.inventorycli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InventoryCliApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                InventoryCliApplication.class,
                args
        );
    }
}