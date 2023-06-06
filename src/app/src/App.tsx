import './App.css'
import Header from './components/Header'
import DepartmentsMenu from './components/DepartmentsMenu'
import ProductMatrix from './components/ProductMatrix'
import { useState } from 'react'

export default function App() {
  const [idCategory, setIdCategory] = useState<number | undefined>(undefined)
  const [idSubcategory, setIdSubcategory] = useState<number | undefined>(undefined)

  function onCategoryClick(newIdCategory: number) {
    setIdCategory(newIdCategory)
    setIdSubcategory(() => undefined)
  }

  function onSubcategoryClick(newIdSubcategory: number) {
    setIdSubcategory(newIdSubcategory)
    setIdCategory(() => undefined)
  }

  return (
    <>
    <Header />
    <section className="content">
      <DepartmentsMenu 
        onCategoryClick={onCategoryClick} 
        onSubcategoryClick={onSubcategoryClick} 
      />
      <ProductMatrix 
        idCategory={idCategory} 
        idSubcategory={idSubcategory}/>
    </section>
    </>
  )
}
