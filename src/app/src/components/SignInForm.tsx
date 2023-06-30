import { FormEvent, useEffect, useRef } from "react"
import FormWrapper from "./FormWrapper"
import { ServerRoute } from "../utils/utils"
import { User } from "../utils/types"

type Parameters = {
    username: string
    password: string
    isFromStoreFront: string
}

interface SignInFormProps {
   setIsSignedIn: (value: boolean) => void
   goToSignUp: () => void
}

export default function SignInForm({goToSignUp, setIsSignedIn}: SignInFormProps) {
    const username = useRef<HTMLInputElement>(null)
    const password = useRef<HTMLInputElement>(null)

    function handleSubmit(event: FormEvent) {
        event.preventDefault()

        if (username.current && password.current) {
            const parameters: Parameters = {
                username: username.current.value,
                password: password.current.value,
                isFromStoreFront: String(true)
            };

            const requestOptions: RequestInit = {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: new URLSearchParams(parameters).toString()
            }

            fetch(ServerRoute.SignIn, requestOptions).then(response => response.arrayBuffer())
            .then(buffer => {
                const decoder = new TextDecoder('iso-8859-1')
                const text = decoder.decode(buffer)
                return JSON.parse(text) as {user: User}
            })
            .then(data => {
                if (data.user) {
                    // setIsSignedIn(true)
                    console.log(data.user)
                    return
                }
                // handle invalid login
            })
            .catch(console.log)
        }
    }

    function handleGoToSignUp(event: FormEvent) {
        event.preventDefault()
        goToSignUp()
    }

    return (
        <>
            <FormWrapper title="Login"
                         description="Enter your credentials:">
                <label>Username</label>
                <input ref={username} title="Username" autoFocus required type="text" />
                <label>Password</label>
                <input ref={password} title="Passowrd" autoFocus required type="password" />
            </FormWrapper>
            <button type="submit" onClick={handleSubmit}>Go</button>
            <span>Don't have an account yet?</span>
            <button type="button" onClick={handleGoToSignUp} >Sign Up</button>
        </>
    )
}