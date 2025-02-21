package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Data transfer object for creating a user.
 */
public class UserCreateDto {

    @NotBlank(message = "First name is mandatory")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Size(max = 100)
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Address is mandatory")
    @Size(max = 200)
    private String address;

    @NotNull(message = "Date of birth is mandatory")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password should have at least 6 characters")
    @Size(max = 100)
    private String password;

    @NotNull(message = "Admin status must not be null")
    private Boolean admin;

    // Constructors
    public UserCreateDto() {
    }

    public UserCreateDto(String firstName, String lastName, String email, String address, Date dateOfBirth,
                         String password, Boolean admin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.password = password;
        this.admin = admin;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.format(dateOfBirth);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd");
        }
        this.dateOfBirth = dateOfBirth;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    // Builder class
    public static final class UserDtoBuilder {
        private String firstName;
        private String lastName;
        private String email;
        private String address;
        private Date dateOfBirth;
        private String password;
        private Boolean admin;

        private UserDtoBuilder() {
        }

        public static UserDtoBuilder aUserDto() {
            return new UserDtoBuilder();
        }

        public UserDtoBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserDtoBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserDtoBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public UserDtoBuilder withAddress(String address) {
            this.address = address;
            return this;
        }

        public UserDtoBuilder withDateOfBirth(Date dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public UserDtoBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserDtoBuilder withAdmin(Boolean admin) {
            this.admin = admin;
            return this;
        }

        public UserCreateDto build() {
            UserCreateDto userCreateDto = new UserCreateDto(firstName, lastName, email, address, dateOfBirth, password, admin);
            userCreateDto.setFirstName(firstName);
            userCreateDto.setLastName(lastName);
            userCreateDto.setEmail(email);
            userCreateDto.setAddress(address);
            userCreateDto.setDateOfBirth(dateOfBirth);
            userCreateDto.setPassword(password);
            userCreateDto.setAdmin(admin);
            return userCreateDto;
        }
    }
}