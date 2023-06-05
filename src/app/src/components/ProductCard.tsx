export interface Product {
    id: string
}

export interface ProductCardProps {
    id: string
}

export default function ProductCard({id}: ProductCardProps) {
    return (
        <div className="card">
            Product {id}
        </div>
    )
}
