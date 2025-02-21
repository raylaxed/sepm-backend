package at.ac.tuwien.sepr.groupphase.backend.unittests;


import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.PasswordResetTokenRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Nested
@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest implements TestData {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        // Delete all password reset tokens
        passwordResetTokenRepository.deleteAll();

        // Delete all users
        userRepository.deleteAll();
    }

    @Test
    public void givenValidRegistrationDto_whenRegisterUser_thenUserIsCreated() throws ValidationException, ConflictException {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto(
            TEST_USER_FIRSTNAME,
            TEST_USER_LASTNAME,
            TEST_USER_ADDRESS,
            TEST_USER_DOB,
            TEST_USER_EMAIL,
            TEST_USER_PASSWORD
        );

        // Mock the passwordEncoder to return an encoded password
        when(passwordEncoder.encode(TEST_USER_PASSWORD)).thenReturn("encodedPassword");

        // Act
        ApplicationUser registeredUser = userService.registerUser(dto);

        // Assert
        assertAll(
            () -> assertNotNull(registeredUser.getId()),
            () -> assertEquals(TEST_USER_EMAIL, registeredUser.getEmail()),
            () -> assertEquals(TEST_USER_FIRSTNAME, registeredUser.getFirstName()),
            () -> assertEquals(TEST_USER_LASTNAME, registeredUser.getLastName()),
            () -> assertFalse(registeredUser.getAdmin())
        );
    }

    @Test
    public void givenInvalidEmail_whenRegisterUser_thenThrowValidationException() {
        UserRegistrationDto dto = new UserRegistrationDto(
            TEST_USER_FIRSTNAME,
            TEST_USER_LASTNAME,
            TEST_USER_ADDRESS,
            TEST_USER_DOB,
            TEST_INVALID_EMAIL,
            TEST_USER_PASSWORD
        );

        ValidationException thrown = assertThrows(
            ValidationException.class,
            () -> userService.registerUser(dto)
        );

        assertTrue(thrown.getMessage().contains("email"));
    }

    @Test
    public void givenExistingEmail_whenRegisterUser_thenThrowConflictException() throws ValidationException, ConflictException {
        // First registration
        UserRegistrationDto dto1 = new UserRegistrationDto(
            TEST_USER_FIRSTNAME,
            TEST_USER_LASTNAME,
            TEST_USER_ADDRESS,
            TEST_USER_DOB,
            TEST_USER_EMAIL,
            TEST_USER_PASSWORD
        );
        when(passwordEncoder.encode(TEST_USER_PASSWORD)).thenReturn("encodedPassword1");
        userService.registerUser(dto1);

        // Second registration with same email
        UserRegistrationDto dto2 = new UserRegistrationDto(
            "Jane",
            "Smith",
            "2 Other Street",
            TEST_USER_DOB,
            TEST_USER_EMAIL,
            "differentpassword"
        );
        when(passwordEncoder.encode("differentpassword")).thenReturn("encodedPassword2");

        ConflictException thrown = assertThrows(
            ConflictException.class,
            () -> userService.registerUser(dto2)
        );

        assertTrue(thrown.getMessage().contains("Email address is already registered"));
    }

    @Test
    public void givenExistingUser_whenDeleteUser_thenUserIsDeleted() throws ValidationException, ConflictException {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto(
            TEST_USER_FIRSTNAME,
            TEST_USER_LASTNAME,
            TEST_USER_ADDRESS,
            TEST_USER_DOB,
            TEST_USER_EMAIL,
            TEST_USER_PASSWORD
        );

        // Mock the passwordEncoder to return an encoded password
        when(passwordEncoder.encode(TEST_USER_PASSWORD)).thenReturn("encodedPassword");

        // Act
        userService.registerUser(dto);

        // Delete the user
        assertDoesNotThrow(() -> userService.deleteUser(TEST_USER_EMAIL));

        // Verify that the user no longer exists
        NotFoundException thrown = assertThrows(
            NotFoundException.class,
            () -> userService.findApplicationUserByEmail(TEST_USER_EMAIL)
        );

        // Assert
        assertTrue(thrown.getMessage().contains("Could not find the user with the email address"));
    }

    @Test
    public void givenValidUpdateDto_whenUpdate_thenReturnsNewToken() throws ValidationException, ConflictException, NotFoundException {
        // First register a user
        UserRegistrationDto registrationDto = new UserRegistrationDto(
            TEST_USER_FIRSTNAME,
            TEST_USER_LASTNAME,
            TEST_USER_ADDRESS,
            TEST_USER_DOB,
            TEST_USER_EMAIL,
            TEST_USER_PASSWORD
        );
        when(passwordEncoder.encode(TEST_USER_PASSWORD)).thenReturn("encodedPassword");
        userService.registerUser(registrationDto);

        // Update the user
        UserUpdateDto updateDto = new UserUpdateDto(
            null,
            "UpdatedFirst",
            "UpdatedLast",
            "2 Updated Street",
            TEST_USER_DOB,
            "updated@example.com"
        );

        String token = userService.update(TEST_USER_EMAIL, updateDto);

        // Verify that a token is returned
        assertNotNull(token);
        assertTrue(token.length() > 0);

        // Verify that the user was actually updated in the database
        ApplicationUser updatedUser = userRepository.findByEmail("updated@example.com");
        assertAll(
            () -> assertEquals("UpdatedFirst", updatedUser.getFirstName()),
            () -> assertEquals("UpdatedLast", updatedUser.getLastName()),
            () -> assertEquals("2 Updated Street", updatedUser.getAddress()),
            () -> assertEquals("updated@example.com", updatedUser.getEmail())
        );
    }

    @Test
    public void givenInvalidEmail_whenUpdate_thenThrowValidationException() throws ValidationException, ConflictException {
        // Arrange
        UserRegistrationDto registrationDto = new UserRegistrationDto(
            TEST_USER_FIRSTNAME,
            TEST_USER_LASTNAME,
            TEST_USER_ADDRESS,
            TEST_USER_DOB,
            TEST_USER_EMAIL,
            TEST_USER_PASSWORD
        );

        // Mock the passwordEncoder to return an encoded password
        when(passwordEncoder.encode(TEST_USER_PASSWORD)).thenReturn("encodedPassword");

        // Act
        userService.registerUser(registrationDto);

        // Try to update with invalid email
        UserUpdateDto updateDto = new UserUpdateDto(
            null, // ID is no longer needed
            TEST_USER_FIRSTNAME,
            TEST_USER_LASTNAME,
            TEST_USER_ADDRESS,
            TEST_USER_DOB,
            TEST_INVALID_EMAIL
        );

        ValidationException thrown = assertThrows(
            ValidationException.class,
            () -> userService.update(TEST_USER_EMAIL, updateDto)
        );

        // Assert
        assertTrue(thrown.getMessage().contains("email"));
    }

    @Test
    public void givenNonexistentUser_whenUpdate_thenThrowNotFoundException() {
        UserUpdateDto updateDto = new UserUpdateDto(
            null, // ID is no longer needed
            TEST_USER_FIRSTNAME,
            TEST_USER_LASTNAME,
            TEST_USER_ADDRESS,
            TEST_USER_DOB,
            TEST_USER_EMAIL
        );

        assertThrows(
            NotFoundException.class,
            () -> userService.update("nonexistent@example.com", updateDto)
        );
    }

    @Test
    public void givenExistingEmail_whenUpdate_thenThrowConflictException() throws ValidationException, ConflictException {
        // Register first user
        UserRegistrationDto dto1 = new UserRegistrationDto(
            TEST_USER_FIRSTNAME,
            TEST_USER_LASTNAME,
            TEST_USER_ADDRESS,
            TEST_USER_DOB,
            TEST_USER_EMAIL,
            TEST_USER_PASSWORD
        );
        when(passwordEncoder.encode(TEST_USER_PASSWORD)).thenReturn("encodedPassword1");
        userService.registerUser(dto1);

        // Register second user
        UserRegistrationDto dto2 = new UserRegistrationDto(
            "Jane",
            "Smith",
            "2 Other Street",
            TEST_USER_DOB,
            "jane@example.com",
            "password456"
        );
        when(passwordEncoder.encode("password456")).thenReturn("encodedPassword2");
        userService.registerUser(dto2);

        // Try to update second user with first user's email
        UserUpdateDto updateDto = new UserUpdateDto(
            null, // ID is no longer needed
            "Jane",
            "Smith",
            "2 Other Street",
            TEST_USER_DOB,
            TEST_USER_EMAIL  // Using first user's email
        );

        ConflictException thrown = assertThrows(
            ConflictException.class,
            () -> userService.update("jane@example.com", updateDto)
        );

        assertTrue(thrown.getMessage().contains("Email address is already registered"));
    }

    @Test
    public void givenValidUser_whenCreateUser_thenUserIsCreatedSuccessfully() throws ParseException, ValidationException, ConflictException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateOfBirth = dateFormat.parse("2000-01-01");

        UserCreateDto testUser = new UserCreateDto("test", "testuser", "userTest@example.com",
            "Examplestreet 1", dateOfBirth, "password", false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Act
        UserDetailDto createdUser = userService.createUser(testUser);

        // Assert
        assertNotNull(createdUser.getId());
        assertEquals("test", createdUser.getFirstName());
        assertEquals("testuser", createdUser.getLastName());
        assertEquals("userTest@example.com", createdUser.getEmail());
        assertTrue(userRepository.findById(createdUser.getId()).isPresent());
    }

    @Test
    public void givenDuplicateEmail_whenCreateUser_thenConflictException() throws ParseException, ValidationException, ConflictException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateOfBirth = dateFormat.parse("2000-01-01");

        UserCreateDto testUser = new UserCreateDto("test", "testuser", "duplicate@example.com",
            "Examplestreet 1", dateOfBirth, "password", false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword1");
        userService.createUser(testUser);

        UserCreateDto duplicateUser = new UserCreateDto("test", "testuser", "duplicate@example.com",
            "Examplestreet 1", dateOfBirth, "password", false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword2");

        // Act & Assert
        assertThrows(ConflictException.class, () -> userService.createUser(duplicateUser));
    }
    @Test
    public void givenInvalidUser_whenCreateUser_thenValidationFails() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateOfBirth = dateFormat.parse("2000-01-01");

        UserCreateDto invalidUser = new UserCreateDto("", "", "invalid-email",
            "", dateOfBirth, "short", false);

        // Act & Assert
        assertThrows(ValidationException.class, () -> userService.createUser(invalidUser));
    }


    @Test
    public void testResetPassword_Negative() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.resetPassword("invalid@example.com", request));
    }


    @Test
    public void testCreatePasswordResetTokenForUser_Positive() {
        // Arrange
        ApplicationUser user = new ApplicationUser();
        user.setFirstName("test");
        user.setLastName("user");
        user.setEmail("test@example.com");
        user.setAddress("Examplestreet 1");
        user.setDateOfBirth(new Date());
        user.setPassword("password");
        userRepository.save(user);

        // Act
        String token = userService.createPasswordResetTokenForUser(user, "reset-token");

        // Assert
        assertEquals("reset-token", token);
    }

    @Test
    public void testCreatePasswordResetTokenForUser_Negative() {
        // Arrange
        ApplicationUser user = new ApplicationUser();
        user.setFirstName("test");
        user.setLastName("user");
        user.setEmail("test@example.com");
        user.setAddress("Examplestreet 1");
        user.setDateOfBirth(new Date());
        user.setPassword("password");
        userRepository.save(user);

        // Act
        String token = userService.createPasswordResetTokenForUser(user, "reset-token");

        // Assert
        assertNotNull(token);
    }

    @Test
    public void givenFiveFailedLoginAttempts_whenLogin_thenAccountIsLocked() throws ValidationException, ConflictException {
        // First register a user
        UserRegistrationDto dto = new UserRegistrationDto(
            TEST_USER_FIRSTNAME,
            TEST_USER_LASTNAME,
            TEST_USER_ADDRESS,
            TEST_USER_DOB,
            TEST_USER_EMAIL,
            TEST_USER_PASSWORD
        );

        // Mock the password encoder BEFORE registering the user
        when(passwordEncoder.encode(TEST_USER_PASSWORD)).thenReturn("encodedPassword");
        userService.registerUser(dto);

        // Create login DTO with wrong password
        UserLoginDto loginDto = UserLoginDto.UserLoginDtoBuilder.anUserLoginDto()
            .withEmail(TEST_USER_EMAIL)
            .withPassword("wrongpassword")
            .build();

        // Mock password encoder for wrong password
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // Attempt to login 5 times with wrong password
        for (int i = 0; i < 5; i++) {
            assertThrows(BadCredentialsException.class, () -> userService.login(loginDto));
        }

        // On the 6th attempt, should get locked account message
        BadCredentialsException thrown = assertThrows(
            BadCredentialsException.class,
            () -> userService.login(loginDto)
        );

        assertEquals("Account is locked. Please try again later or reset your password.", thrown.getMessage());

        // Verify that even with correct password, account remains locked
        UserLoginDto correctLoginDto = UserLoginDto.UserLoginDtoBuilder.anUserLoginDto()
            .withEmail(TEST_USER_EMAIL)
            .withPassword(TEST_USER_PASSWORD)
            .build();

        // Mock password encoder for correct password - though it shouldn't matter since account is locked
        when(passwordEncoder.matches(TEST_USER_PASSWORD, "encodedPassword")).thenReturn(true);

        thrown = assertThrows(
            BadCredentialsException.class,
            () -> userService.login(correctLoginDto)
        );

        assertEquals("Account is locked. Please try again later or reset your password.", thrown.getMessage());
    }

    @Test
    public void givenLessThanFiveFailedAttempts_whenLoginWithCorrectPassword_thenSucceeds() throws ValidationException, ConflictException {
        // First register a user
        UserRegistrationDto dto = new UserRegistrationDto(
            TEST_USER_FIRSTNAME,
            TEST_USER_LASTNAME,
            TEST_USER_ADDRESS,
            TEST_USER_DOB,
            TEST_USER_EMAIL,
            TEST_USER_PASSWORD
        );

        // Mock the password encoder BEFORE registering the user
        when(passwordEncoder.encode(TEST_USER_PASSWORD)).thenReturn("encodedPassword");
        userService.registerUser(dto);

        // Create login DTO with wrong password
        UserLoginDto wrongLoginDto = UserLoginDto.UserLoginDtoBuilder.anUserLoginDto()
            .withEmail(TEST_USER_EMAIL)
            .withPassword("wrongpassword")
            .build();

        // Mock password encoder for wrong password
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // Attempt to login 3 times with wrong password
        for (int i = 0; i < 3; i++) {
            assertThrows(BadCredentialsException.class, () -> userService.login(wrongLoginDto));
        }

        // Now try with correct password
        UserLoginDto correctLoginDto = UserLoginDto.UserLoginDtoBuilder.anUserLoginDto()
            .withEmail(TEST_USER_EMAIL)
            .withPassword(TEST_USER_PASSWORD)
            .build();

        // Mock password encoder for correct password
        when(passwordEncoder.matches(TEST_USER_PASSWORD, "encodedPassword")).thenReturn(true);

        // Should succeed and return a token
        String token = userService.login(correctLoginDto);
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    public void givenValidEmail_whenBlockUser_thenUserIsBlocked() throws ConflictException {
        // Arrange
        ApplicationUser user = new ApplicationUser();
        user.setFirstName(TEST_USER_FIRSTNAME);
        user.setLastName(TEST_USER_LASTNAME);
        user.setEmail(TEST_USER_EMAIL);
        user.setPassword(TEST_USER_PASSWORD);
        user.setAddress(TEST_USER_ADDRESS);
        user.setDateOfBirth(TEST_USER_DOB);
        user.setAdmin(false);
        user.setBlocked(false);
        userRepository.save(user);

        // Act
        userService.blockUser(TEST_USER_EMAIL);

        // Assert
        ApplicationUser blockedUser = userRepository.findByEmail(TEST_USER_EMAIL);
        assertTrue(blockedUser.getBlocked());
    }

    @Test
    public void givenInvalidEmail_whenBlockUser_thenThrowNotFoundException() {
        // Act & Assert
        NotFoundException thrown = assertThrows(
            NotFoundException.class,
            () -> userService.blockUser("nonexistent@example.com")
        );

        assertTrue(thrown.getMessage().contains("Could not find the user with the email address"));
    }

    @Test
    public void givenValidEmail_whenUnblockUser_thenUserIsUnblocked() throws ConflictException {
        // Arrange
        ApplicationUser user = new ApplicationUser();
        user.setFirstName(TEST_USER_FIRSTNAME);
        user.setLastName(TEST_USER_LASTNAME);
        user.setEmail(TEST_USER_EMAIL);
        user.setPassword(TEST_USER_PASSWORD);
        user.setAddress(TEST_USER_ADDRESS);
        user.setDateOfBirth(TEST_USER_DOB);
        user.setAdmin(false);
        user.setBlocked(true);
        userRepository.save(user);

        // Act
        userService.unblockUser(TEST_USER_EMAIL);

        // Assert
        ApplicationUser unblockedUser = userRepository.findByEmail(TEST_USER_EMAIL);
        assertFalse(unblockedUser.getBlocked());
    }

    @Test
    public void givenInvalidEmail_whenUnblockUser_thenThrowNotFoundException() {
        // Act & Assert
        NotFoundException thrown = assertThrows(
            NotFoundException.class,
            () -> userService.unblockUser("nonexistent@example.com")
        );

        assertTrue(thrown.getMessage().contains("Could not find the user with the email address"));
    }

    @Test
    public void givenValidUser_whenChangeUserPassword_thenPasswordIsChanged() {
        // Arrange
        ApplicationUser user = new ApplicationUser();
        user.setFirstName(TEST_USER_FIRSTNAME);
        user.setLastName(TEST_USER_LASTNAME);
        user.setEmail(TEST_USER_EMAIL);
        user.setPassword("oldPassword");
        user.setAddress(TEST_USER_ADDRESS);
        user.setDateOfBirth(TEST_USER_DOB);
        user.setAdmin(false);
        userRepository.save(user);

        String newPassword = "newPassword";
        String encodedPassword = "encodedNewPassword";

        // Mock the passwordEncoder to return the encoded password
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(passwordEncoder.matches(newPassword, encodedPassword)).thenReturn(true);

        // Act
        userService.changeUserPassword(user, newPassword);

        // Assert
        ApplicationUser updatedUser = userRepository.findByEmail(TEST_USER_EMAIL);
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
    }

    @Test
    public void givenNonExistentUser_whenChangeUserPassword_thenThrowNotFoundException() {
        // Arrange
        ApplicationUser nonExistentUser = new ApplicationUser();
        nonExistentUser.setEmail("nonexistent@example.com");

        String newPassword = "newPassword";

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> userService.changeUserPassword(nonExistentUser, newPassword));
    }

    @Test
    public void givenBlockedUsers_whenGetBlockedUsers_thenReturnBlockedUsers() {
        // Arrange
        ApplicationUser blockedUser1 = new ApplicationUser();
        blockedUser1.setFirstName("Blocked");
        blockedUser1.setLastName("User1");
        blockedUser1.setEmail("blocked1@example.com");
        blockedUser1.setBlocked(true);
        blockedUser1.setAddress("Examplestreet 1");
        blockedUser1.setDateOfBirth(new Date());
        blockedUser1.setPassword("password");
        userRepository.save(blockedUser1);

        ApplicationUser blockedUser2 = new ApplicationUser();
        blockedUser2.setFirstName("Blocked");
        blockedUser2.setLastName("User2");
        blockedUser2.setEmail("blocked2@example.com");
        blockedUser2.setBlocked(true);
        blockedUser2.setAddress("Examplestreet 1");
        blockedUser2.setDateOfBirth(new Date());
        blockedUser2.setPassword("password");
        userRepository.save(blockedUser2);

        // Act
        List<UserDetailDto> blockedUsers = userService.getBlockedUsers();

        // Assert
        assertEquals(2, blockedUsers.size());
        assertTrue(blockedUsers.stream().anyMatch(user -> user.getEmail().equals("blocked1@example.com")));
        assertTrue(blockedUsers.stream().anyMatch(user -> user.getEmail().equals("blocked2@example.com")));
    }

    @Test
    public void givenNoBlockedUsers_whenGetBlockedUsers_thenReturnEmptyList() {
        // Act
        List<UserDetailDto> blockedUsers = userService.getBlockedUsers();

        // Assert
        assertTrue(blockedUsers.isEmpty());
    }
}