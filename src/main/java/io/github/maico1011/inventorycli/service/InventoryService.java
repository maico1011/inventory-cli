package io.github.maico1011.inventorycli.service;

import io.github.maico1011.inventorycli.model.Product;
import io.github.maico1011.inventorycli.repository.InventoryRepository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class InventoryService {

    private final InventoryRepository repository;
    private int nextId;

    public InventoryService(InventoryRepository repository) {
        this.repository = repository;

        int maxId = repository.findAll().stream()
                .mapToInt(Product::getId)
                .max()
                .orElse(0);

        if (maxId == Integer.MAX_VALUE) {
            throw new IllegalStateException("商品IDの上限に達しています。");
        }
        this.nextId = maxId + 1;
    }

    public Product registerProduct(String productName, int stock) {

        if (nextId == Integer.MAX_VALUE) {
            throw new IllegalStateException("商品IDの上限に達しています。");
        }

        Product product = new Product(nextId, productName, stock);

        repository.save(product);
        nextId++;

        return product;
    }

    public Collection<Product> getAllProducts() {
        return repository.findAll();
    }

    public Product getProduct(int id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "商品ID：" + id + " は存在しません"
                        ));
    }

    public void addStock(int id, int quantity) {
        Product product = getProduct(id);
        product.addStockCount(quantity);
        repository.save(product);
    }

    public void removeStock(int id, int quantity) {
        Product product = getProduct(id);
        product.removeStockCount(quantity);
        repository.save(product);
    }

}
