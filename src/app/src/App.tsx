import "./App.css"
import Header from "./components/Header"
import DepartmentsMenu from "./components/DepartmentsMenu"
import ProductMatrix from "./components/ProductMatrix"
import { useState } from "react"
import Modal from "./components/Modal"
import Multistepform from "./components/Multistepform"

export default function App() {
  const [activeIndex, setActiveIndex] = useState<number>(1)
  const [idCategory, setIdCategory] = useState<number>()
  const [idSubcategory, setIdSubcategory] = useState<number>()
  const [modalOpen, setModalOpen] = useState(false)

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

  function handleOpenModal() {
    setModalOpen(true)
  }

  function handleCloseModal() {
    setModalOpen(false)
  }

  return (
    <>
      <Modal open={modalOpen} onClose={handleCloseModal}>
        {/* <h1>Modal Content</h1>
        <p>This is the content of the modal.</p>
        <button onClick={handleCloseModal}>Close Modal</button> */}
        <Multistepform />
      </Modal>

      <Header handleOpenModal={handleOpenModal} />

      <section className="content">
        <DepartmentsMenu
          onCategoryClick={onCategoryClick}
          onSubcategoryClick={onSubcategoryClick}
        />

        <ProductMatrix
          parentActiveIndex={activeIndex}
          idCategory={idCategory}
          idSubcategory={idSubcategory}
        />
      </section>
    </>
  )
}
