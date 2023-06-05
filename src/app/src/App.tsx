import './App.css'
import Header from './components/Header'
import DepartmentsMenu from './components/DepartmentsMenu'
import ProductMatrix from './components/ProductMatrix'

export default function App() {


  return (
    <>
    <Header />
    <section className="content">
      <DepartmentsMenu />
      <ProductMatrix />
    </section>
    </>
  )
}
