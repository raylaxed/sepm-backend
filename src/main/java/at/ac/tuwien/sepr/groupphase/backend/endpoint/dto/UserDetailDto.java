package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.Objects;

public class UserDetailDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;
    private String password;
    private boolean admin;
    private boolean blocked;

    public UserDetailDto() {
    }

    public UserDetailDto(Long id, String firstName, String lastName, String email, String address, Date dateOfBirth, String password, boolean admin) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.password = password;
        this.admin = admin;
    }

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
        this.dateOfBirth = dateOfBirth;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserDetailDto that)) {
            return false;
        }
        return admin == that.admin
            && Objects.equals(id, that.id)
            && Objects.equals(firstName, that.firstName)
            && Objects.equals(lastName, that.lastName)
            && Objects.equals(email, that.email)
            && Objects.equals(address, that.address)
            && Objects.equals(dateOfBirth, that.dateOfBirth)
            && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, address, dateOfBirth, password, admin);
    }

    @Override
    public String toString() {
        return "UserDetailDto{"
            + "id=" + id
            + ", firstName='" + firstName + '\''
            + ", lastName='" + lastName + '\''
            + ", email='" + email + '\''
            + ", address='" + address + '\''
            + ", dateOfBirth='" + dateOfBirth + '\''
            + ", password='" + password + '\''
            + ", admin=" + admin
            + ", blocked=" + blocked
            + '}';
    }

    public static final class UserDetailDtoBuilder {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String address;
        private String password;
        private Date dateOfBirth;
        private boolean admin;
        private boolean blocked;

        private UserDetailDtoBuilder() {
        }

        public static UserDetailDtoBuilder aUserDetailDto() {
            return new UserDetailDtoBuilder();
        }

        public UserDetailDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public UserDetailDtoBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserDetailDtoBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserDetailDtoBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public UserDetailDtoBuilder withAddress(String address) {
            this.address = address;
            return this;
        }

        public UserDetailDtoBuilder withDateOfBirth(Date dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public UserDetailDtoBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserDetailDtoBuilder withAdmin(boolean admin) {
            this.admin = admin;
            return this;
        }

        public UserDetailDtoBuilder withBlocked(boolean blocked) {
            this.blocked = blocked;
            return this;
        }

        public UserDetailDto build() {
            UserDetailDto userDetailDto = new UserDetailDto(id, firstName, lastName, email, address, dateOfBirth, password, admin);
            userDetailDto.setId(id);
            userDetailDto.setFirstName(firstName);
            userDetailDto.setLastName(lastName);
            userDetailDto.setEmail(email);
            userDetailDto.setAddress(address);
            userDetailDto.setDateOfBirth(dateOfBirth);
            userDetailDto.setPassword(password);
            userDetailDto.setAdmin(admin);
            userDetailDto.setBlocked(blocked);
            return userDetailDto;
        }
    }
}