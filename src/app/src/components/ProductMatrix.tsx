import { useEffect, useState } from "react";
import ProductCard from "./ProductCard";
import Subheader from "./Subheader";
import { Product } from "../utils/types";
import fetchAndDecode, { ServerUrl } from "../utils/utils";


export interface ProductMatrixProps {

}

export default function ProductMatrix({}: ProductMatrixProps) {
    const [products, setProducts] = useState<Product[]>([])

    useEffect(() => {
       fetchAndDecode<{products: Product[]}>(ServerUrl.Products,
        data => {
            const fetchedProducts = data.products
            setProducts(fetchedProducts)
        }) 
    },[])
  
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