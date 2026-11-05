package io.github.maico1011.inventorycli.repository.csv;

import io.github.maico1011.inventorycli.model.Product;
import io.github.maico1011.inventorycli.repository.InventoryRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CsvInventoryRepository implements InventoryRepository {

    // CSVから読み込んだ商品を保持する
    private final Map<Integer, Product> products;
    // CSVの保存先
    private final Path filePath;
    private final CsvInventoryFileStore fileStore;

    public CsvInventoryRepository(Path filePath) {
        this.filePath = filePath;
        this.products = new LinkedHashMap<>();
        this.fileStore = new CsvInventoryFileStore();

        if (Files.exists(filePath)) {
            // InventoryRepositoryインターフェースは、IOExceptionを想定していないため、
            // 元の例外を原因として保持しつつIllegalStateExceptionへ変換
            try {
                List<Product> loadedProducts = fileStore.load(filePath);

                for (Product product : loadedProducts) {
                    products.put(product.getId(), product);
                }

            } catch (IOException e) {
                throw new IllegalStateException(
                        "CSVファイルの読み込みに失敗しました", e
                );
            }
        }
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

        Objects.requireNonNull(product, "商品がnullです");

        if (product.getId() <= 0) {
            throw new IllegalArgumentException(
                    "商品IDは1以上である必要があります"
            );
        }
        // 元のproductsはロールバック用に保持し、更新用productsを生成
        Map<Integer, Product> updatedProducts =
                new LinkedHashMap<>(products);

        updatedProducts.put(product.getId(), product);
        persistAndApply(updatedProducts);
    }

    @Override
    public boolean deleteById(int id) {
        if(!products.containsKey(id)){
            return false;
        }
        // 元のproductsはロールバック用に保持し、更新用productsを生成
        Map<Integer, Product> updatedProducts =
                new LinkedHashMap<>(products);

        if(updatedProducts.remove(id) != null){
            persistAndApply(updatedProducts);
            return true;
        }
        return false;
    }

    private void persistAndApply(
            Map<Integer, Product> updatedProducts) {

        try {
            fileStore.save(filePath, updatedProducts.values());
        } catch (IOException e) {
            throw new IllegalStateException(
                    "CSVファイルの保存に失敗しました", e
            );
        }

        products.clear();
        products.putAll(updatedProducts);
    }

}
