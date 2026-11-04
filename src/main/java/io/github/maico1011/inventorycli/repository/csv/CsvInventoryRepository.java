package io.github.maico1011.inventorycli.repository.csv;

import io.github.maico1011.inventorycli.model.Product;
import io.github.maico1011.inventorycli.repository.InventoryRepository;

import java.nio.file.Path;
import java.util.Map;

public class CsvInventoryRepository implements InventoryRepository {
    // CSVから読み込んだ商品を保持する
    private final Map<Integer, Product> products;

    // CSVの保存先
    private final Path filePath;

    // コンストラクタ

    // findAll()

    // findById()

    // save()

    // deleteById()
}
