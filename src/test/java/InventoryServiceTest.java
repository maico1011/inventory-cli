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
}
