import ProductCard, { Product } from "./ProductCard";
import Subheader from "./Subheader";


export interface ProductMatrixProps {

}

export default function ProductMatrix({}: ProductMatrixProps) {
    // Sample product data
    const products: Product[] = [];

    for (let i = 0; i < 13; i++) {
        products.push({id: i.toString()})
    }
  
    return (
        <div className="main-content">
            <Subheader />
            <div className="products-grid">
                {products.map(product => (
                    <ProductCard key={product.id} {...product} />
                ))} 
            </div>
        </div>
    );
  }