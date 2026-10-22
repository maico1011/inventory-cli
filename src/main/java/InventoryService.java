import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class InventoryService {

    private final Map<Integer, Product > products = new LinkedHashMap<>();
    private int nextId = 0;
    public Product registerProduct(String productName, int stock) {
        Product product = new Product(nextId, productName, stock);
        products.put(nextId++, product);
        return product;
    }

    public Collection<Product> getAllProducts() {
        return java.util.List.copyOf(products.values());
    }

    public Product getProduct(int id) {
        Product product = products.get(id);

        if(product == null ) {
            throw new IllegalArgumentException("商品ID：" + id + " は存在しません");
        }

        return product;
    }

    public void addStock(int id, int quantity) {
        Product product = getProduct(id);
        product.addStockCount(quantity);
    }

    public void removeStock(int id, int quantity) {
        Product product = getProduct(id);
        product.removeStockCount(quantity);
    }

    // csv読み込み時のデータ置き換え
    public void replaceAll(Collection<Product> loadedProducts) {
        Map<Integer, Product> newProducts = new LinkedHashMap<>();
        int maxId = 0;

        for (Product product : loadedProducts) {
            int id = product.getId();

            if (id <= 0) {
                throw new IllegalArgumentException("商品IDは1以上にしてください。");
            }

            if (newProducts.putIfAbsent(id, product) != null) {
                throw new IllegalArgumentException("商品IDが重複しています: " + id);
            }

            maxId = Math.max(maxId, id);
        }

        if (maxId == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("商品IDの上限に達しています。");
        }

        products.clear();
        products.putAll(newProducts);
        nextId = maxId + 1;
    }

}
