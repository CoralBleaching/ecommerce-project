import { useRef } from "react"

export interface SubheaderProps {

}

export default function Subheader({}: SubheaderProps) {
    const searchText = useRef<HTMLInputElement>(null)

    function onSearch(event: React.FormEvent) {
        event.preventDefault()
        console.log(searchText.current?.value)
    }

    return (
        <div className="sub-header">
            <div className="sub-header-component">
                <form className="search-bar" onSubmit={onSearch}>
                    <input className="search-input" ref={searchText} title="product-search"
                        type="search" placeholder="Type a product name..."
                        />
                    <button type="submit" >Go</button>
                </form>
            </div>
            <div className="sub-header-component" >
                Order by:
                <select title="Choose an option">
                <option value="hotness">Hotness</option>
                <option value="price-ascending">Price (Ascending)</option>
                <option value="price-descending">Price (Descending)</option>
                <option value="newest">Newest</option>
                <option value="oldest">Oldest</option>
                </select>
            </div>
        </div>
    )
}