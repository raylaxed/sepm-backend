package at.ac.tuwien.sepr.groupphase.backend.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Validator class for user-related operations.
 * This class provides validation logic for user registration data to ensure all submitted
 * information meets the required criteria including length restrictions and format requirements.
 */
@Component
public class UserValidator {

    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_ADDRESS_LENGTH = 200;
    private static final int MAX_PASSWORD_LENGTH = 72; // Common max length for bcrypt
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MIN_AGE_YEARS = 0;
    private static final int MAX_AGE_YEARS = 120;

    /**
     * Common validation for user fields used in both registration and updates.
     *
     * @param isUpdate flag to indicate if this is an update operation where password is optional
     */
    private void validateUserCommonFields(String firstName, String lastName, String address,
                                          Date dateOfBirth, String email, String password,
                                          List<String> validationErrors, boolean isUpdate) {
        // Validate first name
        if (isNullOrEmpty(firstName)) {
            validationErrors.add("First name is required");
        } else if (firstName.length() > MAX_NAME_LENGTH) {
            validationErrors.add("First name cannot exceed " + MAX_NAME_LENGTH + " characters");
        }

        // Validate last name
        if (isNullOrEmpty(lastName)) {
            validationErrors.add("Last name is required");
        } else if (lastName.length() > MAX_NAME_LENGTH) {
            validationErrors.add("Last name cannot exceed " + MAX_NAME_LENGTH + " characters");
        }

        // Validate address
        if (isNullOrEmpty(address)) {
            validationErrors.add("Address is required");
        } else if (address.length() > MAX_ADDRESS_LENGTH) {
            validationErrors.add("Address cannot exceed " + MAX_ADDRESS_LENGTH + " characters");
        }

        // Validate date of birth
        if (dateOfBirth == null) {
            validationErrors.add("Date of birth is required");
        } else {
            validateDateOfBirth(dateOfBirth, validationErrors);
        }

        // Validate email
        if (isNullOrEmpty(email)) {
            validationErrors.add("Email is required");
        } else if (!isValidEmail(email)) {
            validationErrors.add("Invalid email format");
        }

        // Validate password - different rules for update vs registration
        if (!isUpdate) {
            // For registration, password is required
            if (isNullOrEmpty(password)) {
                validationErrors.add("Password is required");
            } else if (password.length() > MAX_PASSWORD_LENGTH) {
                validationErrors.add("Password cannot exceed " + MAX_PASSWORD_LENGTH + " characters");
            } else if (password.length() < MIN_PASSWORD_LENGTH) {
                validationErrors.add("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
            }
        }
    }

    /**
     * Validates user registration data against defined business rules.
     *
     * @param userRegistrationDto the DTO containing user registration information to validate
     * @throws ValidationException if any validation rules are violated. The exception contains
     *                             a list of all validation errors that occurred
     */
    public void validateUserRegistration(UserRegistrationDto userRegistrationDto) throws ValidationException {
        List<String> validationErrors = new ArrayList<>();

        validateUserCommonFields(
            userRegistrationDto.getFirstName(),
            userRegistrationDto.getLastName(),
            userRegistrationDto.getAddress(),
            userRegistrationDto.getDateOfBirth(),
            userRegistrationDto.getEmail(),
            userRegistrationDto.getPassword(),
            validationErrors,
            false  // This is not an update
        );

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("User registration validation failed", validationErrors);
        }
    }

    /**
     * Validates user update data against defined business rules.
     *
     * @param userUpdateDto the DTO containing user update information to validate
     * @throws ValidationException if any validation rules are violated. The exception contains
     *                             a list of all validation errors that occurred
     */
    public void validateUserUpdate(UserUpdateDto userUpdateDto) throws ValidationException {
        List<String> validationErrors = new ArrayList<>();

        validateUserCommonFields(
            userUpdateDto.getFirstName(),
            userUpdateDto.getLastName(),
            userUpdateDto.getAddress(),
            userUpdateDto.getDateOfBirth(),
            userUpdateDto.getEmail(),
            null,
            validationErrors,
            true  // This is an update
        );

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("User update validation failed", validationErrors);
        }
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private void validateDateOfBirth(Date dateOfBirth, List<String> validationErrors) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        // Check if date is in the future
        if (dateOfBirth.after(new Date())) {
            validationErrors.add("Date of birth cannot be in the future");
            return;
        }

        // Calculate age
        Calendar birthCal = Calendar.getInstance();
        birthCal.setTime(dateOfBirth);
        int age = cal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);

        // Adjust age if birthday hasn't occurred this year
        if (cal.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        if (age > MAX_AGE_YEARS) {
            validationErrors.add("Age cannot be more than " + MAX_AGE_YEARS + " years");
        }
        if (age < MIN_AGE_YEARS) {
            validationErrors.add("Age cannot be less than " + MIN_AGE_YEARS + " years");
        }
    }

    /**
     * Validates user creation data against defined business rules.
     *
     * @param userCreateDto the DTO containing user registration information to validate
     * @throws ValidationException if any validation rules are violated. The exception contains
     *                             a list of all validation errors that occurred
     */
    public void validateUserCreation(UserCreateDto userCreateDto) throws ValidationException {
        List<String> validationErrors = new ArrayList<>();

        validateUserCommonFields(
            userCreateDto.getFirstName(),
            userCreateDto.getLastName(),
            userCreateDto.getAddress(),
            userCreateDto.getDateOfBirth(),
            userCreateDto.getEmail(),
            userCreateDto.getPassword(),
            validationErrors,
            false  // This is not an update
        );

        if (userCreateDto.getAdmin() == null) {
            validationErrors.add("Admin status must not be null");
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException("User registration validation failed", validationErrors);
        }
    }

}