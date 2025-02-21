package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public record HallDto(
    Long id,
    String name,
    Integer capacity,
    Integer canvasWidth,
    Integer canvasHeight,
    StageDto stage,
    List<SectorDto> sectors,
    List<StandingSectorDto> standingSectors
) { } 
