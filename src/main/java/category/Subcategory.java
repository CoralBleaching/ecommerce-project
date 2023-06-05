package category;

public class Subcategory {
    private int idSubcategory;
    private String name;
    private String description;

    public Subcategory() {
    }

    public Subcategory(int idSubcategory, String name, String description) {
        this.idSubcategory = idSubcategory;
        this.name = name;
        this.description = description;
    }

    public Subcategory(Subcategory other) {
        this.idSubcategory = other.idSubcategory;
        this.name = other.name;
        this.description = other.description;
    }

    public int getIdSubcategory() {
        return idSubcategory;
    }

    public void setIdSubcategory(int idSubcategory) {
        this.idSubcategory = idSubcategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
