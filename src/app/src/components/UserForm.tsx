import { User } from "../utils/types";
import FormWrapper from "./FormWrapper";

interface UserFormProps {
    user: User,
    updateFields: (fields: Partial<User>) => void
}

export default function UserForm({user, updateFields}: UserFormProps) {
    return (
        <FormWrapper title="Your information" description="Enter your personal information and account information here.">
            <label>Full name</label>
            <input autoFocus required type="text" 
            value={user.name}
            onChange={e => updateFields({name: e.target.value})}/>
            <label>Email</label>
            <input autoFocus required type="email" 
            value={user.email}
            onChange={e => updateFields({email: e.target.value})}/>
            <label>Username</label>
            <input autoFocus required type="text" 
            value={user.username}
            onChange={e => updateFields({username: e.target.value})}/>
            <label>Password</label>
            <input autoFocus required type="password" 
            value={user.password}
            onChange={e => updateFields({password: e.target.value})}/>
            <label>Confirm password</label>
            <input autoFocus required type="password" />
        </FormWrapper>
    )
}