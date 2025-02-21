package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PasswordDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.service.PasswordResetTokenService;
import at.ac.tuwien.sepr.groupphase.backend.service.SecurityService;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.CustomUserDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/usersAdminControl")
public class UserAdminControlEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CustomUserDetailService customUserDetailService;
    private final UserService userService;
    private final SecurityService securityService;
    private final PasswordResetTokenService passwordResetTokenService;

    @Autowired
    public UserAdminControlEndpoint(CustomUserDetailService customUserDetailService, UserMapper userMapper, UserService userService, SecurityService securityService,
                                    PasswordResetTokenService passwordResetTokenService) {
        this.customUserDetailService = customUserDetailService;
        this.userService = userService;
        this.securityService = securityService;
        this.passwordResetTokenService = passwordResetTokenService;
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/block")
    @Operation(summary = "Block a user")
    public void blockUser(@RequestBody Map<String, String> request) throws ConflictException {
        LOGGER.info("POST /api/v1/admin/users/block");
        String email = request.get("email");
        customUserDetailService.blockUser(email);
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/unblock")
    @Operation(summary = "Unblock a user")
    public void unblockUser(@RequestBody Map<String, String> request) throws ConflictException {
        LOGGER.info("POST /api/v1/admin/users/unblock");
        String email = request.get("email");
        customUserDetailService.unblockUser(email);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/getBlockedUsers")
    @Operation(summary = "Get all blocked users")
    public List<UserDetailDto> getBlockedUsers() {
        LOGGER.info("GET /api/v1/usersAdminControl/getBlockedUsers");
        return customUserDetailService.getBlockedUsers();
    }


    @PostMapping("/resetPassword")
    @PermitAll
    public ResponseEntity<Void> resetPassword(HttpServletRequest request, @RequestParam("email") String email) {
        LOGGER.info("POST /api/v1/usersAdminControl/resetPassword for email: {}", email);

        // Validate email format
        if (!isValidEmail(email)) {
            this.logClientError(HttpStatus.BAD_REQUEST, "Invalid email", new IllegalArgumentException("Invalid email"));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            userService.resetPassword(email, request);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            this.logClientError(HttpStatus.NOT_FOUND, "User not found", e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            this.logClientError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isValidEmail(String email) {
        // Simple email validation logic
        return email != null && email.contains("@");
    }

    @PostMapping(value = "/savePassword", produces = "application/json")
    @PermitAll
    public ResponseEntity<Map<String, String>> savePassword(@Valid @RequestBody PasswordDto passwordDto) {
        LOGGER.debug("POST /api/v1/usersAdminControl/savePassword for token: {}", passwordDto.getToken());

        String result = securityService.validatePasswordResetToken(passwordDto.getToken());
        if (result != null) {
            this.logClientError(HttpStatus.BAD_REQUEST, "Invalid token", new IllegalArgumentException("Invalid token"));
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid token");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Optional<ApplicationUser> user = userService.getUserByPasswordResetToken(passwordDto.getToken());
        if (user.isPresent()) {
            userService.changeUserPassword(user.get(), passwordDto.getNewPassword());
            passwordResetTokenService.deleteTokenByUser(user.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            this.logClientError(HttpStatus.NOT_FOUND, "User not found for token", new NotFoundException("User not found for token"));
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "User not found for token");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Create a new user", security = @SecurityRequirement(name = "apiKey"))
    public UserDetailDto createUser(@Valid @RequestBody UserCreateDto userCreateDto) throws ValidationException, ConflictException {
        LOGGER.info("POST /api/v1/usersAdminControl body: {}", userCreateDto);
        return userService.createUser(userCreateDto);
    }

    private void logClientError(HttpStatus status, String message, Exception e) {
        LOGGER.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
    }
}
