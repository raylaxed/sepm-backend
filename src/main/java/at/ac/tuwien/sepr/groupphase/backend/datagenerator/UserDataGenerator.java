package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Data generator for creating default user accounts in the application.
 * This component is only active when the "generateData" profile is enabled.
 * It automatically generates a default user and admin account if no users exist in the database.
 */
@Profile("generateData")
@Component
public class UserDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_USERS_TO_GENERATE = 1000;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new UserDataGenerator.
     *
     * @param userRepository   Repository for user data persistence
     * @param passwordEncoder  Encoder for securing user passwords
     */
    public UserDataGenerator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Generates default user accounts after bean construction.
     * Creates a regular user and an admin user if no users exist in the database.
     */
    @PostConstruct
    private void generateUsers() throws ParseException {
        LOGGER.debug("checking default users");

        if (userRepository.count() > 0) {
            LOGGER.debug("users already exist, skipping generation");
            return;
        }

        final List<ApplicationUser> usersToSave = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String myEmail = "sepr.ticketline@proton.me";
        final String defaultUserEmail = "user@email.com";
        final String defaultAdminEmail = "admin@email.com";

        // Create "me" user
        ApplicationUser me = new ApplicationUser();
        me.setFirstName("lucy");
        me.setLastName("ichhhh");
        me.setEmail(myEmail);
        me.setAddress("only for testing gasse 1");
        me.setDateOfBirth(dateFormat.parse("2000-01-01"));
        me.setPassword(passwordEncoder.encode("password"));
        me.setAdmin(false);
        usersToSave.add(me);
        LOGGER.debug("created default user me {}", me.getEmail());

        // Create default user
        ApplicationUser user = new ApplicationUser();
        user.setFirstName("Default");
        user.setLastName("User");
        user.setEmail(defaultUserEmail);
        user.setAddress("Default Street 1, 12345 Default City");
        user.setDateOfBirth(dateFormat.parse("2000-01-01"));
        user.setPassword(passwordEncoder.encode("password"));
        user.setAdmin(false);
        usersToSave.add(user);
        LOGGER.debug("created default user {}", user.getEmail());

        // Create admin user
        ApplicationUser admin = new ApplicationUser();
        admin.setFirstName("Default");
        admin.setLastName("Admin");
        admin.setEmail(defaultAdminEmail);
        admin.setAddress("Admin Street 1, 12345 Admin City");
        admin.setDateOfBirth(dateFormat.parse("2000-01-01"));
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setAdmin(true);
        usersToSave.add(admin);
        LOGGER.debug("created default admin {}", admin.getEmail());

        // Generate additional users
        for (int i = 0; i < NUMBER_OF_USERS_TO_GENERATE; i++) {
            ApplicationUser additionalUser = new ApplicationUser();
            additionalUser.setFirstName("FirstName" + i);
            additionalUser.setLastName("Lastname" + i);
            additionalUser.setEmail("user" + i + "@email.com");
            additionalUser.setAddress("Default Street " + i + ", 12345 Default City");

            int year = 1960 + (int) (Math.random() * 45);
            int month = 1 + (int) (Math.random() * 12);
            int day = 1 + (int) (Math.random() * 28);
            additionalUser.setDateOfBirth(dateFormat.parse(String.format("%d-%02d-%02d", year, month, day)));

            additionalUser.setPassword(passwordEncoder.encode("password"));
            additionalUser.setAdmin(false);
            usersToSave.add(additionalUser);
            LOGGER.debug("created additional user {}", additionalUser.getEmail());
        }

        // Save all users in one batch
        LOGGER.debug("saving {} users to database", usersToSave.size());
        userRepository.saveAll(usersToSave);
    }
}