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
            <h4 className="form-description">{description}<br/><br/></h4>
            <div className="form-step">{children}</div>
        </>
    )
}