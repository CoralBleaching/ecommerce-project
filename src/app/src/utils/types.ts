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
}
