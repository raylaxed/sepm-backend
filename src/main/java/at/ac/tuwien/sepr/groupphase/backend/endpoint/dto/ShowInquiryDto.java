package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ShowInquiryDto {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Date is required")
    @FutureOrPresent(message = "Date cannot be in the past")
    private LocalDate date;

    @NotNull(message = "Time is required")
    private LocalTime time;

    @NotNull(message = "Capacity must not be null")
    @Min(value = 0, message = "Capacity must not be negative")
    private int capacity;

    @NotBlank(message = "Summary is required")
    @Size(max = 500, message = "Summary cannot exceed 500 characters")
    private String summary;

    @NotBlank(message = "Description is required")
    @Size(max = 10000,  message = "Description cannot exceed 10 000 characters")
    private String text;

    @NotNull(message = "Sold seats must not be null")
    @Min(value = 0, message = "Sold seats must not be negative")
    private int soldSeats;

    @NotBlank(message = "Event type is required")
    @Size(max = 100)
    private String eventType;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be positive")
    private int duration;

    private String imageUrl;

    private Long[] artistIds;

    @NotNull(message = "Venue is required")
    private Long venueId;

    @NotNull(message = "Hall is required")
    private Long hallId;

    private double minPrice;

    private double maxPrice;

    @NotEmpty(message = "At least one sector must have a price set")
    @Valid
    private List<ShowSectorDto> showSectors;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getSoldSeats() {
        return soldSeats;
    }

    public void setSoldSeats(int soldSeats) {
        this.soldSeats = soldSeats;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Long[] getArtistIds() {
        return artistIds;
    }

    public void setArtistIds(Long[] artistIds) {
        this.artistIds = artistIds;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public Long getHallId() {
        return hallId;
    }

    public void setHallId(Long hallId) {
        this.hallId = hallId;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public List<ShowSectorDto> getShowSectors() {
        return showSectors;
    }

    public void setShowSectors(List<ShowSectorDto> showSectors) {
        this.showSectors = showSectors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShowInquiryDto that)) {
            return false;
        }
        return capacity == that.capacity
            && soldSeats == that.soldSeats
            && duration == that.duration
            && Objects.equals(name, that.name)
            && Objects.equals(date, that.date)
            && Objects.equals(time, that.time)
            && Objects.equals(eventType, that.eventType)
            && Objects.equals(summary, that.summary)
            && Objects.equals(text, that.text)
            && Objects.equals(imageUrl, that.imageUrl)
            && Objects.equals(venueId, that.venueId)
            && Objects.equals(hallId, that.hallId)
            && Arrays.equals(artistIds, that.artistIds)
            && Objects.equals(minPrice, that.minPrice)
            && Objects.equals(maxPrice, that.maxPrice)
            && Objects.equals(showSectors, that.showSectors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date, time, capacity, soldSeats,
                           eventType, duration, summary, text, imageUrl, venueId, hallId, artistIds,
                           minPrice, maxPrice, showSectors);
    }

    @Override
    public String toString() {
        return "ShowInquiryDto{"
            + "name='" + name + '\''
            + ", date=" + date + '\''
            + ", time=" + time + '\''
            + ", capacity=" + capacity + '\''
            + ", soldSeats=" + soldSeats + '\''
            + ", eventType='" + eventType + '\''
            + ", duration=" + duration + '\''
            + ", summary='" + summary + '\''
            + ", text='" + text + '\''
            + ", imageUrl='" + imageUrl + '\''
            + ", venueId=" + venueId + '\''
            + ", hallId=" + hallId + '\''
            + ", artistIds='" + Arrays.toString(artistIds) + '\''
            + ", minPrice=" + minPrice + '\''
            + ", maxPrice=" + maxPrice + '\''
            + ", showSectors=" + showSectors.toString() + '\''
            + '}';
    }

    public static final class ShowInquiryDtoBuilder {
        private String name;
        private LocalDate date;
        private LocalTime time;
        private int capacity;
        private String summary;
        private String text;
        private String imageUrl;
        private int soldSeats;
        private String eventType;
        private int duration;
        private Long venueId;
        private Long hallId;
        private Long[] artistIds;
        private double minPrice;
        private double maxPrice;
        private List<ShowSectorDto> showSectors;

        private ShowInquiryDtoBuilder() {
        }

        public static ShowInquiryDtoBuilder aShowInquiryDto() {
            return new ShowInquiryDtoBuilder();
        }

        public ShowInquiryDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ShowInquiryDtoBuilder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public ShowInquiryDtoBuilder withTime(LocalTime time) {
            this.time = time;
            return this;
        }

        public ShowInquiryDtoBuilder withCapacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public ShowInquiryDtoBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public ShowInquiryDtoBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public ShowInquiryDtoBuilder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public ShowInquiryDtoBuilder withSoldSeats(int soldSeats) {
            this.soldSeats = soldSeats;
            return this;
        }

        public ShowInquiryDtoBuilder withEventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public ShowInquiryDtoBuilder withDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public ShowInquiryDtoBuilder withVenueId(Long venueId) {
            this.venueId = venueId;
            return this;
        }

        public ShowInquiryDtoBuilder withHallId(Long hallId) {
            this.hallId = hallId;
            return this;
        }

        public ShowInquiryDtoBuilder withArtistIds(Long[] artistIds) {
            this.artistIds = artistIds;
            return this;
        }

        public ShowInquiryDtoBuilder withMinPrice(double minPrice) {
            this.minPrice = minPrice;
            return this;
        }

        public ShowInquiryDtoBuilder withMaxPrice(double maxPrice) {
            this.maxPrice = maxPrice;
            return this;
        }

        public ShowInquiryDtoBuilder withShowSectors(List<ShowSectorDto> showSectors) {
            this.showSectors = showSectors;
            return this;
        }

        public ShowInquiryDto build() {
            ShowInquiryDto showInquiryDto = new ShowInquiryDto();
            showInquiryDto.setName(name);
            showInquiryDto.setDate(date);
            showInquiryDto.setTime(time);
            showInquiryDto.setCapacity(capacity);
            showInquiryDto.setSoldSeats(soldSeats);
            showInquiryDto.setEventType(eventType);
            showInquiryDto.setDuration(duration);
            showInquiryDto.setSummary(summary);
            showInquiryDto.setText(text);
            showInquiryDto.setImageUrl(imageUrl);
            showInquiryDto.setVenueId(venueId);
            showInquiryDto.setHallId(hallId);
            showInquiryDto.setArtistIds(artistIds);
            showInquiryDto.setMinPrice(minPrice);
            showInquiryDto.setMaxPrice(maxPrice);
            showInquiryDto.setShowSectors(showSectors);
            return showInquiryDto;
        }
    }
}