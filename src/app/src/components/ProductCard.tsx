import { Product } from "../utils/types"

export interface ProductCardProps {
    product: Product
}

export default function ProductCard({product}: ProductCardProps) {
    return (
        <div className="card">
            <ul>
                <li>{product.name}</li>
                <li>{product.price}</li>
                <li>{product.date?.toDateString()}</li>
            </ul>
            
        </div>
    )
}
