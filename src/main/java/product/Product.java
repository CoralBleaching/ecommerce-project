package product;

public class Product {
    private int idProduct,
            idPicture,
            stock,
            hotness;
    private float price;
    private String category,
            subcategory,
            name,
            description,
            timestamp;

    public Product() {
    }

    public Product(int idProduct, int idPicture, int stock, int hotness, String timestamp, float price,
            String category, String subcategory, String name, String description) {
        this.idProduct = idProduct;
        this.idPicture = idPicture;
        this.stock = stock;
        this.hotness = hotness;
        this.timestamp = timestamp;
        this.price = price;
        this.category = category;
        this.subcategory = subcategory;
        this.name = name;
        this.description = description;
    }

    public Product(Product other) {
        this.idProduct = other.idProduct;
        this.idPicture = other.idPicture;
        this.stock = other.stock;
        this.hotness = other.hotness;
        this.timestamp = other.timestamp;
        this.price = other.price;
        this.category = other.category;
        this.subcategory = other.subcategory;
        this.name = other.name;
        this.description = other.description;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public int getIdPicture() {
        return idPicture;
    }

    public int getStock() {
        return stock;
    }

    public int getHotness() {
        return hotness;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public float getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public void setIdPicture(int idPicture) {
        this.idPicture = idPicture;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setHotness(int hotness) {
        this.hotness = hotness;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Product [idProduct=" + idProduct + ", idPicture=" + idPicture + ", stock=" + stock + ", hotness="
                + hotness + ", price=" + price + ", category=" + category + ", subcategory=" + subcategory + ", name="
                + name + ", description=" + description + ", timestamp=" + timestamp + "]";
    }

}