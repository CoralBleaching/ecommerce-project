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