package sale;

import java.util.List;

public record Sale(
        int saleId,
        int userId,
        String timestamp,
        List<Integer> quantities,
        List<Integer> priceIds) {
}
