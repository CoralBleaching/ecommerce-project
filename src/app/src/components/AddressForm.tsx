import { useEffect, useState } from "react"
import { Address } from "../utils/types"
import FormWrapper from "./FormWrapper"
import InputSelection from "./InputSelection"
import useCountryInfo from "../customHooks/useCountryInfo"

interface AddressFormProps {
  address: Partial<Address>
  updateFields: (fields: Partial<Address>) => void
}

export default function AddressForm({
  address,
  updateFields,
}: AddressFormProps) {
  const { countryInfo, countryOptions } = useCountryInfo()
  const [cityOptions, setCityOptions] = useState<string[]>()
  const [stateOptions, setStateOptions] = useState<string[]>()
  const [selectedCity, setSelectedCity] = useState(address.city)
  const [selectedState, setSelectedState] = useState(address.state)
  const [selectedCountry, setSelectedCountry] = useState(address.country)

  function onCountrySelection(value: string) {
    if (countryInfo.some(({ name }) => name === value)) {
      setSelectedCountry(value)
    }
    setSelectedState(undefined)
    setSelectedCity(undefined)
    setStateOptions(
      countryInfo
        .find(({ name }) => name === value)
        ?.states.map((state) => state.name) ?? []
    )
  }

  function onStateSelection(value: string) {
    setSelectedState(value)
    setSelectedCity(undefined)
    countryInfo.forEach((country) => {
      if (country.states.some(({ name }) => name === value)) {
        setSelectedCountry(() => country.name)

        setCityOptions(
          country.states.find(({ name }) => name === value)?.cities
        )
      }
    })
  }

  function onCitySelection(value: string) {
    setSelectedCity(value)
  }

  useEffect(
    function updateCity() {
      updateFields({ city: selectedCity })
    },
    [selectedCity]
  )

  useEffect(
    function updateState() {
      updateFields({ state: selectedState })
    },
    [selectedState]
  )

  useEffect(
    function updateCountry() {
      updateFields({ country: selectedCountry })
    },
    [selectedCountry]
  )

  return (
    <FormWrapper
      title="Address information"
      description="You can fill out your address information later at your first checkout."
    >
      <label>Street</label>
      <input
        autoFocus
        type="text"
        title="Street"
        value={address.street ?? ""}
        onChange={(e) => updateFields({ street: e.target.value })}
      />

      <label>Number</label>
      <input
        autoFocus
        type="text"
        title="number"
        value={address.number ?? ""}
        onChange={(e) => updateFields({ number: e.target.value })}
      />

      <label>District</label>
      <input
        autoFocus
        type="text"
        title="District"
        value={address.district ?? ""}
        onChange={(e) => updateFields({ district: e.target.value })}
      />

      <label>Zipcode</label>
      <input
        autoFocus
        type="text"
        title="Zipcode"
        value={address.zipcode ?? ""}
        onChange={(e) => updateFields({ zipcode: e.target.value })}
      />

      <InputSelection
        title="Country"
        defaultValue={selectedCountry ?? "Select or type a country name..."}
        options={countryOptions ?? []}
        onChange={(value) => onCountrySelection(value)}
      />

      <InputSelection
        title="State"
        defaultValue={
          selectedState ??
          (selectedCountry ? "Select or type a state name..." : "")
        }
        placeholder="Waiting for you to select a country..."
        options={stateOptions ?? []}
        onChange={(value) => onStateSelection(value)}
      />

      <InputSelection
        title="City"
        defaultValue={
          selectedCity ?? (selectedState ? "Select or type a city name..." : "")
        }
        placeholder="Waiting for you to select a state..."
        options={cityOptions ?? []}
        onChange={(value) => onCitySelection(value)}
      />

      <label>Label</label>
      <input
        autoFocus
        type="text"
        title="Label"
        placeholder='(Optional) Set a label for this address, e.g. "Home"'
        value={address.label ?? ""}
        onChange={(e) => updateFields({ label: e.target.value })}
      />
    </FormWrapper>
  )
}
