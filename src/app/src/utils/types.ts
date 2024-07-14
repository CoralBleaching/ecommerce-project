export interface Subcategory {
  idSubcategory: number
  name: string
  description: string
}

export interface Category {
  idCategory: number
  name: string
  description: string
  subcategories: Subcategory[]
}

export interface Product {
  idProduct: number
  idPicture: number
  stock: number
  price: number
  category: string
  subcategory: string
  name: string
  description: string
  hotness: number
  timestamp: string
  date?: Date
}

export type CartItem = {
  quantity: number
  product: Product
}

export interface Evaluation {
  idProduct: number
  idSale: number
  timestamp: Date
  review: string
  score: number
}

export interface Sale {
  saleId: number
  productIds: number[]
}

export interface User {
  idUser?: number
  name: string
  username: string
  password: string
  email: string
}

export interface Address {
  addressId?: number
  city: string
  state: string
  country: string
  street: string
  number: string
  zipcode: string
  district: string
  label: string
}

type State = {
  name: string
  cities: string[]
}

export interface Country {
  name: string
  states: State[]
}
