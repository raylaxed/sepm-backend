package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

public class EventInquiryDto {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Summary is required")
    @Size(max = 500, message = "Summary must not exceed 500 characters")
    private String summary;

    @NotBlank(message = "Text is required")
    @Size(max = 10000, message = "Text must not exceed 10,000 characters")
    private String text;

    @NotNull(message = "Start date is required")
    private LocalDate durationFrom;

    @NotNull(message = "End date is required")
    private LocalDate durationTo;

    @NotBlank(message = "Type is required")
    @Size(max = 50, message = "Type must not exceed 50 characters")
    private String type;

    private String imageUrl;

    @NotNull(message = "Sold seats must not be null")
    @Min(value = 0, message = "Sold seats must be at least 0")
    private Integer soldSeats;

    @NotEmpty(message = "At least one show has be selected")
    private Long[] showIds;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public LocalDate getDurationFrom() {
        return durationFrom;
    }

    public void setDurationFrom(LocalDate durationFrom) {
        this.durationFrom = durationFrom;
    }

    public LocalDate getDurationTo() {
        return durationTo;
    }

    public void setDurationTo(LocalDate durationTo) {
        this.durationTo = durationTo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getSoldSeats() {
        return soldSeats;
    }

    public void setSoldSeats(Integer soldSeats) {
        this.soldSeats = soldSeats;
    }

    public Long[] getShowIds() {
        return showIds;
    }

    public void setShowIds(Long[] showIds) {
        this.showIds = showIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventInquiryDto that)) {
            return false;
        }
        return Objects.equals(name, that.name)
            && Objects.equals(summary, that.summary)
            && Objects.equals(text, that.text)
            && Objects.equals(durationFrom, that.durationFrom)
            && Objects.equals(durationTo, that.durationTo)
            && Objects.equals(type, that.type)
            && Objects.equals(imageUrl, that.imageUrl)
            && Objects.equals(soldSeats, that.soldSeats)
            && Objects.equals(showIds, that.showIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, summary, text, durationFrom, durationTo, type, imageUrl, soldSeats, showIds);
    }

    @Override
    public String toString() {
        return "EventInquiryDto{"
            + "name='" + name + '\''
            + ", summary='" + summary + '\''
            + ", text='" + text + '\''
            + ", durationFrom=" + durationFrom + '\''
            + ", durationTo=" + durationTo + '\''
            + ", type='" + type + '\''
            + ", imageUrl='" + imageUrl + '\''
            + ", soldSeats=" + soldSeats + '\''
            + ", showIds=" + Arrays.toString(showIds)
            + '}';
    }

    // Builder class for EventInquiryDto
    public static final class EventInquiryDtoBuilder {
        private String name;
        private String summary;
        private String text;
        private LocalDate durationFrom;
        private LocalDate durationTo;
        private String type;
        private String imageUrl;
        private Integer soldSeats;
        private Long[] showIds;

        private EventInquiryDtoBuilder() {
        }

        public static EventInquiryDtoBuilder anEventInquiryDto() {
            return new EventInquiryDtoBuilder();
        }

        public EventInquiryDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public EventInquiryDtoBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public EventInquiryDtoBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public EventInquiryDtoBuilder withDurationFrom(LocalDate durationFrom) {
            this.durationFrom = durationFrom;
            return this;
        }

        public EventInquiryDtoBuilder withDurationTo(LocalDate durationTo) {
            this.durationTo = durationTo;
            return this;
        }

        public EventInquiryDtoBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public EventInquiryDtoBuilder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public EventInquiryDtoBuilder withSoldSeats(Integer soldSeats) {
            this.soldSeats = soldSeats;
            return this;
        }

        public EventInquiryDtoBuilder withShowIds(Long[] showIds) {
            this.showIds = showIds;
            return this;
        }

        public EventInquiryDto build() {
            EventInquiryDto eventInquiryDto = new EventInquiryDto();
            eventInquiryDto.setName(name);
            eventInquiryDto.setSummary(summary);
            eventInquiryDto.setText(text);
            eventInquiryDto.setDurationFrom(durationFrom);
            eventInquiryDto.setDurationTo(durationTo);
            eventInquiryDto.setType(type);
            eventInquiryDto.setImageUrl(imageUrl);
            eventInquiryDto.setSoldSeats(soldSeats);
            eventInquiryDto.setShowIds(showIds);
            return eventInquiryDto;
        }
    }
}
