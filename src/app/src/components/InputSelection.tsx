// Note: this is another component almost entirely built by chatGPT, CSS included.

import React, { useEffect, useState } from "react"

interface InputSelectionProps {
  title: string
  defaultValue: string
  placeholder?: string
  options: string[]
  onChange: (e: string) => void
}

export default function InputSelection ({title, defaultValue, placeholder, options, onChange }: InputSelectionProps) {
  const [selectedOption, setSelectedOption] = useState<string>()
  const [inputValue, setInputValue] = useState(defaultValue)
  const [isDropdownOpen, setIsDropdownOpen] = useState(false)
  const [isValidValue, setIsValidValue] = useState(true)
  const [filterText, setFilterText] = useState(defaultValue ?? "");

  const filteredOptions = isValidValue ? options : options.filter((option) =>
    option.toLowerCase().includes(filterText.toLowerCase())
  );
  
  function handleInputChange (event: React.ChangeEvent<HTMLInputElement>) {
    const value = event.target.value
    if (value === "") {
      setIsDropdownOpen(false)
    } else {
      setIsDropdownOpen(true)
    }
    setFilterText(value);
    setInputValue(value)
    setIsValidValue(() => options.some((option) => option === value))
  }

  useEffect(() => {
    if (isValidValue) {
      setSelectedOption(inputValue)
    }
  }, [isValidValue])

  function handleOptionSelect (option: string) {
    setFilterText(option)
    setSelectedOption(option)
    setInputValue(option)
    setIsValidValue(true)
  }

  function toggleDropdown () {
    setIsDropdownOpen(!isDropdownOpen)
  }

  useEffect(() => {
    setIsDropdownOpen(false)
    if (selectedOption) onChange(selectedOption)    
  }, [selectedOption])

  useEffect(() => {
    setFilterText(defaultValue)
  }, [defaultValue])

  return (
    <>
      <label>{title}</label>
      <div className="input-selection">
        <input title={title} placeholder={placeholder}
          type="text"
          value={filterText}
          onChange={handleInputChange}
          className={isValidValue ? "" : "invalid"}
          />
        <button type="button" onClick={toggleDropdown}>{isDropdownOpen ? "▲" : "▼"}</button>
        {isDropdownOpen && (
          <ul className="dropdown">
            {filteredOptions.map((option) => (
              <li key={option} onClick={() => handleOptionSelect(option)}>
                {option}
              </li>
            ))}
          </ul>
        )}
      </div>
    </>
  )
}
