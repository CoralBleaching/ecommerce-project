interface HeaderProps {
    handleOpenModal: () => void
    // handleCloseModal: () => void
}

export default function Header({
    handleOpenModal, 
    // handleCloseModal
}: HeaderProps) {


    return (
        <header className="header">
            <h1 className="title">Super Store</h1>
            <div className="buttons">
                <button onClick={handleOpenModal}>Sign In/Out</button>
                <button>Sign Up/Settings</button>
                <button>Cart</button>
            </div>
        </header>
    )
}