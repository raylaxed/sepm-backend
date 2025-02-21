package at.ac.tuwien.sepr.groupphase.backend.unittests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.OrderMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Order;
import at.ac.tuwien.sepr.groupphase.backend.entity.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class OrderMappingTest {

    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        orderMapper = Mappers.getMapper(OrderMapper.class);
    }

    @Test
    void givenOrder_whenMapToOrderDto_thenDtoHasAllProperties() {
        // Arrange
        ApplicationUser user = new ApplicationUser();
        user.setId(1L);

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setPrice(50.0);

        Order order = Order.OrderBuilder.anOrder()
            .withId(1L)
            .withTotal(100.0)
            .withOrderDate(LocalDateTime.now())
            .withTickets(Collections.singletonList(ticket))
            .withUser(user)
            .withPaymentIntentId("pi_123456")
            .build();

        // Act
        OrderDto orderDto = orderMapper.orderToOrderDto(order);

        // Assert
        assertNotNull(orderDto);
        assertAll(
            () -> assertEquals(order.getId(), orderDto.getId()),
            () -> assertEquals(order.getTotal(), orderDto.getTotal()),
            () -> assertEquals(order.getOrderDate(), orderDto.getOrderDate()),
            () -> assertEquals(order.getPaymentIntentId(), orderDto.getPaymentIntentId()),
            () -> assertEquals(order.getUser().getId(), orderDto.getUserId()),
            () -> assertEquals(1, orderDto.getTickets().size()),
            () -> assertEquals(ticket.getId(), orderDto.getTickets().get(0).getId())
        );
    }

    @Test
    void givenOrderDto_whenMapToOrder_thenEntityHasAllProperties() {
        // Arrange
        OrderDto orderDto = OrderDto.OrderDtoBuilder.anOrderDto()
            .withId(1L)
            .withTotal(100.0)
            .withOrderDate(LocalDateTime.now())
            .withPaymentIntentId("pi_123456")
            .withUserId(1L)
            .build();

        // Act
        Order order = orderMapper.orderDtoToOrder(orderDto);

        // Assert
        assertNotNull(order);
        assertAll(
            () -> assertEquals(orderDto.getId(), order.getId()),
            () -> assertEquals(orderDto.getTotal(), order.getTotal()),
            () -> assertEquals(orderDto.getOrderDate(), order.getOrderDate()),
            () -> assertEquals(orderDto.getPaymentIntentId(), order.getPaymentIntentId()),
            () -> assertNotNull(order.getUser()),
            () -> assertEquals(orderDto.getUserId(), order.getUser().getId())
        );
    }
}