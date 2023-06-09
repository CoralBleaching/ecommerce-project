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

export interface Evaluation {
    idProduct: number
    idSale: number
    timestamp: Date 
    review: string 
    score: number
}
