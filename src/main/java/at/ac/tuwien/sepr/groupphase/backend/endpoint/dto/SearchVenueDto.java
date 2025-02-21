package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public class SearchVenueDto {
    private String name;
    private String street;
    private String city;
    private String county;
    private String postalCode;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return "VenueSearchDto{"
            + "name='" + name + '\''
            + ", street='" + street + '\''
            + ", city='" + city + '\''
            + ", county='" + county + '\''
            + ", postalCode='" + postalCode + '\''
            + '}';
    }
}