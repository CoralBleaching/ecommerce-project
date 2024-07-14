import useAllCategories from "../customHooks/useAllCategories"

export interface DepartmentsMenuProps {
  onCategoryClick: (idCategory: number) => void
  onSubcategoryClick: (idSubcategory: number) => void
}

export default function DepartmentsMenu({
  onCategoryClick,
  onSubcategoryClick,
}: DepartmentsMenuProps) {
  const categories = useAllCategories()

  return (
    <div className="navbar">
      <h2>Departments</h2>
      {categories.map((category) => {
        return (
          <div key={category.idCategory}>
            <h3>
              <a
                className="menu-cat"
                onClick={() => onCategoryClick(category.idCategory)}
              >
                {category.name}
              </a>
            </h3>
            {category.subcategories.map((subcategory) => {
              return (
                <div key={subcategory.idSubcategory}>
                  <a
                    className="menu-subcat"
                    onClick={() =>
                      onSubcategoryClick(subcategory.idSubcategory)
                    }
                  >
                    {subcategory.name}
                  </a>
                </div>
              )
            })}
          </div>
        )
      })}
    </div>
  )
}
