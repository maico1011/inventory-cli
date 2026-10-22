
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CsvInventoryRepository {

    private static final String HEADER = "id,name,stockCount";

    public List<Product> load(Path path) throws IOException {
        List<String> lines = Files.readAllLines(
                path, StandardCharsets.UTF_8
        );

        if (lines.isEmpty() || !HEADER.equals(lines.get(0))) {
            throw new IllegalArgumentException(
                    "CSVのヘッダーが不正です。"
            );
        }

        List<Product> products = new ArrayList<>();
        Set<Integer> usedIds = new HashSet<>();

        for (int i = 1; i < lines.size(); i++) {
            String[] columns = lines.get(i).split(",", -1);

            if (columns.length != 3) {
                throw new IllegalArgumentException(
                        "列の数が不正です。"
                );
            }

            try {
                int id = Integer.parseInt(columns[0]);
                String name = columns[1];
                int stockCount = Integer.parseInt(columns[2]);

                if (id <= 0 || !usedIds.add(id)) {
                    throw new IllegalArgumentException(
                            "商品IDが不正、または重複しています。"
                    );
                }

                if (name.contains("\"")) {
                    throw new IllegalArgumentException(
                            "商品名に使用できない文字が含まれています。"
                    );
                }

                products.add(new Product(id, name, stockCount));

            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        (i + 1) + "行目: " + e.getMessage(), e
                );
            }
        }

        return products;
    }
}
