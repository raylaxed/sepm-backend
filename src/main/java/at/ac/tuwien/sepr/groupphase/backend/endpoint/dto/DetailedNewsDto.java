package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

public class DetailedNewsDto extends SimpleNewsDto {

    private String text;
    private String[] imagePaths;
    private SimpleEventDto event;
    //private Long showId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String[] getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(String[] imagePaths) {
        this.imagePaths = imagePaths;
    }

    public SimpleEventDto getEvent() {
        return event;
    }

    public void setEvent(SimpleEventDto event) {
        this.event = event;
    }

    //public Long getShowId() { return showId; }
    //public void setShowId(Long showId) { this.showId = showId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DetailedNewsDto that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(text, that.text)
            && Arrays.equals(imagePaths, that.imagePaths)
            && Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text);
    }

    @Override
    public String toString() {
        return "DetailedNewsDto{"
            + "text='" + text + '\''
            + "images='" + Arrays.toString(imagePaths) + '\''
            + "event=" + event + '\''
            + '}';
    }


    public static final class DetailedNewsDtoBuilder {
        private Long id;
        private LocalDateTime publishedAt;
        private String text;
        private String title;
        private String summary;
        private String[] imagePaths;
        private SimpleEventDto event;
        //private Long showId;

        private DetailedNewsDtoBuilder() {
        }

        public static DetailedNewsDtoBuilder aDetailedNewsDto() {
            return new DetailedNewsDtoBuilder();
        }

        public DetailedNewsDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public DetailedNewsDtoBuilder withPublishedAt(LocalDateTime publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public DetailedNewsDtoBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public DetailedNewsDtoBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public DetailedNewsDtoBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public DetailedNewsDtoBuilder withImagePaths(String[] imagePaths) {
            this.imagePaths = imagePaths;
            return this;
        }

        public DetailedNewsDtoBuilder withEvent(SimpleEventDto event) {
            this.event = event;
            return this;
        }

        public DetailedNewsDto build() {
            DetailedNewsDto detailedNewsDto = new DetailedNewsDto();
            detailedNewsDto.setId(id);
            detailedNewsDto.setPublishedAt(publishedAt);
            detailedNewsDto.setText(text);
            detailedNewsDto.setTitle(title);
            detailedNewsDto.setSummary(summary);
            detailedNewsDto.setImagePaths(imagePaths);
            detailedNewsDto.setEvent(event);
            return detailedNewsDto;
        }
    }
}