import { FormEvent, useState } from "react";
import useMultistepform from "../utils/useMultistepform";
import AddressForm from "./AddressForm";
import UserForm from "./UserForm";
import { Address, User } from "../utils/types";


const INITIAL_USER_DATA: User = {
    idUser: -1,
    name: "",
    username: "",
    password: "",
    email: "",
}
const INITIAL_ADDRESS_DATA: Address = {
    city: "",
    state: "",
    country: "",
    street: "",
    number: "",
    zipcode: "",
    district: "",
    label: ""
}

interface MultistepformProps {

}

export default function Multistepform({}: MultistepformProps) {
    const [user, setUser] = useState<User>(INITIAL_USER_DATA)
    const [address, setAddress] = useState<Address>(INITIAL_ADDRESS_DATA)
    const {
        currentStepIndex,
        totalSteps,
        isFirstStep,
        isLastStep,
        step,
        onBack,
        onNext,
    } = useMultistepform([<UserForm user={user} updateFields={updateUser} />, <AddressForm address={address} updateFields={updateAddress} />])

    function updateUser(fields: Partial<User>) {
        setUser(previous => { 
            return {...previous, ...fields}
        })
    }

    function updateAddress(fields: Partial<Address>) {
        setAddress(previous => { 
            return {...previous, ...fields}
        })
    }

    function onSubmit(e: FormEvent) {
        e.preventDefault()
        if (!isLastStep) return onNext()
        alert("Success!")
    }   

    return (
        <form className="multi-step-form" onSubmit={onSubmit}>
            <div className="step-counter">
                {currentStepIndex + 1} / {totalSteps} 
            </div>
            <div>
                {step}
            </div>
            <div className="multi-step-form-buttons">
                {!isFirstStep && 
                    <button type="button" onClick={onBack}>Back</button>}
                {!isLastStep ? 
                    <button type="submit">Next</button>
                    : <button type="submit">Submit</button>
                }
            </div>
        </form>
    )

}