package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record StageDto(
    Long id,
    Integer positionX,
    Integer positionY,
    Integer width,
    Integer height
) {} 