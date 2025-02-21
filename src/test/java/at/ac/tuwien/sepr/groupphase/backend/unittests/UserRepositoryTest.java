package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest implements TestData {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void givenNothing_whenSaveUser_thenFindListWithOneElementAndFindUserByEmail() {
        ApplicationUser user = new ApplicationUser();
        user.setFirstName(TEST_USER_FIRSTNAME);
        user.setLastName(TEST_USER_LASTNAME);
        user.setEmail(TEST_USER_EMAIL);
        user.setPassword(TEST_USER_PASSWORD);
        user.setAddress(TEST_USER_ADDRESS);
        user.setDateOfBirth(TEST_USER_DOB);
        user.setAdmin(false);

        userRepository.save(user);

        assertAll(
            () -> assertEquals(1, userRepository.findAll().size()),
            () -> assertNotNull(userRepository.findByEmail(TEST_USER_EMAIL)),
            () -> assertEquals(TEST_USER_FIRSTNAME, userRepository.findByEmail(TEST_USER_EMAIL).getFirstName())
        );
    }

    @Test
    public void testFindUserByEmail() {
        // Create a new user
        ApplicationUser user = new ApplicationUser();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("password");

        // Save the user
        userRepository.save(user);

        // Retrieve the user by email
        ApplicationUser retrievedUser = userRepository.findByEmail("test@example.com");

        // Verify the user was found
        assertTrue(retrievedUser != null);
    }
}