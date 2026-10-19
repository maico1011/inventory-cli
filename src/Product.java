public class Product {
    private final int id;
    private final String name;
    private int stockCount;
    public Product(int id, String name, int stockCount) {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("商品名は必須です");
        }
        if(stockCount < 0) {
            throw new IllegalArgumentException("在庫数は0以上にしてください");
        }
        this.name = name;
        this.id = id;
        this.stockCount = stockCount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStockCount() {
        return stockCount;
    }

    public void addStockCount(int quantity) {
        if(quantity <=0) {
            throw new IllegalArgumentException("入庫数は１以上にしてください。");
        }
        stockCount = Math.addExact(stockCount, quantity);
    }

    public void removeStockCount(int quantity) {
        if(quantity <=0) {
            throw new IllegalArgumentException("出庫数は１以上にしてください。");
        }
        if(quantity > stockCount) {
            throw new IllegalArgumentException("在庫が不足しています。");
        }
        stockCount -=  quantity;
    }
}
