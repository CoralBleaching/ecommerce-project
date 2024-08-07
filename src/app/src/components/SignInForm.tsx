import { FormEvent, useRef } from "react"
import FormWrapper from "./FormWrapper"
import fetchAndDecode, { ServerRoute } from "../utils/utils"
import { User } from "../utils/types"

type Parameters = {
  username: string
  password: string
  isFromStoreFront: string
}

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
      const parameters: Parameters = {
        username: username.current.value,
        password: password.current.value,
        isFromStoreFront: String(true),
      }

      const requestOptions: RequestInit = {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body: new URLSearchParams(parameters).toString(),
      }

      fetchAndDecode<{ user: User }>(
        ServerRoute.SignIn,
        ({ user }) => {
          if (user) {
            setUser(user)
            closeSignIn()
          }
        },
        requestOptions
      )
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
