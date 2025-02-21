package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record StandingSectorDto(
    Long id,
    String sectorName,
    Integer capacity,
    Integer takenCapacity,
    Integer positionX1,
    Integer positionY1,
    Integer positionX2,
    Integer positionY2,
    Long price
) { }