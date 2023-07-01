export default function fetchAndDecode<T>(url: string, 
  callback: (data: T) => void,
  options: RequestInit = {},
  errorCallback: (reason: any) => void = console.log) {
    fetch(url, options)
    .then(response =>  response.arrayBuffer())
    .then(buffer => {
      const decoder = new TextDecoder('iso-8859-1')
      const text = decoder.decode(buffer)
      return text.length > 0 ? JSON.parse(text) as T : {} as T
    })
    .then(callback).catch(errorCallback)
}

const SERVER_URL = "http://localhost:8080/ecommerce-demo"

export enum ServerRoute {
  AllCategories = SERVER_URL + "/Categories",
  AllProducts = SERVER_URL + "/Products",
  CountryStateCityInfo = SERVER_URL + "/CountryInfo",
  Picture = SERVER_URL + "/Picture",
  Pictures = SERVER_URL + "/Pictures",
  PingSession = SERVER_URL + "/PingSession",
  ProductCount = SERVER_URL + "/ProductCount",
  SignIn = SERVER_URL + "/Login",
  SignOut = SERVER_URL + "/SignOut",
}

export enum Order {
  PriceAscending = "price-ascending",
  PriceDescending = "price-descending",
  Hotness = "hotness",
  Newest = "newest",
  Oldest = "oldest",
}
