package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper interface for converting between Order entities and DTOs.
 * This mapper provides methods to convert Order entities to DTOs and vice versa,
 * handling the relationship between orders and users.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    /**
     * Converts an Order entity to an OrderDto.
     * Maps the user's ID from the associated ApplicationUser entity.
     *
     * @param order the Order entity to convert
     * @return the corresponding OrderDto
     */
    @Named("orderToDto")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "cancellationInvoiceId", source = "cancellationInvoiceId")
    OrderDto orderToOrderDto(Order order);

    /**
     * Converts an OrderDto to an Order entity.
     * Creates an ApplicationUser entity from the userId.
     *
     * @param orderDto the OrderDto to convert
     * @return the corresponding Order entity
     */
    @Mapping(target = "user", source = "userId", qualifiedByName = "userIdToApplicationUser")
    @Mapping(target = "cancellationInvoiceId", source = "cancellationInvoiceId")
    Order orderDtoToOrder(OrderDto orderDto);

    /**
     * Helper method to create an ApplicationUser entity from a user ID.
     *
     * @param userId the ID of the user
     * @return a new ApplicationUser entity with the given ID, or null if userId is null
     */
    @Named("userIdToApplicationUser")
    default ApplicationUser userIdToApplicationUser(Long userId) {
        if (userId == null) {
            return null;
        }
        ApplicationUser user = new ApplicationUser();
        user.setId(userId);
        return user;
    }
}