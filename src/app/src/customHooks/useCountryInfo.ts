import { useEffect, useState } from "react"
import countriesData from "../../../../database/json/Country.json"
import statesData from "../../../../database/json/State.json"
import citiesData from "../../../../database/json/City.json"
import { Country } from "../utils/types"

export default function useCountryInfo() {
  const [countryInfo, setCountryInfo] = useState<Country[]>([])
  const [countryOptions, setCountryOptions] = useState<string[]>([])

  useEffect(() => {
    const transformedCountryData = countriesData.map((country) => {
      const countryStates = statesData
        .filter((state) => state.id_country === country.id_country)
        .map((state) => ({
          name: state.name,
          cities: citiesData
            .filter((city) => city.id_state === state.id_state)
            .map((city) => city.name),
        }))

      return {
        name: country.name,
        states: countryStates,
      }
    })

    setCountryInfo(transformedCountryData)
    setCountryOptions(transformedCountryData.map((country) => country.name))
  }, [])

  return { countryInfo, countryOptions }
}
