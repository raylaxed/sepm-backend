package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record VenueDto(
    Long id,
    @NotBlank(message = "Name must not be empty")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    String name,
    @NotBlank(message = "Street must not be empty")
    @Size(max = 100, message = "Street must not exceed 100 characters")
    String street,
    @NotBlank(message = "City must not be empty")
    @Size(max = 100, message = "City must not exceed 100 characters")
    String city,
    @NotBlank(message = "County must not be empty")
    @Size(max = 100, message = "County must not exceed 100 characters")
    String county,
    @NotBlank(message = "Postal code must not be empty")
    @Size(max = 100, message = "Postal code must not exceed 100 characters")
    @Pattern(regexp = "^\\d{4,5}$", message = "Postal code must be 4-5 digits")

    String postalCode,
    
    List<Long> hallIds
) {} 