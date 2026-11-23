package io.github.maico1011.inventorycli.exception;

/**
 * 指定した商品IDの商品が存在しないことを表す例外。
 * 既存CLIの IllegalArgumentException による例外処理との互換性を保つため、
 * IllegalArgumentException を継承する。
 */
public class ProductNotFoundException extends IllegalArgumentException {

    private final int productId;

    public ProductNotFoundException(int productId) {
        super("商品ID " + productId + " は存在しません");
        this.productId = productId;
    }

    public int getProductId() {
        return productId;
    }
}
