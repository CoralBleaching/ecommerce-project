import { ReactNode } from "react";

type FormWrapperProps = {
    title: string,
    description: string,
    children: ReactNode,
}

export default function FormWrapper({title, description, children}: FormWrapperProps) {
    return (
        <>
            <h2 className="form-title">{title}</h2>
            <span className="form-description">{description}</span>
            <div className="form-step">{children}</div>
        </>
    )
}