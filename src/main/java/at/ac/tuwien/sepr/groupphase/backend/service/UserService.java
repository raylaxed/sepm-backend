package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import jakarta.validation.Valid;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Service for user related operations.
 */
public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address.
     * <br>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Find an application user based on the email address.
     *
     * @param email the email address
     * @return a application user
     * @throws NotFoundException if the user with the given email does not exist
     */
    ApplicationUser findApplicationUserByEmail(String email) throws NotFoundException;

    /**
     * Log in a user.
     *
     * @param userLoginDto login credentials
     * @return the JWT, if successful
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are bad
     */
    String login(UserLoginDto userLoginDto);

    /**
     * Register a new user.
     *
     * @param userRegistrationDto the user registration data
     * @return the created ApplicationUser
     * @throws ValidationException if the data is invalid
     * @throws ConflictException if the email is already taken
     */
    ApplicationUser registerUser(UserRegistrationDto userRegistrationDto) throws ValidationException, ConflictException, at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

    /**
     * Create a new user.
     *
     * @param testUser the user to create
     * @return the created user
     * @throws ConflictException if the user already exists
     * @throws ValidationException if the user is invalid
     */
    UserDetailDto createUser(@Valid UserCreateDto testUser) throws ConflictException, ValidationException, at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

    /**
     * Reset the password of a user.
     *
     * @param email the email address of the user
     * @param request the request
     */
    void resetPassword(String email, HttpServletRequest request);

    /**
     * Create a password reset token for a user.
     *
     * @param user the user
     * @param token the token
     * @return the token
     */
    String createPasswordResetTokenForUser(ApplicationUser user, String token);

    /**
     * Change the password of a user.
     *
     * @param user the user
     * @param password the password
     */
    void changeUserPassword(ApplicationUser user, String password);

    /**
     * Get a user by a password reset token.
     *
     * @param token the token
     * @return the user
     */
    Optional<ApplicationUser> getUserByPasswordResetToken(String token);

    /**
     * Update an existing user and return a new token if email changed.
     *
     * @param email the email of the user to update
     * @param userUpdateDto the user update data
     * @return the updated ApplicationUser and new token
     * @throws ValidationException if the data is invalid
     * @throws ConflictException if the email is already taken by another user
     * @throws NotFoundException if the user with the given email does not exist
     */
    String update(String email, UserUpdateDto userUpdateDto) throws ValidationException, ConflictException, NotFoundException;


    /**
     * Delete a user based on their email address.
     *
     * @param email the email address of the user to delete
     * @throws NotFoundException if no user with the given email exists
     */
    void deleteUser(String email) throws NotFoundException;

    /**
     * Block a user.
     *
     * @param email the email address of the user to block
     */
    void blockUser(String email) throws ConflictException;

    /**
     * Unblock a user.
     *
     * @param email the email address of the user to unblock
     */
    void unblockUser(String email) throws ConflictException;

    /**
     * Get all blocked users.
     *
     * @return a list of blocked users
     */
    List<UserDetailDto> getBlockedUsers();

    /**
     * Find an application user based on the ID.
     *
     * @param id the user ID
     * @return the application user
     * @throws NotFoundException if the user with the given ID does not exist
     */
    ApplicationUser findApplicationUserById(Long id) throws NotFoundException;

    /**
     * Find all users.
     *
     * @return a list of all users
     */
    List<UserDetailDto> getAllUsers();
}
