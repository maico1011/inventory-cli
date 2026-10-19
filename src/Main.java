import java.util.Scanner;

//TIP コードを<b>実行</b>するには、<shortcut actionId="Run"/> を押すか
// ガターの <icon src="AllIcons.Actions.Execute"/> アイコンをクリックします。
public class Main {
    public static void main(String[] args) {
        //TIP ハイライトされたテキストにキャレットがある状態で <shortcut actionId="ShowIntentionActions"/> を押すと
        // IntelliJ IDEA によるその修正案を確認できます。
    Scanner sc = new Scanner(System.in);
    InventoryService service = new InventoryService();

        while(true){
            System.out.println("\n 在庫管理システム");
            System.out.println("1.　商品一覧");
            System.out.println("2.　商品登録");
            System.out.println("3.　入庫");
            System.out.println("4.　出庫");
            System.out.println("0.　終了");

            int choice = readInt(sc,"操作を選択してください：");

            try{
                switch(choice){
                    case 1 -> showProducts(service);
                    case 2 -> registerProduct(sc, service);
                    case 3 -> changeStock(sc, service, true);
                    case 4 -> changeStock(sc, service, false);
                    case 0 -> {
                        System.out.println("終了します。");
                        sc.close();
                        return;
                    }
                    default -> System.out.println("無効な操作です");
                }
            } catch (IllegalArgumentException  e) {
                System.out.println("エラー: " + e.getMessage());
            } catch (ArithmeticException e) {
                System.out.println("エラー: 在庫数の上限を超えました。");
            }
        }
    }

    private static int readInt(Scanner scanner, String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine();

            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("整数を入力してください。");
            }
        }
    }

    private static void showProducts(InventoryService service) {
        if (service.getAllProducts().isEmpty()) {
            System.out.println("登録された商品はありません。");
            return;
        }

        for (Product product : service.getAllProducts()) {
            System.out.printf(
                    "ID: %d / 商品名: %s / 在庫: %d%n",
                    product.getId(),
                    product.getName(),
                    product.getStockCount()
            );
        }
    }

    private static void registerProduct(
            Scanner scanner, InventoryService service
    ) {
        System.out.print("商品名: ");
        String name = scanner.nextLine();

        int stock = readInt(scanner, "初期在庫数: ");
        Product product = service.registerProduct(name, stock);

        System.out.println(
                "商品を登録しました。ID: " + product.getId()
        );
    }

    private static void changeStock(
            Scanner scanner, InventoryService service, boolean isAdd
    ) {
        int id = readInt(scanner, "商品ID: ");
        int quantity = readInt(
                scanner, isAdd ? "入庫数: " : "出庫数: "
        );

        if (isAdd) {
            service.addStock(id, quantity);
        } else {
            service.removeStock(id, quantity);
        }

        System.out.println(
                (isAdd ? "入庫" : "出庫")
                        + "しました。現在の在庫: "
                        + service.getProduct(id).getStockCount()
        );
    }

}