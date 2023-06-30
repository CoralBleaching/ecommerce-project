import { Address } from "../utils/types";
import FormWrapper from "./FormWrapper";
import InputSelection from "./InputSelection";

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
            <label >Street</label>
            <input autoFocus required type="text" title="Street"
            value={address.street}
            onChange={e => updateFields({street: e.target.value})}/>
            <label>Number</label>
            <input autoFocus required type="text" title="number"
            value={address.number}
            onChange={e => updateFields({number: e.target.value})}/>
            <InputSelection title="City"
                defaultValue={address.city}
                options={cityOptions} 
                onChange={value => updateFields({city: value})}/>
            <InputSelection title="State"
                defaultValue={address.state} 
                options={stateOptions}
                onChange={value => updateFields({state: value})}/>
            <InputSelection title="Country"
                defaultValue={address.country} 
                options={countryOptions} 
                onChange={value => updateFields({country: value})}/>
        </FormWrapper>
    )
}