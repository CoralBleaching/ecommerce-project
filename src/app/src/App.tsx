import "./App.css"
import Header from "./components/Header"
import DepartmentsMenu from "./components/DepartmentsMenu"
import ProductMatrix from "./components/ProductMatrix"
import { useState } from "react"
import Modal from "./components/Modal"
import SignUpForm from "./components/SignUpForm"
import SignInForm from "./components/SignInForm"
import fetchAndDecode, { ServerRoute } from "./utils/utils"
import { CartItem, Product, User } from "./utils/types"
import SidePanel from "./components/SidePanel"
import Cart from "./components/Cart"


export default function App() {
  const [activeIndex, setActiveIndex] = useState<number>(1)
  const [idCategory, setIdCategory] = useState<number>()
  const [idSubcategory, setIdSubcategory] = useState<number>()
  const [signInModalOpen, setSignInModalOpen] = useState(false)
  const [signUpModalOpen, setSignUpModalOpen] = useState(false)
  const [user, setUser] = useState<User>()
  const [cartIsOpen, setCartIsOpen] = useState(false)
  const [cart, setCart] = useState<CartItem[]>([])

  const isSignedIn = user != undefined

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

  function onSignOutClick() {
    const queryString = new URLSearchParams({"isFromStoreFront": String(true)}).toString()
    fetchAndDecode(`${ServerRoute.SignOut}?${queryString}`, () => {})
    setUser(undefined)
  }

  function onCartClick() {

  }

  function onAddToCart(product: Product, quantity: number) {
    let i = cart.findIndex(item => item.product.idProduct === product.idProduct)
    if (i == -1) {
      setCart([...cart, {quantity, product}])
    } else {
      setCart(cart.map(item => {
        if (item.product.idProduct === product.idProduct) {
          item.quantity++
        }
        return item
      }))
    }
  }

  function onRemoveFromCart(productId: number) {
    const newCart = [...cart]
    for (let i = 0, offset = 0; i < cart.length - offset; i++) {
      if (newCart[i].product.idProduct === productId) {
        newCart[i].quantity--
      }
      if (newCart[i].quantity <= 0) {
        newCart.splice(i, 1)
        offset++
        i--
      }
    }
    setCart(newCart)    
  }

  return (
    <>
      <Modal open={signInModalOpen} onClose={() => setSignInModalOpen(false)}>
        <SignInForm setUser={setUser} 
                    goToSignUp={onGoToSignUp} 
                    closeSignIn={() => setSignInModalOpen(false)}/>
      </Modal>

      <Modal open={signUpModalOpen} onClose={() => setSignUpModalOpen(false)}>
        <SignUpForm goToSignIn={onGoToSignIn} />
      </Modal>

      <Header isSignedIn={isSignedIn}
              handleClickOnSignIn={() => setSignInModalOpen(true)} 
              handleClickOnSignUp={() => setSignUpModalOpen(true)}
              handleClickOnSignOut={onSignOutClick}
              handleClickOnCart={() => {setCartIsOpen(true)}}/>

      <section className="content">
        <DepartmentsMenu
          onCategoryClick={onCategoryClick}
          onSubcategoryClick={onSubcategoryClick}
        />

        <ProductMatrix
          parentActiveIndex={activeIndex}
          idCategory={idCategory}
          idSubcategory={idSubcategory}
          onAddToCart={onAddToCart}
          onRemoveFromCart={onRemoveFromCart}
        />
      </section>

      <SidePanel open={cartIsOpen} 
                 onClose={() => {setCartIsOpen(false)}}>
        <Cart cartItems={cart}
              onAddToCart={onAddToCart}
              onRemoveFromCart={onRemoveFromCart}/>
      </SidePanel>
    </>
  )
}
