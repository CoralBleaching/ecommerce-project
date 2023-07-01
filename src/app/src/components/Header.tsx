interface HeaderProps {
  isSignedIn: boolean
  handleClickOnSignUp: () => void
  handleClickOnSignIn: () => void
  handleClickOnSignOut: () => void
  handleClickOnCart: () => void
}

export default function Header({
  isSignedIn,
  handleClickOnSignUp,
  handleClickOnSignIn,
  handleClickOnSignOut,
  handleClickOnCart,
}: HeaderProps) {
  return (
    <header className="header">
      <h1 className="title">Super Store</h1>
      <div className="buttons">
        {isSignedIn ? (
          <button type="button" onClick={handleClickOnSignOut}>Sign Out</button>
        ) : (
          <button type="button" onClick={handleClickOnSignIn}>Sign In</button>
        )}
        <button type="button" onClick={handleClickOnSignUp}>
          {isSignedIn ? "Settings" : "Sign Up"}
        </button>
        <button type="button" onClick={handleClickOnCart}>Cart</button>
      </div>
    </header>
  )
}
