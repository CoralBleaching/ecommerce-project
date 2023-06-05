interface HeaderProps {

}

export default function Header({}: HeaderProps) {

    return (
        <header className="header">
            <h1 className="title">Super Store</h1>
            <div className="buttons">
                <button>Sign In/Out</button>
                <button>Sign Up/Settings</button>
                <button>Cart</button>
            </div>
        </header>
    )
}