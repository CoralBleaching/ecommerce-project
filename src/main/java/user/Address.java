package user;

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
