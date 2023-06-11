/* Note: this file was initially written by chatGPT 
  then extensively debugged manually */

interface PaginationBarProps {
  totalIndices: number;
  activeIndex: number;
  maxVisibleIndices: number;
  onIndexClick: (index: number) => void;
  onPrevClick: () => void;
  onNextClick: () => void;
}

export default function PaginationBar({
  totalIndices,
  activeIndex,
  maxVisibleIndices,
  onIndexClick,
  onPrevClick,
  onNextClick,
}: PaginationBarProps) {
  function getPageIndices(): number[] {
    const indices: number[] = [];
    if (totalIndices <= maxVisibleIndices) {
      for (let i = 1; i <= totalIndices; i++) {
        indices.push(i);
      }
    } else {
      let offset = activeIndex - (maxVisibleIndices - 1)
      let start = Math.max(1, offset);
      let end = Math.min(totalIndices, start + maxVisibleIndices - 1);

      for (let i = start; i <= end; i++) {
        indices.push(i);
      }

      if (start + maxVisibleIndices - 1 >= totalIndices) {
        indices.unshift(-1)
      } else {
        indices.push(-1)
      }
    }

    return indices;
  }

  return (
    <div className="pagination-bar">
      <button disabled={activeIndex === 1} onClick={() => onIndexClick(1)}>
        {"<<"}
      </button>
      <button disabled={activeIndex === 1} onClick={onPrevClick}>
        {"<"}
      </button>
      {getPageIndices().map((index) => (
        <button
          key={index}
          disabled={index === -1 || index === activeIndex}
          onClick={() => onIndexClick(index)}
          className={index === activeIndex ? "active" : ""}
        >
          {index === -1 ? "..." : index}
        </button>
      ))}
      <button disabled={activeIndex === totalIndices} onClick={onNextClick}>
        {">"}
      </button>
      <button disabled={activeIndex === totalIndices} onClick={() => onIndexClick(totalIndices)}>
        {">>"}
      </button>
    </div>
  );
}
