import { useEffect, useRef, useState } from "react";
import ProductCard from "./ProductCard";
import Subheader from "./Subheader";
import { Product } from "../utils/types";
import fetchAndDecode, { Order, ServerRoute } from "../utils/utils";
import PaginationBar from "./PaginationBar";

export interface ProductMatrixProps {
  idCategory?: number;
  idSubcategory?: number;
}

type Parameters = {
  orderBy?: string;
  searchText?: string;
  category?: string;
  subcategory?: string;
  pageNumber?: string;
  resultsPerPage?: string;
};

export default function ProductMatrix({
  idCategory,
  idSubcategory,
}: ProductMatrixProps) {
  const RESULTS_PER_PAGE = 9;
  const INITIAL_PAGE = 1;
  const MAX_VISIBLE_INDICES = 4;

  const [products, setProducts] = useState<Product[]>([]);
  const [orderBy, setOrderBy] = useState<string>(Order.Hotness);
  const [searchText, setSearchText] = useState<string | undefined>();
  const [activeIndex, setActiveIndex] = useState(INITIAL_PAGE);
  const [totalIndices, setTotalIndices] = useState<number>();

  useEffect(() => {
    let params: Parameters = {};
    if (searchText) params = { ...params, searchText: searchText };
    if (idCategory) params = { ...params, category: idCategory.toString() };
    if (idSubcategory)
      params = { ...params, subcategory: idSubcategory.toString() };

    const queryString = new URLSearchParams(params).toString();
    const requestUrl = `${ServerRoute.ProductCount}?${queryString}`;
    console.log(queryString);

    fetchAndDecode<{count: number}>(requestUrl, (data) => {
      let count = data.count;
      setTotalIndices(() => Math.ceil(count / RESULTS_PER_PAGE));
    })
    console.log("totalIndices = ", totalIndices)
  }, [idCategory, idSubcategory, searchText, totalIndices])

  useEffect(() => {
    // TODO: import parameter names?
    let params: Parameters = {
      orderBy: orderBy,
      pageNumber: activeIndex.toString(),
      resultsPerPage: RESULTS_PER_PAGE.toString(),
    };
    if (searchText) params = { ...params, searchText: searchText };
    if (idCategory) params = { ...params, category: idCategory.toString() };
    if (idSubcategory)
      params = { ...params, subcategory: idSubcategory.toString() };

    const queryString = new URLSearchParams(params).toString();
    const requestUrl = `${ServerRoute.AllProducts}?${queryString}`;
    console.log(queryString);

    fetchAndDecode<{ products: Product[] }>(requestUrl, (data) => {
      const fetchedProducts = data.products;
      fetchedProducts.forEach((product) => {
        product.date = new Date(product.timestamp);
      });
      setProducts(fetchedProducts);
    });
  }, [idCategory, idSubcategory, orderBy, searchText, activeIndex]);


  function onSetOrder(newOrder: string) {
    setOrderBy(newOrder);
  }

  function onSetSearchText(newText: string | undefined) {
    setSearchText(newText);
  }

  function onIndexClick(i: number) {
    setActiveIndex(i);
  }

  function onPrevClick() {
    if (activeIndex > 1) {
      setActiveIndex(activeIndex - 1);
    }
  }

  function onNextClick() {
    if (totalIndices && activeIndex < totalIndices) {
      setActiveIndex(activeIndex + 1);
    }
  }

  return (
    <div className="main-content">
      <Subheader setOrder={onSetOrder} setSearchText={onSetSearchText} />
      <div className="products-grid">
        {products.map((product) => (
          <ProductCard key={product.name} product={product} />
        ))}
      </div>
      {
        totalIndices !== undefined &&
        <PaginationBar
          totalIndices={totalIndices}
          activeIndex={activeIndex}
          maxVisibleIndices={MAX_VISIBLE_INDICES}
          onIndexClick={onIndexClick}
          onPrevClick={onPrevClick}
          onNextClick={onNextClick}
        />
      }
    </div>
  );
}
