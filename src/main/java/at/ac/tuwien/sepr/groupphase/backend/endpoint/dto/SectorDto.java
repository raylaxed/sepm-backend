package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;

public record SectorDto(
    Long id,
    Long sectorName,
    Integer rows,
    Integer columns,
    Long price,
    List<SeatDto> seats
) { } 