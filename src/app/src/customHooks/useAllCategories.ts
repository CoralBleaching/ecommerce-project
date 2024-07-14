import { useEffect, useState } from "react"
import categoriesData from "../../../../database/json/Category.json"
import subcategoriesData from "../../../../database/json/Subcategory.json"
import { Category } from "../utils/types"

export default function useCountryInfo() {
  const [categoryData, setCategoryData] = useState<Category[]>([])

  useEffect(() => {
    const transformedCategoryData = categoriesData.map((category) => {
      const subcategories = subcategoriesData
        .filter(
          (subcategory) => subcategory.id_category === category.id_category
        )
        .map((subcategory) => ({
          name: subcategory.name,
          idSubcategory: subcategory.id_subcategory,
          description: subcategory.description,
        }))

      return {
        name: category.name,
        idCategory: category.id_category,
        description: category.description,
        subcategories,
      }
    })

    setCategoryData(transformedCategoryData)
  }, [])

  return categoryData
}
