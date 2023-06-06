export default function fetchAndDecode<T>(url: string, callback: (data: T) => void) {
    fetch(url)
    .then(response =>  response.arrayBuffer())
    .then(buffer => {
      const decoder = new TextDecoder('iso-8859-1')
      const text = decoder.decode(buffer)
      return JSON.parse(text) as T
    })
    .then(callback).catch(error => console.log(error))
}

const SERVER_URL = "http://localhost:8080/ecommerce-demo"

export enum ServerRoute {
  AllCategories = SERVER_URL + "/Categories",
  AllProducts = SERVER_URL + "/Products"
}