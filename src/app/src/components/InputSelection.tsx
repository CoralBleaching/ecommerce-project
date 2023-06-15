// Note: this is another component almost entirely built by chatGPT, CSS included.

import React, { useState } from "react"

interface InputSelectionProps {
  options: string[]
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void
}

export default function InputSelection ({ options }: InputSelectionProps) {
  const [selectedOption, setSelectedOption] = useState<string>()
  const [inputValue, setInputValue] = useState("")
  const [isDropdownOpen, setIsDropdownOpen] = useState(false)
  const [isValidValue, setIsValidValue] = useState(true)
  const [filterText, setFilterText] = useState("");

  const filteredOptions = options.filter((option) =>
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
    setIsValidValue(options.some((option) => option === value))
  }

  function handleOptionSelect (option: string) {
    setFilterText(option);
    setSelectedOption(option)
    setInputValue(option)
    setIsDropdownOpen(false)
    setIsValidValue(true)
  }

  function toggleDropdown () {
    setIsDropdownOpen(!isDropdownOpen)
  }

  return (
    <div className="input-selection">
      <input
        type="text"
        value={filterText}
        onChange={handleInputChange}
        className={isValidValue ? "" : "invalid"}
      />
      <button type="button" onClick={toggleDropdown}>{isDropdownOpen ? "▲" : "▼"}</button>
      {isDropdownOpen && (
        <ul className="dropdown">
          {filteredOptions/*.slice(0, 4)*/.map((option) => (
            <li key={option} onClick={() => handleOptionSelect(option)}>
              {option}
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}
