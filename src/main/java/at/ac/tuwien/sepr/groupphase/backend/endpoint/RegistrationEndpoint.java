package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreatedUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

/**
 * REST endpoint for handling user registration operations.
 * This endpoint provides functionality to register new users in the system.
 */
@RestController
@RequestMapping(value = "/api/v1/registration")
public class RegistrationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final UserMapper userMapper;

    public RegistrationEndpoint(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * Registers a new user in the system.
     *
     * @param userRegistrationDto the registration data of the user to be created
     * @return CreatedUserDto containing the information of the newly registered user
     * @throws ValidationException if the registration data is invalid
     * @throws ConflictException if the user already exists or other conflict occurs
     */
    @PermitAll
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user")
    public CreatedUserDto register(@Valid @RequestBody UserRegistrationDto userRegistrationDto) 
        throws ValidationException, ConflictException {
        LOGGER.info("POST /api/v1/registration body: {}", userRegistrationDto);
        ApplicationUser registeredUser = userService.registerUser(userRegistrationDto);
        return userMapper.applicationUserToCreatedUserDto(registeredUser);
    }
} 