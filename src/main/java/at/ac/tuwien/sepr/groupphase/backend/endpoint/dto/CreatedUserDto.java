package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

/**
 * Data Transfer Object (DTO) representing a newly created user.
 * This class is used to transfer user information after successful user creation,
 * containing essential user details such as ID, email, and name information.
 */
public class CreatedUserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
} 