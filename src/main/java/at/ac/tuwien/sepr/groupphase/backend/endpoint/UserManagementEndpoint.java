package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserManagementEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final UserMapper userMapper;

    public UserManagementEndpoint(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @PutMapping("/me")
    @Operation(summary = "Update current user details", security = @SecurityRequirement(name = "apiKey"))
    public String updateUser(Authentication authentication, @Valid @RequestBody UserUpdateDto userUpdateDto)
        throws ValidationException, ConflictException, NotFoundException {
        LOGGER.info("PUT /api/v1/users/me body: {}", userUpdateDto);
        String email = authentication.getName();
        return userService.update(email, userUpdateDto);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current user details", security = @SecurityRequirement(name = "apiKey"))
    public UserDetailDto getCurrentUser(Authentication authentication) throws NotFoundException {
        LOGGER.info("GET /api/v1/users/me");
        String email = authentication.getName();
        return userMapper.applicationUserToUserDetailDto(userService.findApplicationUserByEmail(email));
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete current user", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<Void> deleteCurrentUser(Authentication authentication) throws NotFoundException {
        LOGGER.info("DELETE /api/v1/users/me");
        String email = authentication.getName();
        userService.deleteUser(email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all users", security = @SecurityRequirement(name = "apiKey"))
    public List<UserDetailDto> getAllUsers() {
        LOGGER.info("GET /api/v1/users/all");
        return userService.getAllUsers();
    }

}