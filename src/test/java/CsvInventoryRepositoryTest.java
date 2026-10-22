
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvInventoryRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void CSVから商品を読み込める() throws Exception {
        Path path = tempDir.resolve("products.csv");

        Files.writeString(
                path,
                "id,name,stockCount\n1,ノート,10\n2,ペン,20\n",
                StandardCharsets.UTF_8
        );

        CsvInventoryRepository repository =
                new CsvInventoryRepository();

        List<Product> products = repository.load(path);

        assertAll(
                () -> assertEquals(2, products.size()),
                () -> assertEquals(1, products.get(0).getId()),
                () -> assertEquals("ノート", products.get(0).getName()),
                () -> assertEquals(10, products.get(0).getStockCount()),
                () -> assertEquals(2, products.get(1).getId()),
                () -> assertEquals("ペン", products.get(1).getName())
        );
    }

    @Test
    void 商品IDが重複しているCSVは読み込めない() throws Exception {
        Path path = tempDir.resolve("duplicate.csv");

        Files.writeString(
                path,
                "id,name,stockCount\n1,ノート,10\n1,ペン,20\n",
                StandardCharsets.UTF_8
        );

        CsvInventoryRepository repository =
                new CsvInventoryRepository();

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.load(path)
        );
    }

    @Test
    void 不正な在庫数のCSVは読み込めない() throws Exception {
        Path path = tempDir.resolve("invalid.csv");

        Files.writeString(
                path,
                "id,name,stockCount\n1,ノート,-5\n",
                StandardCharsets.UTF_8
        );

        CsvInventoryRepository repository =
                new CsvInventoryRepository();

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.load(path)
        );
    }


    @Test
    void 商品情報をCSVに保存できる() throws IOException {
        CsvInventoryRepository repository =
                new CsvInventoryRepository();

        Path file = tempDir.resolve("products.csv");

        List<Product> products = List.of(
                new Product(1, "テストノート", 10),
                new Product(2, "テストペン", 20)
        );

        repository.save(file, products);

        List<String> lines = Files.readAllLines(
                file, StandardCharsets.UTF_8
        );

        assertEquals(
                List.of(
                        "id,name,stockCount",
                        "1,テストノート,10",
                        "2,テストペン,20"
                ),
                lines
        );
    }

    @Test
    void 保存した商品を読み込んで復元できる() throws IOException {
        CsvInventoryRepository repository =
                new CsvInventoryRepository();

        Path file = tempDir.resolve("products.csv");

        List<Product> original = List.of(
                new Product(1, "ノート", 10),
                new Product(3, "消しゴム", 5)
        );

        repository.save(file, original);

        List<Product> loaded = repository.load(file);

        assertAll(
                () -> assertEquals(2, loaded.size()),
                () -> assertEquals(1, loaded.get(0).getId()),
                () -> assertEquals("ノート", loaded.get(0).getName()),
                () -> assertEquals(10, loaded.get(0).getStockCount()),
                () -> assertEquals(3, loaded.get(1).getId()),
                () -> assertEquals("消しゴム", loaded.get(1).getName()),
                () -> assertEquals(5, loaded.get(1).getStockCount())
        );
    }

    @Test
    void カンマを含む商品名は保存できない() {
        CsvInventoryRepository repository =
                new CsvInventoryRepository();

        Path file = tempDir.resolve("products.csv");

        List<Product> products = List.of(
                new Product(1, "ノート,赤", 10)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.save(file, products)
        );

        assertFalse(Files.exists(file));
    }

}
