import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {
    @Test
    void 商品を作成できる(){
        Product product = new Product(0, "テストノート", 10);

        assertAll(
            () -> assertEquals(0, product.getId()),
            () -> assertEquals("テストノート", product.getName()),
            () -> assertEquals(10, product.getStockCount())
        );
    }

    @Test
    void 商品名が空の場合エラー(){
        assertThrows(
            IllegalArgumentException.class,
            () -> new Product(0, "", 10)
        );
    }

    @Test
    void 初期在庫が負の場合はエラー(){
        assertThrows(
            IllegalArgumentException.class,
            () -> new Product(0, "テストノート", -1)
        );
    }

    @Test
    void 入庫すると在庫が増加(){
        Product product = new Product(0, "テストノート", 10);

        product.addStockCount(5);

        assertEquals(15, product.getStockCount());
    }

    @Test
    void 入庫数が０の場合はエラー(){
        Product product = new Product(0, "テストノート", 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> product.addStockCount(0)
        );

        // エラー後も在庫数に変化はない
        assertEquals(10, product.getStockCount());
    }

    @Test
    void 出庫すると在庫が減少(){
        Product product = new Product(0, "テストノート", 10);

        product.removeStockCount(5);

        assertEquals(5, product.getStockCount());
    }

    @Test
    void 在庫数が不足する場合はエラー(){
        Product product = new Product(0, "テストノート", 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> product.removeStockCount(20)
        );

        // エラー後も在庫数に変化はない
        assertEquals(10, product.getStockCount());
    }

    @Test
    void 在庫加算でオーバーフローするとエラー() {
        Product product = new Product(
                0, "テストノート", Integer.MAX_VALUE
        );

        assertThrows(
                ArithmeticException.class,
                () -> product.addStockCount(1)
        );

        assertEquals(Integer.MAX_VALUE, product.getStockCount());
    }


}
