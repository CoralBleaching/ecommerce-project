package product;

public enum Order {
    PriceAscending("price-ascending"),
    PriceDescending("price-descending"),
    Hotness("hotness"),
    Newest("newest"),
    Oldest("oldest");

    private final String value;

    Order(String value) {
        this.value = value;
    }

    public static Order getFromString(String orderStr) {
        for (Order order : Order.values()) {
            if (order.value.equals(orderStr)) {
                return order;
            }
        }
        return Hotness;
    }

    public String get() {
        return value;
    }
}
