export interface SubheaderProps {

}

export default function Subheader({}: SubheaderProps) {
    return (
        <div className="sub-header">
            Order by:
            <select title="Choose an option">
            <option value="ascending">Price (Ascending)</option>
            <option value="descending">Price (Descending)</option>
            </select>
        </div>
    )
}