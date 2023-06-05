package category;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private int idCategory;
    private String name;
    private String description;
    private List<Subcategory> subcategories;

    public Category() {
    }

    public Category(int idCategory, String name, String description) {
        this.idCategory = idCategory;
        this.name = name;
        this.description = description;
        this.subcategories = new ArrayList<>();
    }

    public Category(Category other) {
        this.idCategory = other.idCategory;
        this.name = other.name;
        this.description = other.description;
        this.subcategories = new ArrayList<>(other.subcategories);
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
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

    public List<Subcategory> getSubcategories() {
        return subcategories;
    }

    // TODO: if this method is need, make it appropiate. Else, remove.
    public void setSubcategories(List<Subcategory> subcategories) {
        this.subcategories = new ArrayList<>(subcategories);
    }

    public void addSubcategory(Subcategory subcategory) {
        this.subcategories.add(subcategory);
    }

    public void removeSubcategory(Subcategory subcategory) {
        this.subcategories.remove(subcategory);
    }
}
