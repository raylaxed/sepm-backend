package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import java.util.Objects;

@Entity
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String county;

    @Column(nullable = false)
    private String postalCode;

    @Column(name = "hall_ids")
    @ElementCollection
    @CollectionTable(
        name = "venue_hall_ids",
        joinColumns = @JoinColumn(name = "venue_id")
    )
    private List<Long> hallIds = new ArrayList<>();

    @Column(name = "show_ids")
    @ElementCollection
    @CollectionTable(
        name = "venue_show_ids",
        joinColumns = @JoinColumn(name = "venue_id")
    )
    private List<Long> showIds = new ArrayList<>();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<Long> getHallIds() {
        return hallIds;
    }

    public void setHallIds(List<Long> hallIds) {
        this.hallIds = hallIds;
    }

    public void addHallId(Long hallId) {
        hallIds.add(hallId);
    }

    public void removeHallId(Long hallId) {
        hallIds.remove(hallId);
    }

    public List<Long> getShowIds() {
        return showIds;
    }

    public void setShowIds(List<Long> showIds) {
        this.showIds = showIds;
    }

    // Override equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Venue venue)) {
            return false;
        }
        return Objects.equals(id, venue.id)
               && Objects.equals(name, venue.name)
               && Objects.equals(street, venue.street)
               && Objects.equals(city, venue.city)
               && Objects.equals(county, venue.county)
               && Objects.equals(postalCode, venue.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, street, city, county, postalCode);
    }

    // Override toString
    @Override
    public String toString() {
        return "Venue{"
               + "id=" + id
               + ", name='" + name + '\''
               + ", street='" + street + '\''
               + ", city='" + city + '\''
               + ", county='" + county + '\''
               + ", postalCode='" + postalCode + '\''
               + '}';
    }

    // Builder Pattern (optional)
    public static final class VenueBuilder {
        private Long id;
        private String name;
        private String street;
        private String city;
        private String county;
        private String postalCode;
        private List<Long> hallIds = new ArrayList<>();
        private List<Long> showIds = new ArrayList<>();

        public VenueBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public VenueBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public VenueBuilder withStreet(String street) {
            this.street = street;
            return this;
        }

        public VenueBuilder withCity(String city) {
            this.city = city;
            return this;
        }

        public VenueBuilder withCounty(String county) {
            this.county = county;
            return this;
        }

        public VenueBuilder withPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public VenueBuilder withHallIds(List<Long> hallIds) {
            this.hallIds = hallIds;
            return this;
        }

        public VenueBuilder withShowIds(List<Long> showIds) {
            this.showIds = showIds;
            return this;
        }

        public Venue build() {
            Venue venue = new Venue();
            venue.setId(id);
            venue.setName(name);
            venue.setStreet(street);
            venue.setCity(city);
            venue.setCounty(county);
            venue.setPostalCode(postalCode);
            venue.setHallIds(hallIds);
            venue.setShowIds(showIds);
            return venue;
        }
    }
}