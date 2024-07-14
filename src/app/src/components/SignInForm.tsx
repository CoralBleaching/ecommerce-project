import { FormEvent, useRef } from "react"
import FormWrapper from "./FormWrapper"
import { User } from "../utils/types"
import usersData from "../../../../database/json/User.json"

interface SignInFormProps {
  setUser: (value: User) => void
  goToSignUp: () => void
  closeSignIn: () => void
}

export default function SignInForm({
  goToSignUp,
  setUser,
  closeSignIn,
}: SignInFormProps) {
  const username = useRef<HTMLInputElement>(null)
  const password = useRef<HTMLInputElement>(null)

  function handleSubmit(event: FormEvent) {
    event.preventDefault()

    if (username.current && password.current) {
      const user = fetchUser(username.current.value, password.current.value)
      if (user) {
        setUser(user)
        closeSignIn()
      }

      function fetchUser(username: string, password: string): User | null {
        if (!username || !password) {
          return null
        }
        const user = usersData.find(
          (u) => u.username === username && u.password === password
        )
        if (user) {
          return {
            idUser: user.id_user,
            name: user.name,
            username: user.username,
            email: user.email,
            password: user.password,
          }
        }
        return null
      }
    }
  }

  function handleGoToSignUp(event: FormEvent) {
    event.preventDefault()
    goToSignUp()
  }

  return (
    <>
      <FormWrapper title="Login" description="Enter your credentials:">
        <label>Username</label>
        <input ref={username} title="Username" autoFocus required type="text" />
        <label>Password</label>
        <input
          ref={password}
          title="Passowrd"
          autoFocus
          required
          type="password"
        />
      </FormWrapper>
      <button type="submit" onClick={handleSubmit}>
        Go
      </button>
      <span>Don't have an account yet?</span>
      <button type="button" onClick={handleGoToSignUp}>
        Sign Up
      </button>
    </>
  )
}
