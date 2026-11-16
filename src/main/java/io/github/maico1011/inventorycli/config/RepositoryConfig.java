package io.github.maico1011.inventorycli.config;

import io.github.maico1011.inventorycli.repository.InventoryRepository;
import io.github.maico1011.inventorycli.repository.csv.CsvInventoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class RepositoryConfig {

    @Bean
    public InventoryRepository inventoryRepository() {
        Path path = Path.of("data", "products.csv");

        return new CsvInventoryRepository(path);
    }
}