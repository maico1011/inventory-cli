package io.github.maico1011.inventorycli.repository;

import io.github.maico1011.inventorycli.model.Product;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository {

    List<Product> findAll();

    Optional<Product> findById(int id);

    void save(Product product);

    boolean deleteById(int id);
}
