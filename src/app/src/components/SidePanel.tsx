import { ReactNode, useEffect, useState } from "react"

interface SidePanelProps {
  open: boolean
  onClose: () => void
  children: ReactNode
}

export default function SidePanel({ open, onClose, children }: SidePanelProps) {
  const [folded, setFolded] = useState(true)

  useEffect(
    function handleEscPress() {
      function handleEscapeKeyPress(event: KeyboardEvent) {
        if (event.key === "Escape") {
          onClose()
        }
      }

      if (open) {
        document.addEventListener("keydown", handleEscapeKeyPress)
      }

      return () => {
        document.removeEventListener("keydown", handleEscapeKeyPress)
      }
    },
    [open, onClose]
  )

  // function handleOverlayClick (event: React.MouseEvent<HTMLDivElement>) {
  //   if (event.target !== event.currentTarget) {
  //     onClose()
  //   }
  // }

  function handleToggleFold() {
    setFolded(!folded)
  }

  if (!open) {
    return null
  }

  return (
    <div className={`side-panel ${folded ? "folded" : ""}`}>
      <div className="side-panel-left-bar" onClick={handleToggleFold}>
        <div className="arrow-icon">{folded ? ">" : "<"}</div>
      </div>
      <div className="side-panel-content">{children}</div>
      {/* <div className="side-panel-overlay" onClick={handleOverlayClick} /> */}
    </div>
  )
}
