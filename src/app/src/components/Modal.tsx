import { ReactNode, useEffect } from "react"

interface ModalProps {
  open: boolean
  onClose: () => void
  children: ReactNode
}

export default function Modal({ open, onClose, children }: ModalProps) {
  useEffect(() => {
    const handleEscapeKeyPress = (event: KeyboardEvent) => {
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
  }, [open, onClose])

  const handleOverlayClick = (event: React.MouseEvent<HTMLDivElement>) => {
    if (event.target === event.currentTarget) {
      onClose()
    }
  }

  if (!open) {
    return null
  }

  return (
    <div className="modal-overlay" onClick={handleOverlayClick}>
      <div className="modal-content">{children}</div>
    </div>
  )
}
