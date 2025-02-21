package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class DetailedEventDto extends SimpleEventDto {

    private String text;
    private List<SimpleShowDto> shows;

    // Getters and Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<SimpleShowDto> getShows() {
        return shows;
    }

    public void setShows(List<SimpleShowDto> shows) {
        this.shows = shows;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DetailedEventDto that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(text, that.text) && Objects.equals(shows, that.shows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text, shows);
    }

    @Override
    public String toString() {
        return "DetailedEventDto{"
            + "text='" + text + '\''
            + ", id=" + getId()
            + ", name='" + getName() + '\''
            + ", summary='" + getSummary() + '\''
            + ", type='" + getType() + '\''
            + '}';
    }

    // Builder Class
    public static final class DetailedEventDtoBuilder {
        private Long id;
        private String name;
        private String summary;
        private String type;
        private String text;
        private LocalDate durationFrom;
        private LocalDate durationTo;
        private List<SimpleShowDto> shows;

        private DetailedEventDtoBuilder() {
        }

        public static DetailedEventDtoBuilder aDetailedEventDto() {
            return new DetailedEventDtoBuilder();
        }

        public DetailedEventDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public DetailedEventDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public DetailedEventDtoBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public DetailedEventDtoBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public DetailedEventDtoBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public DetailedEventDtoBuilder withShows(List<SimpleShowDto> shows) {
            this.shows = shows;
            return this;
        }

        public DetailedEventDto build() {
            DetailedEventDto detailedEventDto = new DetailedEventDto();
            detailedEventDto.setId(id);
            detailedEventDto.setName(name);
            detailedEventDto.setSummary(summary);
            detailedEventDto.setType(type);
            detailedEventDto.setText(text);
            detailedEventDto.setDurationFrom(durationFrom);
            detailedEventDto.setDurationTo(durationTo);
            detailedEventDto.setShows(shows);
            return detailedEventDto;
        }
    }
}
