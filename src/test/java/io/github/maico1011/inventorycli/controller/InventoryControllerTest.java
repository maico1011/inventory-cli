package io.github.maico1011.inventorycli.controller;

import io.github.maico1011.inventorycli.exception.ProductNotFoundException;
import io.github.maico1011.inventorycli.model.Product;
import io.github.maico1011.inventorycli.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Controller と ControllerAdvice のみを読み込むスライステスト。
// InventoryService はモックに置き換えるため、RepositoryConfig は読み込まれず
// data/products.csv の読み書きは発生しない。
@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryService inventoryService;

    @Test
    void 存在する商品IDを取得すると200と商品JSONを返す() throws Exception {
        when(inventoryService.getProduct(1))
                .thenReturn(new Product(1, "テストノート", 10));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("テストノート"))
                .andExpect(jsonPath("$.stockCount").value(10));
    }

    @Test
    void 存在しない商品IDを取得すると404とエラーJSONを返す() throws Exception {
        when(inventoryService.getProduct(9999))
                .thenThrow(new ProductNotFoundException(9999));

        mockMvc.perform(get("/api/products/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("PRODUCT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("商品ID 9999 は存在しません"));
    }

    @Test
    void 商品一覧を取得すると200と配列を返す() throws Exception {
        when(inventoryService.getAllProducts())
                .thenReturn(List.of(
                        new Product(1, "テストノート", 10),
                        new Product(2, "テストペン", 20)
                ));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("テストノート"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("テストペン"));
    }

    @Test
    void ProductNotFoundException以外のIllegalArgumentExceptionは404に変換されない() {
        IllegalArgumentException thrown =
                new IllegalArgumentException("その他の入力エラー");
        when(inventoryService.getProduct(1)).thenThrow(thrown);

        // ハンドラが存在しないため、例外はレスポンスに変換されず MockMvc から送出される。
        // ラッパーの型には依存せず、原因の連鎖に元の例外が含まれることを検証する。
        Exception e = assertThrows(
                Exception.class,
                () -> mockMvc.perform(get("/api/products/1"))
        );

        assertTrue(
                causeChainContains(e, thrown),
                "送出された例外の原因に元の IllegalArgumentException が含まれていません: " + e
        );
    }

    private static boolean causeChainContains(Throwable t, Throwable target) {
        for (Throwable cur = t; cur != null; cur = cur.getCause()) {
            if (cur == target) {
                return true;
            }
            if (cur.getCause() == cur) {
                break;
            }
        }
        return false;
    }
}
