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
    private String city, state, country,
            street, number, zipcode,
            district, label;

    public Address() {
    }

    public Address(String City, String State, String Country, String street, String number, String zipcode,
            String district, String label) {
        this.city = City;
        this.state = State;
        this.country = Country;
        this.street = street;
        this.number = number;
        this.zipcode = zipcode;
        this.district = district;
        this.label = label;
    }

    public Address(Address other) {
        this.city = other.city;
        this.state = other.state;
        this.country = other.country;
        this.street = other.street;
        this.number = other.number;
        this.zipcode = other.zipcode;
        this.district = other.district;
        this.label = other.label;
    }

    @Override
    public String toString() {
        return "Address{" +
                "city=" + city +
                ", state=" + state +
                ", country=" + country +
                ", street=" + street +
                ", number=" + number +
                ", zipcode=" + zipcode +
                ", district=" + district +
                ", label=" + label +
                '}';
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
