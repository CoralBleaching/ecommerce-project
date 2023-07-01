import { CartItem, Product } from "../utils/types"
import ProductCard, { CardType } from "./ProductCard"

interface CartProps {
    cartItems: CartItem[]
    onAddToCart: (product: Product, quantity: number) => void
    onRemoveFromCart: (productId: number) => void
}

export default function Cart({
    cartItems,
    onAddToCart,
    onRemoveFromCart
}: CartProps) {

    function registerSale() {
        // build sale query
        // request to register sale
    }

    return (
        <div className="cart">
          <h1>Your Cart</h1>
          {cartItems.map(item => {
            return <ProductCard product={item.product}
                         type={CardType.Cart}
                         quantity={item.quantity}
                         onAddToCart={onAddToCart}
                         onRemoveFromCart={onRemoveFromCart} />
          })}
          <button type="button" onClick={registerSale}>Proceed to Checkout</button>
        </div>
    )
}