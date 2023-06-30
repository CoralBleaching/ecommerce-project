import { User } from "../utils/types";
import FormWrapper from "./FormWrapper";

interface UserFormProps {
    user: Partial<User>
    updateFields: (fields: Partial<User>) => void
}

export default function UserForm({user, updateFields}: UserFormProps) {
    return (
        <FormWrapper title="Your information" description="Enter your personal information and account information here.">
            <label>Full name</label>
            <input title="Full name" autoFocus required type="text" 
            value={user.name}
            onChange={e => updateFields({name: e.target.value})}/>
            <label>Email</label>
            <input title="Email" autoFocus required type="email" 
            value={user.email}
            onChange={e => updateFields({email: e.target.value})}/>
            <label>Username</label>
            <input title="Username" autoFocus required type="text" 
            value={user.username}
            onChange={e => updateFields({username: e.target.value})}/>
            <label>Password</label>
            <input title="Password" autoFocus required type="password" 
            value={user.password}
            onChange={e => updateFields({password: e.target.value})}/>
            <label>Confirm password</label>
            <input title="Confirm password" autoFocus required type="password" />
        </FormWrapper>
    )
}