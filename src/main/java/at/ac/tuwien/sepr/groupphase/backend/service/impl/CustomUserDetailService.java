package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.MethodNotAllowedException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.entity.PasswordResetToken;
import at.ac.tuwien.sepr.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PasswordTokenRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.SeenNewsRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.EmailService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import at.ac.tuwien.sepr.groupphase.backend.validator.UserValidator;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final PasswordTokenRepository passwordTokenRepository;
    private final SeenNewsRepository seenNewsRepository;
    private final TicketRepository ticketRepository;
    private final OrderRepository orderRepository;
    private final EmailService emailService;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 30 * 60 * 1000; // 30 minutes in milliseconds

    public CustomUserDetailService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                   JwtTokenizer jwtTokenizer, UserValidator userValidator, UserMapper userMapper,
                                   PasswordTokenRepository passwordTokenRepository, SeenNewsRepository seenNewsRepository, TicketRepository ticketRepository, OrderRepository orderRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.userValidator = userValidator;
        this.userMapper = userMapper;
        this.passwordTokenRepository = passwordTokenRepository;
        this.seenNewsRepository = seenNewsRepository;
        this.ticketRepository = ticketRepository;
        this.orderRepository = orderRepository;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.debug("Load all user by email");
        try {
            ApplicationUser applicationUser = findApplicationUserByEmail(email);

            if (applicationUser.getBlocked()) {
                throw new MethodNotAllowedException(String.format("User with email %s is blocked", email), List.of(email));
            }

            List<GrantedAuthority> grantedAuthorities;
            if (applicationUser.getAdmin()) {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
            } else {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }

            return new User(applicationUser.getEmail(), applicationUser.getPassword(), grantedAuthorities);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ApplicationUser findApplicationUserByEmail(String email) throws NotFoundException {
        LOGGER.debug("Find application user by email");
        ApplicationUser applicationUser = userRepository.findByEmail(email);
        if (applicationUser == null) {
            throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
        }
        return applicationUser;
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        ApplicationUser user = userRepository.findByEmail(userLoginDto.getEmail());
        if (user == null) {
            throw new BadCredentialsException("Username or password is incorrect");
        }

        // Check if account is blocked
        if (user.getBlocked()) {
            if (user.getLockTime() != null && new Date().getTime() - user.getLockTime().getTime() >= LOCK_TIME_DURATION) {
                // Unlock the account if lock duration has passed
                try {
                    unblockUser(user.getEmail());
                } catch (ConflictException e) {
                    //already checked if user is blocked
                }
            } else {
                throw new BadCredentialsException("Account is locked. Please try again later or reset your password.");
            }
        }

        UserDetails userDetails = loadUserByUsername(userLoginDto.getEmail());
        if (userDetails != null
            && userDetails.isAccountNonExpired()
            && userDetails.isCredentialsNonExpired()
            && passwordEncoder.matches(userLoginDto.getPassword(), userDetails.getPassword())
        ) {
            // Reset failed attempts on successful login
            if (user.getFailedLoginAttempts() > 0) {
                resetFailedAttempts(user);
            }

            List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
        }

        // Increment failed attempts
        increaseFailedAttempts(user);

        throw new BadCredentialsException("Username or password is incorrect");
    }



    @Override
    public void resetPassword(String email, HttpServletRequest request) {
        LOGGER.info("Reset password for email: {}", email);
        ApplicationUser user = findApplicationUserByEmail(email);
        String token = UUID.randomUUID().toString();
        String resetToken = createPasswordResetTokenForUser(user, token);
        String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;

        try {
            String subject = "Ticketline | Password Reset";
            String content = "Hello " + user.getFirstName() + " " + user.getLastName() + ", \n\n"
                + "You have forgotten your password? Here you can set a new one:\n"
                + resetLink + "\nIf you did not request a password reset, please ignore this email.\n\n"
                + "Best regards,\nTicketline Team";
            emailService.sendEmail(email, subject, content);
        } catch (IOException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public String createPasswordResetTokenForUser(ApplicationUser user, String token) {
        // Check if a token already exists for the user
        Optional<PasswordResetToken> existingToken = passwordTokenRepository.findByUser(user);
        if (existingToken.isPresent()) {
            // Log the existing token
            LOGGER.info("Existing token found for user: {}", existingToken.get());
            // Delete the old token
            passwordTokenRepository.delete(existingToken.get());
            LOGGER.info("Existing token deleted");
        } else {
            LOGGER.info("No existing token found for user");
        }

        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
        return token;
    }

    public void changeUserPassword(ApplicationUser user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        // Unblock user when password is reset
        user.setBlocked(false);
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);
    }

    @Override
    public Optional<ApplicationUser> getUserByPasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordTokenRepository.findByToken(token);
        if (passwordResetToken != null) {
            return Optional.of(passwordResetToken.getUser());
        }
        return Optional.empty();
    }


    @Override
    public UserDetailDto createUser(@Valid UserCreateDto user) throws ConflictException, ValidationException {
        LOGGER.debug("Attempting to create user with email: {}", user.getEmail());

        userValidator.validateUserCreation(user);

        if (this.userRepository.findByEmail(user.getEmail()) != null) {
            throw new ConflictException("Email already exists!",
                List.of("The email address " + user.getEmail() + " is already in use"));
        }

        ApplicationUser newUser = userMapper.userCreateDtoToApplicationUser(user);
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        ApplicationUser savedUser = userRepository.save(newUser);

        return userMapper.applicationUserToUserDetailDto(savedUser);
    }

    @Override
    public ApplicationUser registerUser(UserRegistrationDto userRegistrationDto) throws ConflictException, ValidationException {
        LOGGER.info("Registering new user with email: {}", userRegistrationDto.getEmail());
        // Validate user registration data
        userValidator.validateUserRegistration(userRegistrationDto);

        // Check if email is already taken
        if (userRepository.findByEmail(userRegistrationDto.getEmail()) != null) {
            throw new ConflictException("Email address is already registered",
                List.of("The email address " + userRegistrationDto.getEmail() + " is already in use"));
        }

        // Map DTO to entity
        ApplicationUser applicationUser = userMapper.userRegistrationDtoToApplicationUser(userRegistrationDto);
        applicationUser.setPassword(passwordEncoder.encode(applicationUser.getPassword()));

        // Save user
        ApplicationUser savedUser = userRepository.save(applicationUser);

        return savedUser;
    }

    @Override
    public String update(String email, UserUpdateDto userUpdateDto) throws ValidationException, ConflictException, NotFoundException {
        LOGGER.debug("Updating user with email: {}", email);

        // Validate update data
        userValidator.validateUserUpdate(userUpdateDto);

        // Check if user exists
        ApplicationUser existingUser = findApplicationUserByEmail(email);

        // Check if new email is already taken by another user
        if (!email.equals(userUpdateDto.getEmail())) {
            ApplicationUser userWithNewEmail = userRepository.findByEmail(userUpdateDto.getEmail());
            if (userWithNewEmail != null) {
                throw new ConflictException("Email address is already registered",
                    List.of("The email address " + userUpdateDto.getEmail() + " is already in use by another user"));
            }
        }

        // Update user fields
        existingUser.setFirstName(userUpdateDto.getFirstName());
        existingUser.setLastName(userUpdateDto.getLastName());
        existingUser.setAddress(userUpdateDto.getAddress());
        existingUser.setDateOfBirth(userUpdateDto.getDateOfBirth());
        existingUser.setEmail(userUpdateDto.getEmail());

        // Remove password update logic

        // Save updated user
        ApplicationUser savedUser = userRepository.save(existingUser);

        // Generate new token
        List<String> roles = savedUser.getAdmin()
            ? List.of("ROLE_ADMIN", "ROLE_USER")
            : List.of("ROLE_USER");

        return jwtTokenizer.getAuthToken(savedUser.getEmail(), roles);
    }

    @Override
    @Transactional
    public void deleteUser(String email) throws NotFoundException {
        LOGGER.debug("Deleting user with email: {}", email);
        ApplicationUser user = findApplicationUserByEmail(email);


        // Delete associated SeenNews entries
        seenNewsRepository.deleteByUser(user);

        // Delete reserved tickets and tickets in cart
        ticketRepository.deleteByUserAndReservedTrue(user);
        ticketRepository.deleteByUserAndInCartTrue(user);

        // Set user to null for bought tickets
        List<Ticket> boughtTickets = ticketRepository.findByUserAndPurchasedTrue(user);
        for (Ticket ticket : boughtTickets) {
            ticket.setUser(null);
            ticketRepository.save(ticket);
        }

        // Handle orders
        List<Order> orders = orderRepository.findByUser(user);
        for (Order order : orders) {
            order.setUser(null);
            orderRepository.save(order);
        }

        userRepository.delete(user);
    }


    private void increaseFailedAttempts(ApplicationUser user) {
        int newFailAttempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(newFailAttempts);

        if (newFailAttempts >= MAX_FAILED_ATTEMPTS) {
            user.setBlocked(true);
            user.setLockTime(new Date());
        }

        userRepository.save(user);
    }

    private void resetFailedAttempts(ApplicationUser user) {
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);
    }

    @Override
    public void blockUser(String email) throws ConflictException {
        LOGGER.info("Block user with email: {}", email);
        ApplicationUser user = userRepository.findByEmail(email);
        if (user != null) {
            if (user.getAdmin()) {
                throw new MethodNotAllowedException(String.format("User with email %s is an admin and cannot be blocked", email), List.of(email));
            }
            if (user.getBlocked()) {
                throw new ConflictException("User with email %s is already blocked", List.of(email));
            } else {
                user.setBlocked(true);
                userRepository.save(user);
            }
        } else {
            throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
        }
    }

    @Override
    public void unblockUser(String email) throws ConflictException {
        LOGGER.info("Unblocking user with email: {}", email);
        ApplicationUser user = userRepository.findByEmail(email);
        if (user != null) {
            if (user.getAdmin()) {
                throw new MethodNotAllowedException(String.format("User with email %s is an admin and cannot be blocked", email), List.of(email));
            }
            if (!user.getBlocked()) {
                throw new ConflictException("User with email %s is currently not blocked", List.of(email));
            }
            user.setBlocked(false);
            userRepository.save(user);
        } else {
            throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
        }
    }

    @Override
    public List<UserDetailDto> getBlockedUsers() {
        LOGGER.info("Getting all blocked users");
        return userRepository.findByBlocked(true).stream()
            .map(userMapper::applicationUserToUserDetailDto)
            .toList();
    }

    @Override
    public ApplicationUser findApplicationUserById(Long id) throws NotFoundException {
        LOGGER.debug("Find application user by id {}", id);
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("Could not find the user with ID %d", id)));
    }

    @Override
    public List<UserDetailDto> getAllUsers() {
        LOGGER.debug("Find all users");
        return userRepository.findAll().stream()
            .map(userMapper::applicationUserToUserDetailDto)
            .toList();
    }
}
