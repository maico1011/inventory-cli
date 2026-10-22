import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryServiceTest {
    @Test
    void 登録した商品を検索できる() {
        InventoryService inventoryService = new InventoryService();

        Product retistered = inventoryService.registerProduct("テストノート", 10);
        Product found = inventoryService.getProduct(retistered.getId());

        assertAll(
                () -> assertEquals(0, found.getId()),
                () -> assertEquals("テストノート", found.getName()),
                () -> assertEquals(10, found.getStockCount())
        );
    }

    @Test
    void 存在しない商品IDで検索するとエラー() {
        InventoryService inventoryService = new InventoryService();

        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.getProduct(999)
        );
    }

    @Test
    void 商品を複数登録するとIDが連番で発行される() {
        InventoryService service = new InventoryService();

        Product first = service.registerProduct("テストノート", 10);
        Product second = service.registerProduct("テストペン", 20);
        Product third = service.registerProduct("テスト消しゴム", 30);

        assertAll(
                () -> assertEquals(0, first.getId()),
                () -> assertEquals(1, second.getId()),
                () -> assertEquals(2, third.getId())
        );
    }

    @Test
    void 一方の商品の出庫は他方の商品に影響しない() {
        InventoryService service = new InventoryService();

        Product notebook = service.registerProduct("テストノート", 10);
        Product pen = service.registerProduct("テストペン", 20);

        service.removeStock(notebook.getId(), 5);

        assertAll(
                () -> assertEquals(5, notebook.getStockCount()),
                () -> assertEquals(20, pen.getStockCount())
        );
    }

    @Test
    void 商品登録失敗時にIDが採番されない() {
        InventoryService service = new InventoryService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registerProduct("", 10)
        );

        Product product = service.registerProduct("テストノート", 10);

        assertEquals(0, product.getId());
    }
}
