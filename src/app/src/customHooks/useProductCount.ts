import { useState, useEffect } from "react"
import productsData from "../../../../database/json/Product.json"
import categoriesData from "../../../../database/json/Category.json"
import subcategoriesData from "../../../../database/json/Subcategory.json"

export default function useProductCount(
  idCategory?: number,
  idSubcategory?: number,
  searchText?: string
) {
  const [count, setCount] = useState<number | undefined>()

  useEffect(() => {
    let filteredData = productsData

    if (idCategory) {
      const categorySubcategories = subcategoriesData
        .filter((sub) => sub.id_category === idCategory)
        .map((sub) => sub.id_subcategory)
      filteredData = filteredData.filter((product) =>
        categorySubcategories.includes(product.id_subcategory)
      )
    }

    if (idSubcategory) {
      filteredData = filteredData.filter(
        (product) => product.id_subcategory === idSubcategory
      )
    }

    if (searchText) {
      const searchTerms = searchText.split(" ")
      filteredData = filteredData.filter((product) =>
        searchTerms.some(
          (term) =>
            product.name.includes(term) ||
            product.description.includes(term) ||
            categoriesData
              .find(
                (cat) =>
                  cat.id_category ===
                  subcategoriesData.find(
                    (sub) => sub.id_subcategory === product.id_subcategory
                  )?.id_category
              )
              ?.name.includes(term) ||
            subcategoriesData
              .find((sub) => sub.id_subcategory === product.id_subcategory)
              ?.name.includes(term)
        )
      )
    }

    setCount(filteredData.length)
  }, [idCategory, idSubcategory, searchText])

  return count
}
