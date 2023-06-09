import { useEffect, useState } from "react";
import ProductCard from "./ProductCard";
import Subheader from "./Subheader";
import { Product } from "../utils/types";
import fetchAndDecode, { ServerRoute } from "../utils/utils";


export interface ProductMatrixProps {
    idCategory?: number
    idSubcategory?: number
}

export default function ProductMatrix({idCategory, idSubcategory}: ProductMatrixProps) {
    const [products, setProducts] = useState<Product[]>([])

    useEffect(() => {
        let params = { }; // TODO: import parameter names?
        if (idCategory) params = {...params, "category": idCategory.toString() }
        if (idSubcategory) params = {...params, "subcategory": idSubcategory.toString() }
        
        const queryString = new URLSearchParams(params).toString();
        const requestUrl = `${ServerRoute.AllProducts}?${queryString}`
        fetchAndDecode<{products: Product[]}>(requestUrl,
        data => {
            const fetchedProducts = data.products
            fetchedProducts.forEach((product) => {
                product.date = new Date(product.timestamp)
            })
            setProducts(fetchedProducts)
        }) 
    },[idCategory, idSubcategory])
  
    return (
        <div className="main-content">
            <Subheader />
            <div className="products-grid">
                {products.map(product => (
                    <ProductCard key={product.name} product={product} />
                ))} 
            </div>
        </div>
    );
  }