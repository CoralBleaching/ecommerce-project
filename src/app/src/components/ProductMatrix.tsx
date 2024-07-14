import { useEffect, useState } from "react"
import ProductCard, { CardType } from "./ProductCard"
import Subheader from "./Subheader"
import { Product } from "../utils/types"
import { Order } from "../utils/utils"
import PaginationBar from "./PaginationBar"
import useProductCount from "../customHooks/useProductCount"
import useAllProducts from "../customHooks/useAllProducts"

export interface ProductMatrixProps {
  parentActiveIndex: number
  idCategory?: number
  idSubcategory?: number
  onAddToCart: (product: Product, quantity: number) => void
  onRemoveFromCart: (productId: number) => void
}

export default function ProductMatrix({
  parentActiveIndex,
  idCategory,
  idSubcategory,
  onAddToCart,
  onRemoveFromCart,
}: ProductMatrixProps) {
  const RESULTS_PER_PAGE = 9
  const MAX_VISIBLE_INDICES = 4

  const [orderBy, setOrderBy] = useState<string>(Order.Hotness)
  const [searchText, setSearchText] = useState<string>()
  const [activeIndex, setActiveIndex] = useState(parentActiveIndex)

  const products = useAllProducts(
    idCategory,
    idSubcategory,
    searchText,
    orderBy,
    activeIndex,
    RESULTS_PER_PAGE
  )
  const totalIndices = useProductCount(idCategory, idSubcategory, searchText)

  useEffect(() => {
    setActiveIndex(parentActiveIndex)
  }, [parentActiveIndex])

  function onSetOrder(newOrder: string) {
    setOrderBy(newOrder)
  }

  function onSetSearchText(newText: string | undefined) {
    setSearchText(newText)
  }

  function onIndexClick(i: number) {
    setActiveIndex(i)
  }

  function onPrevClick() {
    if (activeIndex > 1) {
      setActiveIndex(activeIndex - 1)
    }
  }

  function onNextClick() {
    if (totalIndices && activeIndex < totalIndices) {
      setActiveIndex(activeIndex + 1)
    }
  }

  return (
    <div className="main-content">
      <Subheader setOrder={onSetOrder} setSearchText={onSetSearchText} />
      <div className="products-grid">
        {products.map((product) => (
          <ProductCard
            key={product.name}
            product={product}
            type={CardType.ProductMatrix}
            onAddToCart={onAddToCart}
            onRemoveFromCart={onRemoveFromCart}
          />
        ))}
      </div>
      {totalIndices !== undefined && (
        <PaginationBar
          totalIndices={totalIndices}
          activeIndex={activeIndex}
          maxVisibleIndices={MAX_VISIBLE_INDICES}
          onIndexClick={onIndexClick}
          onPrevClick={onPrevClick}
          onNextClick={onNextClick}
        />
      )}
    </div>
  )
}
