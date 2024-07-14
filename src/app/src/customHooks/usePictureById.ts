import { useState, useEffect } from "react"
import pictureData from "../../../../database/json/Picture.json"

interface Picture {
  id_picture: number
  data: string
}

export default function usePictureById(id: number) {
  const [picture, setPicture] = useState<string | null>(null)

  useEffect(() => {
    const pictureEntry = (pictureData as Picture[]).find(
      (p) => p.id_picture === id
    )
    if (pictureEntry) {
      setPicture(pictureEntry.data)
    }
  }, [id])

  return picture
}
