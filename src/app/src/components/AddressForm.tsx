import { Address } from "../utils/types";
import FormWrapper from "./FormWrapper";
import InputSelection from "./InputSelection";
import SelectBox from "./SelectBox";

let cityOptions = [
    "Rio Branco", "Cruzeiro do Sul", "Sena Madureira",
    "Maceió", "Arapiraca", "Rio Largo",
    "Macapá", "Santana", "Laranjal do Jari",
    "Manaus", "Parintins", "Itacoatiara",
    "Salvador", "Feira de Santana", "Vitória da Conquista",
    "Fortaleza", "Caucaia", "Juazeiro do Norte",
    "Brasília", "Ceilândia", "Taguatinga",
    "Vitória", "Vila Velha", "Serra",
    "Goiânia", "Aparecida de Goiânia", "Anápolis",
    "São Luís", "Imperatriz", "Timon",
    "Cuiabá", "Várzea Grande", "Rondonópolis",
    "Campo Grande", "Dourados", "Três Lagoas",
    "Belo Horizonte", "Uberlândia", "Contagem",
 "Belém", "Ananindeua", "Santarém",
    "João Pessoa", "Campina Grande", "Santa Rita",
    "Curitiba", "Londrina", "Maringá",
    "Recife", "Jaboatão dos Guararapes", "Olinda",
    "Teresina", "Parnaíba", "Picos",
 "Rio de Janeiro", "São Gonçalo", "Duque de Caxias",
    "Natal", "Mossoró", "Parnamirim",
    "Porto Alegre", "Caxias do Sul", "Pelotas",
    "Porto Velho", "Ji-Paraná", "Ariquemes",
    "Boa Vista", "Caracaraí", "Pacaraima",
    "Florianópolis", "Joinville", "Blumenau",
    "São Paulo", "Guarulhos", "Campinas",
    "Aracaju", "Nossa Senhora do Socorro", "São Cristóvão",
    "Palmas", "Araguaína", "Gurupi"]

let stateOptions = [
    "Ceará",
    "Pernambuco",
    "Bahia",
]

let countryOptions = [
    "Brazil",
    "Argentina",
    "Mexico",
]


interface AddressFormProps {
    address: Address,
    updateFields: (fields: Partial<Address>) => void
}

export default function AddressForm({address, updateFields}: AddressFormProps) {
    return (
        <FormWrapper title="Address information" description="You can fill out your address information later at your first checkout.">
            <label>Street</label>
            <input autoFocus required type="text" 
            value={address.street}
            onChange={e => updateFields({street: e.target.value})}/>
            <label>Number</label>
            <input autoFocus required type="email" 
            value={address.number}
            onChange={e => updateFields({number: e.target.value})}/>
            <label>City</label>
            <InputSelection options={cityOptions} 
            onChange={e => updateFields({city: e.target.value})}/>
            <label>State</label>
            <SelectBox options={stateOptions}
            defaultOption={address.state}
            onChange={e => updateFields({state: e.target.value})}/>
            <label>Country</label>
            <SelectBox options={countryOptions} 
            defaultOption={address.country}
            onChange={e => updateFields({country: e.target.value})}/>
        </FormWrapper>
    )
}