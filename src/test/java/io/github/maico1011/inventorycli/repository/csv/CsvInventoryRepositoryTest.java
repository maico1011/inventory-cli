package io.github.maico1011.inventorycli.repository.csv;

import io.github.maico1011.inventorycli.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class CsvInventoryRepositoryTest {

    @TempDir
    Path tempDir;

    private CsvInventoryRepository repository;

    @BeforeEach
    void setUp() {
        repository = new CsvInventoryRepository(
                tempDir.resolve("products.csv")
        );
    }

    private void saveTestProduct() {
        repository.save(new Product(1, "テスト商品", 10));
    }

    @Test
    void 商品を保存できる() {

        Product product = new Product(1, "テスト商品", 10);
        repository.save(product);

        Product actual = repository.findById(1).orElseThrow();

        assertEquals(1, actual.getId());
        assertEquals("テスト商品", actual.getName());
    }

    @Test
    void 同じIDでsaveすると商品が更新される() {
        saveTestProduct();

        Product updateProduct = new Product(1, "テスト商品2", 20);

        repository.save(updateProduct);

        Product actual = repository.findById(1).orElseThrow();

        assertEquals(1, actual.getId());
        assertEquals("テスト商品2", actual.getName());
        assertEquals(20, actual.getStockCount());
    }

    @Test
    void nullをsaveするとNullPointerException発生() {
        assertThrows(NullPointerException.class, () -> {
            repository.save(null);
        });
    }

    @Test
    void IDが0以下の商品を保存するとIllegalArgumentExceptionが発生() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.save(new Product(0, "テストエラー商品", 10));
        });
    }

    @Test
    void 存在しないIDのfindByIdはOptionalemptyを返す() {
        Optional<Product> result = repository.findById(999);
        assertTrue(result.isEmpty());
    }

    @Test
    void 存在する商品IDへのdeleteByIdはtrueを返す() {
        saveTestProduct();
        assertTrue(repository.deleteById(1));
    }

    @Test
    void 存在しない商品IDへのdeleteByIdはfalseを返す() {
        saveTestProduct();
        assertFalse(repository.deleteById(999));
    }


    @Test
    void findAllで登録済みの全商品を取得できる() {
        saveTestProduct();
        repository.save(new Product(2, "テスト商品2", 20));

        List<Product> actual = repository.findAll();

        assertEquals(2, actual.size());

        // 順序は見ずに、期待したIDが全て取得されていることだけ確認
        Set<Integer> actualIds = actual.stream()
                .map(Product::getId)
                .collect(Collectors.toSet());
        assertEquals(Set.of(1, 2), actualIds);
    }

    @Test
    void 保存した商品を新しいRepositoryから取得できる() {
        Path path = tempDir.resolve("products.csv");

        CsvInventoryRepository first =
                new CsvInventoryRepository(path);

        first.save(new Product(1, "テスト商品", 10));

        CsvInventoryRepository second =
                new CsvInventoryRepository(path);

        Product actual = second.findById(1).orElseThrow();

        assertAll(
                () -> assertEquals(1, actual.getId()),
                () -> assertEquals("テスト商品", actual.getName()),
                () -> assertEquals(10, actual.getStockCount())
        );
    }

    @Test
    void 同じIDの商品を更新すると更新後の内容が復元できる() {
        Path path = tempDir.resolve("products.csv");

        CsvInventoryRepository first =
                new CsvInventoryRepository(path);

        first.save(new Product(1, "テスト商品1", 10));
        first.save(new Product(1, "テスト商品2", 20));

        CsvInventoryRepository second =
                new CsvInventoryRepository(path);

        Product actual = second.findById(1).orElseThrow();

        assertEquals("テスト商品2", actual.getName());
        assertEquals(20, actual.getStockCount());
    }

    @Test
    void 削除済みの商品は新しいリポジトリにも残らない() {
        Path path = tempDir.resolve("products.csv");

        CsvInventoryRepository first =
                new CsvInventoryRepository(path);

        first.save(new Product(1, "テスト商品1", 10));
        first.save(new Product(2, "テスト商品2", 20));

        assertTrue(first.deleteById(1));

        CsvInventoryRepository second =
                new CsvInventoryRepository(path);

        assertAll(
                // 削除した商品が存在しない
                () -> assertTrue(second.findById(1).isEmpty()),

                // 削除していない商品は残っている
                () -> assertTrue(second.findById(2).isPresent()),

                // 商品数が1件になっている
                () -> assertEquals(1, second.findAll().size())
        );
    }
    @Test
    void 存在しないIDを削除してもfalseになる() {
        repository.save(new Product(1, "テスト商品", 10));

        assertFalse(repository.deleteById(2));

        assertAll(
                () -> assertEquals(1, repository.findAll().size()),
                () -> assertTrue(repository.findById(1).isPresent())
        );
    }

    @Test
    void 不正文字列によるCSV保存失敗時にMapが変更されない() {
        repository.save(new Product(1, "正常な商品", 10));

        assertThrows(IllegalArgumentException.class, () -> {
            repository.save(new Product(2, "不正,な商品", 20));
        });

        assertAll(
                () -> assertEquals(1, repository.findAll().size()),
                () -> assertTrue(repository.findById(1).isPresent()),
                () -> assertTrue(repository.findById(2).isEmpty())
        );
    }
    @Test
    void 不正なディレクトリによるCSV書き込み失敗時にMapが変更されない() throws IOException {
        Path path = tempDir.resolve("products.csv");

        CsvInventoryRepository repository =
                new CsvInventoryRepository(path);

        Files.createDirectory(path);

        assertThrows(IllegalStateException.class, () -> {
            repository.save(new Product(1, "テスト商品", 10));
        });

        assertTrue(repository.findAll().isEmpty());
    }
}
