import { useState, useEffect } from "react"
import productsData from "../../../../database/json/Product.json"
import categoriesData from "../../../../database/json/Category.json"
import subcategoriesData from "../../../../database/json/Subcategory.json"
import pricesData from "../../../../database/json/Price.json"
import { Product } from "../utils/types"

export default function useAllProducts(
  idCategory?: number,
  idSubcategory?: number,
  searchText?: string,
  orderBy?: string,
  pageNumber?: number,
  resultsPerPage?: number
) {
  const [products, setProducts] = useState<Product[]>([])

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

    const products: Product[] = filteredData.map((item: any) => ({
      idProduct: item.id_product,
      idPicture: item.id_picture,
      name: item.name,
      description: item.description,
      stock: item.stock,
      hotness: item.hotness,
      timestamp: item.timestamp,
      price:
        pricesData.find((pr) => pr.id_product === item.id_product)?.value || 0,
      category:
        categoriesData.find(
          (cat) =>
            cat.id_category ===
            subcategoriesData.find(
              (sub) => sub.id_subcategory === item.id_subcategory
            )?.id_category
        )?.name || "",
      subcategory:
        subcategoriesData.find(
          (sub) => sub.id_subcategory === item.id_subcategory
        )?.name || "",
      date: new Date(item.timestamp),
    }))

    if (orderBy) {
      products.sort((a, b) => {
        if (orderBy === "Hotness") {
          return b.hotness - a.hotness
        } else if (orderBy === "Price") {
          return a.price - b.price
        }
        return 0
      })
    }

    if (resultsPerPage !== undefined && pageNumber !== undefined) {
      const start = (pageNumber - 1) * resultsPerPage
      const end = start + resultsPerPage
      setProducts(products.slice(start, end))
    } else {
      setProducts(products)
    }
  }, [
    idCategory,
    idSubcategory,
    searchText,
    orderBy,
    pageNumber,
    resultsPerPage,
  ])

  return products
}
