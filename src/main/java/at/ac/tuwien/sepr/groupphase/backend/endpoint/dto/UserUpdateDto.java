package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.Date;

/**
 * Data Transfer Object (DTO) for user updates.
 * This class encapsulates the data required during the user update process.
 */
public class UserUpdateDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private Date dateOfBirth;
    private String email;
    
    // Default constructor
    public UserUpdateDto() {
    }

    // Constructor with all fields
    public UserUpdateDto(Long id, String firstName, String lastName, String address, 
                        Date dateOfBirth, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
    }

    // Add id getter and setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


} 