package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.util.Objects;

public class SimpleArtistDto {

    private Long id;
    private String name;
    private String summary;
    private String imageUrl;

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
        if (!(o instanceof SimpleArtistDto that)) {
            return false;
        }
        return Objects.equals(id, that.id)
            && Objects.equals(name, that.name)
            && Objects.equals(summary, that.summary)
            && Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, summary, imageUrl);
    }

    @Override
    public String toString() {
        return "SimpleArtistDto{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", summary='" + summary + '\''
            + ", imageUrl='" + imageUrl + '\''
            + '}';
    }

    public static final class SimpleArtistDtoBuilder {
        private Long id;
        private String name;
        private String summary;
        private String imageUrl;

        private SimpleArtistDtoBuilder() {
        }

        public static SimpleArtistDtoBuilder aSimpleArtistDto() {
            return new SimpleArtistDtoBuilder();
        }

        public SimpleArtistDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public SimpleArtistDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public SimpleArtistDtoBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public SimpleArtistDtoBuilder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public SimpleArtistDto build() {
            SimpleArtistDto simpleArtistDto = new SimpleArtistDto();
            simpleArtistDto.setId(id);
            simpleArtistDto.setName(name);
            simpleArtistDto.setSummary(summary);
            simpleArtistDto.setImageUrl(imageUrl);
            return simpleArtistDto;
        }
    }
}