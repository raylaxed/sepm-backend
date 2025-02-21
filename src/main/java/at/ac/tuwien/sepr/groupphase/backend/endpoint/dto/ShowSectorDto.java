package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.AssertTrue;

public class ShowSectorDto {

    private Long id;

    private Long showId;

    @NotNull(message = "Either sector ID or standing sector ID must be set, but not both")
    @AssertTrue(message = "Either sector ID or standing sector ID must be set, but not both")
    private boolean isValidSectorConfiguration() {
        return (sectorId != null && standingSectorId == null)
            || (sectorId == null && standingSectorId != null);
    }

    private Long sectorId;

    private Long standingSectorId;

    @NotNull(message = "Price must not be null")
    @Positive(message = "Price must be positive")
    private Double price;



    // Default constructor
    public ShowSectorDto() {
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getShowId() {
        return showId;
    }

    public Long getSectorId() {
        return sectorId;
    }

    public Long getStandingSectorId() {
        return standingSectorId;
    }

    public Double getPrice() {
        return price;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public void setSectorId(Long sectorId) {
        this.sectorId = sectorId;
    }

    public void setStandingSectorId(Long standingSectorId) {
        this.standingSectorId = standingSectorId;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    // Builder pattern
    public static class ShowSectorDtoBuilder {
        private final ShowSectorDto showSectorDto;

        public ShowSectorDtoBuilder() {
            showSectorDto = new ShowSectorDto();
        }

        public ShowSectorDtoBuilder withId(Long id) {
            showSectorDto.setId(id);
            return this;
        }

        public ShowSectorDtoBuilder withShowId(Long showId) {
            showSectorDto.setShowId(showId);
            return this;
        }

        public ShowSectorDtoBuilder withSectorId(Long sectorId) {
            showSectorDto.setSectorId(sectorId);
            return this;
        }

        public ShowSectorDtoBuilder withStandingSectorId(Long standingSectorId) {
            showSectorDto.setStandingSectorId(standingSectorId);
            return this;
        }

        public ShowSectorDtoBuilder withPrice(Double price) {
            showSectorDto.setPrice(price);
            return this;
        }

        public ShowSectorDto build() {
            return showSectorDto;
        }
    }

    @Override
    public String toString() {
        return "ShowSectorDto{"
            + "id=" + id
            + ", showId=" + showId
            + ", sectorId=" + sectorId
            + ", standingSectorId=" + standingSectorId
            + ", price=" + price
            + '}';
    }
}