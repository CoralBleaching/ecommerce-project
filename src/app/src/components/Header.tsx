interface HeaderProps {
    isSignedIn: boolean
    handleOpenSignUpModal: () => void
    handleOpenSignInModal: () => void
}

export default function Header({
    isSignedIn,
    handleOpenSignUpModal, 
    handleOpenSignInModal, 
}: HeaderProps) {


    return (
        <header className="header">
            <h1 className="title">Super Store</h1>
            <div className="buttons">
                <button onClick={handleOpenSignInModal}>
                        {isSignedIn ? "Sign Out" : "Sign In"}
                </button>
                <button onClick={handleOpenSignUpModal}>
                        {isSignedIn ? "Settings" : "Sign Up"}
                </button>
                <button>Cart</button>
            </div>
        </header>
    )
}