import './App.css'
import Header from './components/Header'
import DepartmentsMenu from './components/DepartmentsMenu'
import ProductMatrix from './components/ProductMatrix'
import { useState } from 'react'

export default function App() {
  const [activeIndex, setActiveIndex] = useState<number>(1)
  const [idCategory, setIdCategory] = useState<number>()
  const [idSubcategory, setIdSubcategory] = useState<number>()

  function onCategoryClick(newIdCategory: number) {
    setIdCategory(newIdCategory)
    setIdSubcategory(() => undefined)
    setActiveIndex(() => 1)
  }

  function onSubcategoryClick(newIdSubcategory: number) {
    setIdSubcategory(newIdSubcategory)
    setIdCategory(() => undefined)
    setActiveIndex(() => 1)
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
        parentActiveIndex={activeIndex}
        idCategory={idCategory} 
        idSubcategory={idSubcategory}/>
    </section>
    </>
  )
}
