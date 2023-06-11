import { useRef } from "react"
import { Order } from "../utils/utils"

export interface SubheaderProps {
    setOrder: (newOrder: string) => void
    setSearchText: (newText: string | undefined) => void
}

export default function Subheader({setOrder, setSearchText}: SubheaderProps) {
    const searchText = useRef<HTMLInputElement>(null)
    const orderBy = useRef<HTMLSelectElement>(null)

    function onSearch(event: React.FormEvent) {
        event.preventDefault()
        setSearchText(searchText.current?.value)
    }

    function onClear(event: React.FormEvent) {
        event.preventDefault()
        setSearchText(undefined)
        if (searchText.current) {
            searchText.current.value = ""
        }
    }

    function onOrderSelection(event: React.FormEvent) {
        event.preventDefault()
        switch (orderBy.current?.value) {
            case Order.Newest:
                setOrder(Order.Newest)
                return
            case Order.Oldest:
                setOrder(Order.Oldest)
                return
            case Order.PriceAscending:
                setOrder(Order.PriceAscending)
                return
            case Order.PriceDescending:
                setOrder(Order.PriceDescending)
                return
            default:
                setOrder(Order.Hotness)
                return
        }
    }

    return (
        <div className="sub-header">
            <div className="sub-header-component">
                <form className="search-bar" onSubmit={onSearch} onReset={onClear}>
                    <input className="search-input" ref={searchText} title="product-search"
                        type="search" placeholder="Type a product name..."
                        />
                    <button type="submit">Go</button>
                    <button type="reset">Clear</button>
                </form>
            </div>
            <div className="sub-header-component" >
                Order by:
                <select ref={orderBy} title="Choose an option" onChange={onOrderSelection}>
                <option value={Order.Hotness}>Hotness</option>
                <option value={Order.PriceAscending}>Price (Ascending)</option>
                <option value={Order.PriceDescending}>Price (Descending)</option>
                <option value={Order.Newest}>Newest</option>
                <option value={Order.Oldest}>Oldest</option>
                </select>
            </div>
        </div>
    )
}