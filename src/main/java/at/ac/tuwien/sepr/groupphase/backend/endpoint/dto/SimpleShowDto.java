package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import at.ac.tuwien.sepr.groupphase.backend.entity.Artist;

public class SimpleShowDto {

    private Long id;
    private String name;
    private LocalDate date;
    private LocalTime time;
    private String summary;
    private String eventType;
    private String imageUrl;
    private Set<Artist> artists = new LinkedHashSet<>();

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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Set<Artist> getArtists() {
        return artists;
    }

    public void setArtists(Set<Artist> artists) {
        this.artists = artists;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleShowDto that)) {
            return false;
        }
        return Objects.equals(id, that.id)
            && Objects.equals(name, that.name)
            && Objects.equals(date, that.date)
            && Objects.equals(time, that.time)
            && Objects.equals(summary, that.summary)
            && Objects.equals(eventType, that.eventType)
            && Objects.equals(imageUrl, that.imageUrl)
            && Objects.equals(artists, that.artists);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, date, time, summary, eventType, imageUrl, artists);
    }

    @Override
    public String toString() {
        return "SimpleShowDto{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", date=" + date
            + ", time=" + time
            + ", summary='" + summary + '\''
            + ", eventType='" + eventType + '\''
            + ", imageUrl='" + imageUrl + '\''
            + ", artists=" + artists
            + '}';
    }

    public static final class SimpleShowDtoBuilder {
        private Long id;
        private String name;
        private LocalDate date;
        private LocalTime time;
        private String summary;
        private String eventType;
        private String imageUrl;
        private Set<Artist> artists = new LinkedHashSet<>();

        private SimpleShowDtoBuilder() {
        }

        public static SimpleShowDtoBuilder aSimpleShowDto() {
            return new SimpleShowDtoBuilder();
        }

        public SimpleShowDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public SimpleShowDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public SimpleShowDtoBuilder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public SimpleShowDtoBuilder withTime(LocalTime time) {
            this.time = time;
            return this;
        }

        public SimpleShowDtoBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public SimpleShowDtoBuilder withEventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public SimpleShowDtoBuilder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public SimpleShowDtoBuilder withArtists(Set<Artist> artists) {
            this.artists = artists;
            return this;
        }

        public SimpleShowDto build() {
            SimpleShowDto simpleShowDto = new SimpleShowDto();
            simpleShowDto.setId(id);
            simpleShowDto.setName(name);
            simpleShowDto.setDate(date);
            simpleShowDto.setTime(time);
            simpleShowDto.setSummary(summary);
            simpleShowDto.setEventType(eventType);
            simpleShowDto.setImageUrl(imageUrl);
            simpleShowDto.setArtists(artists);
            return simpleShowDto;
        }
    }
} 