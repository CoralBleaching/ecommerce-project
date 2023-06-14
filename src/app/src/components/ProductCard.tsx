import { useEffect, useState } from "react"
import { Product } from "../utils/types"
import fetchAndDecode, { ServerRoute } from "../utils/utils";

export interface ProductCardProps {
    product: Product
}

export default function ProductCard({product}: ProductCardProps) {
    const [picture, setPicture] = useState<string>();

    useEffect(function loadPic() {
        const params = {"idPicture": product.idPicture.toString()}
        const queryString = new URLSearchParams(params).toString()
        const requestUrl = `${ServerRoute.Picture}?${queryString}`

        fetchAndDecode<{picture: string}>(requestUrl, (data) => {
            const pictureData = data.picture
            setPicture(() => pictureData)
        })
    }, [])

    return (
        <div className="card">
            <ul>
                <li>{product.name}</li>
                <li>{product.price}</li>
                <li>{product.date?.toDateString()}</li>
            </ul>
            <img className="card-image"
                src={`data:image/jpeg;base64,${picture}`} alt="Image" 
                />
        </div>
    )
}
