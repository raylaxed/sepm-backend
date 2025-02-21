package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.Date;

/**
 * Data Transfer Object (DTO) for user registration.
 * This class encapsulates the data required during the user registration process.
 */
public class UserRegistrationDto {
    private String firstName;
    private String lastName;
    private String address;
    private Date dateOfBirth;
    private String email;
    private String password;
    
    /**
     * Default constructor for UserRegistrationDto.
     */
    public UserRegistrationDto() {
    }

    /**
     * Creates a new UserRegistrationDto with all fields initialized.
     *
     * @param firstName   The user's first name
     * @param lastName    The user's last name
     * @param address     The user's address
     * @param dateOfBirth The user's date of birth
     * @param email      The user's email address
     * @param password   The user's password
     */
    public UserRegistrationDto(String firstName, String lastName, String address, 
                             Date dateOfBirth, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
} 