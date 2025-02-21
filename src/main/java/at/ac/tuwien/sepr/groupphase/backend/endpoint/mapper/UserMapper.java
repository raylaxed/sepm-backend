package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.CreatedUserDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between ApplicationUser entities and DTOs.
 * This mapper provides methods to convert ApplicationUser entities to different DTO representations
 * and vice versa, facilitating the data transfer between layers of the application.
 */
@Mapper
public interface UserMapper {

    /**
     * Converts a UserCreateDto to an ApplicationUser entity.
     *
     * @param userCreateDto the UserCreateDto to convert
     * @return the corresponding ApplicationUser entity
     */
    ApplicationUser userCreateDtoToApplicationUser(UserCreateDto userCreateDto);

    /**
     * Converts a UserRegistrationDto to an ApplicationUser entity.
     * Sets the admin flag to false and ignores the id field during mapping.
     *
     * @param userRegistrationDto the UserRegistrationDto to convert
     * @return the corresponding ApplicationUser entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "admin", constant = "false")
    ApplicationUser userRegistrationDtoToApplicationUser(UserRegistrationDto userRegistrationDto);

    /**
     * Converts an ApplicationUser entity to a UserDetailDto.
     *
     * @param applicationUser the ApplicationUser entity to convert
     * @return the corresponding UserDetailDto
     */
    UserDetailDto applicationUserToUserDetailDto(ApplicationUser applicationUser);

    /**
     * Converts an ApplicationUser entity to a CreatedUserDto.
     *
     * @param applicationUser the ApplicationUser entity to convert
     * @return the corresponding CreatedUserDto
     */
    CreatedUserDto applicationUserToCreatedUserDto(ApplicationUser applicationUser);
}