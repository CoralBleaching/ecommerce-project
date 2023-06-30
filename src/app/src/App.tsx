import "./App.css"
import Header from "./components/Header"
import DepartmentsMenu from "./components/DepartmentsMenu"
import ProductMatrix from "./components/ProductMatrix"
import { useState } from "react"
import Modal from "./components/Modal"
import SignUpForm from "./components/SignUpForm"
import SignInForm from "./components/SignInForm"

export default function App() {
  const [activeIndex, setActiveIndex] = useState<number>(1)
  const [idCategory, setIdCategory] = useState<number>()
  const [idSubcategory, setIdSubcategory] = useState<number>()
  const [signInModalOpen, setSignInModalOpen] = useState(false)
  const [signUpModalOpen, setSignUpModalOpen] = useState(false)
  const [isSignedIn, setIsSignedIn] = useState(false)

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

  function onGoToSignUp() {
    setSignInModalOpen(false)
    setSignUpModalOpen(true)
  }

  function onGoToSignIn() {
    setSignInModalOpen(true)
    setSignUpModalOpen(false)
  }

  return (
    <>
      <Modal open={signInModalOpen} onClose={() => setSignInModalOpen(false)}>
        <SignInForm setIsSignedIn={setIsSignedIn} goToSignUp={onGoToSignUp} />
      </Modal>

      <Modal open={signUpModalOpen} onClose={() => setSignUpModalOpen(false)}>
        <SignUpForm goToSignIn={onGoToSignIn} />
      </Modal>

      <Header isSignedIn={isSignedIn}
              handleOpenSignInModal={() => setSignInModalOpen(true)} 
              handleOpenSignUpModal={() => setSignUpModalOpen(true)}/>

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
