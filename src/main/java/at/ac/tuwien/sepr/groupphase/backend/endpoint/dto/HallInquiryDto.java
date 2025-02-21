package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Objects;

public class HallInquiryDto {
    @NotBlank(message = "Name must not be empty")
    private String name;

    @NotNull(message = "Capacity must not be null")
    @Min(value = 1, message = "Capacity must be positive")
    private Integer capacity;

    @NotNull(message = "Canvas width must not be null")
    @Min(value = 1, message = "Canvas width must be positive")
    private Integer canvasWidth;

    @NotNull(message = "Canvas height must not be null")
    @Min(value = 1, message = "Canvas height must be positive")
    private Integer canvasHeight;

    private StageDto stage;
    private List<SectorDto> sectors;
    private List<StandingSectorDto> standingSectors;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(Integer canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public Integer getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(Integer canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public StageDto getStage() {
        return stage;
    }

    public void setStage(StageDto stage) {
        this.stage = stage;
    }

    public List<SectorDto> getSectors() {
        return sectors;
    }

    public void setSectors(List<SectorDto> sectors) {
        this.sectors = sectors;
    }

    public List<StandingSectorDto> getStandingSectors() {
        return standingSectors;
    }

    public void setStandingSectors(List<StandingSectorDto> standingSectors) {
        this.standingSectors = standingSectors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HallInquiryDto that = (HallInquiryDto) o;
        return Objects.equals(name, that.name)
            && Objects.equals(capacity, that.capacity)
            && Objects.equals(canvasWidth, that.canvasWidth)
            && Objects.equals(canvasHeight, that.canvasHeight)
            && Objects.equals(stage, that.stage)
            && Objects.equals(sectors, that.sectors)
            && Objects.equals(standingSectors, that.standingSectors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, capacity, canvasWidth, canvasHeight, stage, sectors, standingSectors);
    }

    @Override
    public String toString() {
        return "HallInquiryDto{"
            + "name='" + name + '\''
            + ", capacity=" + capacity
            + ", canvasWidth=" + canvasWidth
            + ", canvasHeight=" + canvasHeight
            + ", stage=" + stage
            + ", sectors=" + sectors
            + ", standingSectors=" + standingSectors
            + '}';
    }

    public static final class HallInquiryDtoBuilder {
        private String name;
        private Integer capacity;
        private Integer canvasWidth;
        private Integer canvasHeight;
        private StageDto stage;
        private List<SectorDto> sectors;
        private List<StandingSectorDto> standingSectors;

        private HallInquiryDtoBuilder() {
        }

        public static HallInquiryDtoBuilder aHallInquiryDto() {
            return new HallInquiryDtoBuilder();
        }

        public HallInquiryDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public HallInquiryDtoBuilder withCapacity(Integer capacity) {
            this.capacity = capacity;
            return this;
        }

        public HallInquiryDtoBuilder withCanvasWidth(Integer canvasWidth) {
            this.canvasWidth = canvasWidth;
            return this;
        }

        public HallInquiryDtoBuilder withCanvasHeight(Integer canvasHeight) {
            this.canvasHeight = canvasHeight;
            return this;
        }

        public HallInquiryDtoBuilder withStage(StageDto stage) {
            this.stage = stage;
            return this;
        }

        public HallInquiryDtoBuilder withSectors(List<SectorDto> sectors) {
            this.sectors = sectors;
            return this;
        }

        public HallInquiryDtoBuilder withStandingSectors(List<StandingSectorDto> standingSectors) {
            this.standingSectors = standingSectors;
            return this;
        }

        public HallInquiryDto build() {
            HallInquiryDto hallInquiryDto = new HallInquiryDto();
            hallInquiryDto.setName(name);
            hallInquiryDto.setCapacity(capacity);
            hallInquiryDto.setCanvasWidth(canvasWidth);
            hallInquiryDto.setCanvasHeight(canvasHeight);
            hallInquiryDto.setStage(stage);
            hallInquiryDto.setSectors(sectors);
            hallInquiryDto.setStandingSectors(standingSectors);
            return hallInquiryDto;
        }
    }
}