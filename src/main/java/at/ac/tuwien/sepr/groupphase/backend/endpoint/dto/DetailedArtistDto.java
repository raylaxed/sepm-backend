package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.List;
import java.util.Objects;

public class DetailedArtistDto extends SimpleArtistDto {

    private String text;
    private List<SimpleShowDto> shows;

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
        if (!(o instanceof DetailedArtistDto that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(text, that.text)
            && Objects.equals(shows, that.shows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text, shows);
    }

    @Override
    public String toString() {
        return "DetailedArtistDto{"
            + "text='" + text + '\''
            + ", shows=" + shows
            + "} " + super.toString();
    }

    public static final class DetailedArtistDtoBuilder {
        private Long id;
        private String name;
        private String summary;
        private String imageUrl;
        private String text;
        private List<SimpleShowDto> shows;

        private DetailedArtistDtoBuilder() {
        }

        public static DetailedArtistDtoBuilder aDetailedArtistDto() {
            return new DetailedArtistDtoBuilder();
        }

        public DetailedArtistDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public DetailedArtistDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public DetailedArtistDtoBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public DetailedArtistDtoBuilder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public DetailedArtistDtoBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public DetailedArtistDtoBuilder withShows(List<SimpleShowDto> shows) {
            this.shows = shows;
            return this;
        }

        public DetailedArtistDto build() {
            DetailedArtistDto detailedArtistDto = new DetailedArtistDto();
            detailedArtistDto.setId(id);
            detailedArtistDto.setName(name);
            detailedArtistDto.setSummary(summary);
            detailedArtistDto.setImageUrl(imageUrl);
            detailedArtistDto.setText(text);
            detailedArtistDto.setShows(shows);
            return detailedArtistDto;
        }
    }
}