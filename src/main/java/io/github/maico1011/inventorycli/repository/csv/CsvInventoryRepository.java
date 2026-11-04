package io.github.maico1011.inventorycli.repository.csv;

import io.github.maico1011.inventorycli.model.Product;
import io.github.maico1011.inventorycli.repository.InventoryRepository;

import java.nio.file.Path;
import java.util.*;

public class CsvInventoryRepository implements InventoryRepository {


    // CSVから読み込んだ商品を保持する
    private final Map<Integer, Product> products;

    // CSVの保存先
    private final Path filePath;

    // コンストラクタ
    public CsvInventoryRepository(Path filePath) {
        this.filePath = filePath;
        this.products = new LinkedHashMap<>();
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public Optional<Product> findById(int id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public void save(Product product) {
        // ① productがnullでないか確認

        // ② 商品IDが1以上であることを確認

        // ③ productsに商品を追加・更新
    }

    // deleteById()
}
