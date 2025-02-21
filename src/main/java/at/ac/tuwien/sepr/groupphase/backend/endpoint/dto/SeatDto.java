package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record SeatDto(
    Long seatId,
    Integer rowSeat,
    Integer sector,
    Integer columnSeat,
    Integer positionX,
    Integer positionY
) { }