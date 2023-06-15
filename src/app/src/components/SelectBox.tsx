import React, { useState } from "react";

interface SelectBoxProps {
  defaultOption: string
  options: string[]
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void
}

export default function SelectBox ({ defaultOption, options, onChange }: SelectBoxProps) {
  const [selectedOption, setSelectedOption] = useState<string>();
  const [filterText, setFilterText] = useState("");
  const [isOptionsVisible, setIsOptionsVisible] = useState(false);

  const filteredOptions = options.filter((option) =>
    option.toLowerCase().includes(filterText.toLowerCase())
  );

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setFilterText(event.target.value);
  };

  const handleOptionSelect = (option: string) => {
    setSelectedOption(option);
    setFilterText("");
    setIsOptionsVisible(false);
  };

  const toggleOptionsVisibility = () => {
    setIsOptionsVisible(!isOptionsVisible);
  };

  return (
    <div className="select-box">
      <input
        type="text"
        value={filterText}
        onChange={handleInputChange}
        onFocus={toggleOptionsVisibility}
        onBlur={toggleOptionsVisibility}
        placeholder="Select an option..."
      />
      {isOptionsVisible && (
        <ul className="options">
          {filteredOptions.map((option) => (
            <li
              key={option}
              onClick={() => handleOptionSelect(option)}
              className={selectedOption === option ? "selected" : ""}
            >
              {option}
            </li>
          ))}
        </ul>
      )}
      {selectedOption && (
        <p>Selected option: {selectedOption}</p>
      )}
    </div>
  );
};
