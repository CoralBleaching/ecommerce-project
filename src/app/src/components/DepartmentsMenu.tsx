import { useEffect, useState } from "react";
import { Category } from "../utils/types";
import fetchAndDecode from "../utils/utils";

export interface DepartmentsMenuProps {

}

export default function DepartmentsMenu({}: DepartmentsMenuProps) {
  const [categories, setCategories] = useState<Category[]>([])

  useEffect(() => {
    fetchAndDecode<{categories: Category[]}>("http://localhost:8080/ecommerce-demo/Categories", data => {
      const fetchedCategories = data.categories;
      setCategories(fetchedCategories)
  })
  }, [])

  return (
    <div className="navbar">
      <h2>Departments</h2>
      {categories.map((category) => {
        return (
        <div key={category.idCategory}>
          <h3>{category.name}</h3>
          {category.subcategories.map((subcategory) => {
            return (
              <div key={subcategory.idSubcategory}>
                <span>{subcategory.name}</span>
              </div>
            )
          })
          }
        </div>
        )
      })}
    </div>
  );
}