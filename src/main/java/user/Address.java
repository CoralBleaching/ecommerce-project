/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user;

/**
 *
 * @author renat
 */
public class Address {
    public final int addressId;
    public final String city, state, country,
            street, number, zipcode,
            district, label;

    public Address(int addressId, String City, String State, String Country, String street, String number,
            String zipcode,
            String district, String label) {
        this.addressId = addressId;
        this.city = City;
        this.state = State;
        this.country = Country;
        this.street = street;
        this.number = number;
        this.zipcode = zipcode;
        this.district = district;
        this.label = label;
    }

}
