import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class InventoryService {

    private final Map<Integer, Product > products = new LinkedHashMap<>();
    private int nextId = 1;
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

}
