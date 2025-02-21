package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;
import java.util.Objects;

public class SimpleEventDto {

    private Long id;
    private String name;
    private String summary;
    private String type;
    private LocalDate durationFrom;
    private LocalDate durationTo;
    private Integer soldSeats;
    private String imageUrl;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Integer getSoldSeats() {
        return soldSeats;
    }

    public void setSoldSeats(Integer soldSeats) {
        this.soldSeats = soldSeats;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleEventDto that)) {
            return false;
        }
        return Objects.equals(id, that.id)
            && Objects.equals(name, that.name)
            && Objects.equals(summary, that.summary)
            && Objects.equals(type, that.type)
            && Objects.equals(durationFrom, that.durationFrom)
            && Objects.equals(durationTo, that.durationTo)
            && Objects.equals(soldSeats, that.soldSeats)
            && Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, summary, type, durationFrom, durationTo, soldSeats, imageUrl);
    }

    @Override
    public String toString() {
        return "SimpleEventDto{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", summary='" + summary + '\''
            + ", type='" + type + '\''
            + ", durationFrom=" + durationFrom + '\''
            + ", durationTo=" + durationTo + '\''
            + ", soldSeats=" + soldSeats + '\''
            + ", imageUrl='" + imageUrl + '\''
            + '}';
    }

    // Builder class
    public static final class SimpleEventDtoBuilder {
        private Long id;
        private String name;
        private String summary;
        private String type;
        private LocalDate durationFrom;
        private LocalDate durationTo;
        private Integer soldSeats;
        private String imageUrl;

        private SimpleEventDtoBuilder() {
        }

        public static SimpleEventDtoBuilder aSimpleEventDto() {
            return new SimpleEventDtoBuilder();
        }

        public SimpleEventDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public SimpleEventDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public SimpleEventDtoBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public SimpleEventDtoBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public SimpleEventDtoBuilder withDurationFrom(LocalDate durationFrom) {
            this.durationFrom = durationFrom;
            return this;
        }

        public SimpleEventDtoBuilder withDurationTo(LocalDate durationTo) {
            this.durationTo = durationTo;
            return this;
        }

        public SimpleEventDtoBuilder withSoldSeats(Integer soldSeats) {
            this.soldSeats = soldSeats;
            return this;
        }

        public SimpleEventDtoBuilder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public SimpleEventDto build() {
            SimpleEventDto simpleEventDto = new SimpleEventDto();
            simpleEventDto.setId(id);
            simpleEventDto.setName(name);
            simpleEventDto.setSummary(summary);
            simpleEventDto.setType(type);
            simpleEventDto.setDurationFrom(durationFrom);
            simpleEventDto.setDurationTo(durationTo);
            simpleEventDto.setSoldSeats(soldSeats);
            simpleEventDto.setImageUrl(imageUrl);
            return simpleEventDto;
        }
    }
}
