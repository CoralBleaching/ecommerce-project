import { FormEvent, useState } from "react";
import useMultiStepForm from "../utils/useMultiStepForm";
import AddressForm from "./AddressForm";
import UserForm from "./UserForm";
import { Address, User } from "../utils/types";


const INITIAL_USER_DATA: User = {
    idUser: undefined,
    name: "",
    username: "",
    password: "",
    email: "",
}
const INITIAL_ADDRESS_DATA: Partial<Address> = {
}

interface MultistepformProps {

}

export default function MultiStepForm({}: MultistepformProps) {
    const [user, setUser] = useState<User>(INITIAL_USER_DATA)
    const [address, setAddress] = useState<Partial<Address>>(INITIAL_ADDRESS_DATA)
    const {
        currentStepIndex,
        totalSteps,
        isFirstStep,
        isLastStep,
        isSkippable,
        step,
        onBack,
        onNext,
    } = useMultiStepForm([
        {step: <UserForm user={user} updateFields={updateUser} />, skippable: false}, 
        {step: <AddressForm address={address} updateFields={updateAddress} />, skippable: true}
    ])

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

    function isInstanceFilled<T>(object: Partial<T>, skipValues: (keyof T)[] = []): boolean {
        return Object.entries(object).every(([key,value]) => {
            if (skipValues.includes(key as keyof T)) {
                return true
            }
            return value !== undefined
        })
    }

    function renderSubmitButton() {
        if (!isLastStep) {
            return "Next"
        }
        if (isSkippable && !isInstanceFilled(address, ['label']) ) {
            return "Skip and register"
        }
        return "Register"
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
                    <button type="submit">{renderSubmitButton()}</button>
            </div>
        </form>
    )

}