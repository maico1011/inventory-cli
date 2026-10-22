import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryServiceTest {

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService();
    }

    @Test
    void 登録した商品を検索できる() {

        Product registered = inventoryService.registerProduct("テストノート", 10);
        Product found = inventoryService.getProduct(registered.getId());

        assertAll(
                () -> assertEquals(1, found.getId()),
                () -> assertEquals("テストノート", found.getName()),
                () -> assertEquals(10, found.getStockCount())
        );
    }

    @Test
    void 存在しない商品IDで検索するとエラー() {

        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.getProduct(999)
        );
    }

    @Test
    void 商品を複数登録するとIDが連番で発行される() {

        Product first = inventoryService.registerProduct("テストノート", 10);
        Product second = inventoryService.registerProduct("テストペン", 20);
        Product third = inventoryService.registerProduct("テスト消しゴム", 30);

        assertAll(
                () -> assertEquals(1, first.getId()),
                () -> assertEquals(2, second.getId()),
                () -> assertEquals(3, third.getId())
        );
    }

    @Test
    void 一方の商品の出庫は他方の商品に影響しない() {

        Product notebook = inventoryService.registerProduct("テストノート", 10);
        Product pen = inventoryService.registerProduct("テストペン", 20);

        inventoryService.removeStock(notebook.getId(), 5);

        assertAll(
                () -> assertEquals(5, notebook.getStockCount()),
                () -> assertEquals(20, pen.getStockCount())
        );
    }

    @Test
    void 商品登録失敗時にIDが採番されない() {

        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.registerProduct("", 10)
        );

        Product product = inventoryService.registerProduct("テストノート", 10);

        assertEquals(1, product.getId());
    }

    @Test
    void 不正な商品一覧への置き換えの場合は元データは変更されない() {

        Product registered = inventoryService.registerProduct("テストノート", 10);

        // IDが重複した不正な商品一覧
        List<Product> invalidProducts = List.of(
                new Product(2, "不正ペン", 20),
                new Product(2, "不正消しゴム", 30)
        );

        // 不正な一覧への置き換えを拒否
        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.replaceAll(invalidProducts)
        );

        // 置き換え前の商品が保持されていることを確認
        assertAll(
                () -> assertEquals(1, inventoryService.getAllProducts().size()),
                () -> assertEquals("テストノート", inventoryService.getProduct(1).getName()),
                () -> assertEquals(10, inventoryService.getProduct(1).getStockCount())
        );

    }
}
