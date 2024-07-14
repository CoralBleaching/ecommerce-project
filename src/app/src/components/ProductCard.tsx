import { Product } from "../utils/types"
import usePictureById from "../customHooks/usePictureById"

export enum CardType {
  ProductMatrix,
  Cart,
}

export interface ProductCardProps {
  product: Product
  type: CardType
  quantity?: number
  onAddToCart: (product: Product, quantity: number) => void
  onRemoveFromCart: (productId: number) => void
}

export default function ProductCard({
  product,
  type,
  quantity,
  onAddToCart,
  onRemoveFromCart,
}: ProductCardProps) {
  const picture = usePictureById(product.idPicture)

  return (
    <div className="card">
      <ul>
        <li>{product.name}</li>
        <li>{`$${product.price}`}</li>
        {/* <li>{product.date?.toDateString()}</li> */}

        {type === CardType.ProductMatrix ? (
          <li>{product.description}</li>
        ) : (
          <div />
        )}
        {type === CardType.Cart && quantity ? <li>{quantity}</li> : <div />}
      </ul>
      <div>
        <img
          className="card-image"
          src={`data:image/jpeg;base64,${picture}`}
          alt="Image"
        />
        {type === CardType.ProductMatrix ? (
          <button type="button" onClick={() => onAddToCart(product, 1)}>
            +
          </button>
        ) : (
          <button
            type="button"
            onClick={() => onRemoveFromCart(product.idProduct)}
          >
            -
          </button>
        )}
      </div>
    </div>
  )
}
