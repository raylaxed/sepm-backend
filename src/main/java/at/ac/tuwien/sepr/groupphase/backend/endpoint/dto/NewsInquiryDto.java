package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;

public class NewsInquiryDto {

    @NotNull(message = "Title must not be null")
    @Size(max = 100, message = "Title is too long")
    private String title;

    @NotNull(message = "Summary must not be null")
    @Size(max = 500, message = "Title is too long")
    private String summary;

    @NotNull(message = "Text must not be null")
    @Size(max = 10000, message = "Text is too long")
    private String text;

    private SimpleEventDto event;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public SimpleEventDto getEvent() {
        return event;
    }

    public void setEvent(SimpleEventDto event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NewsInquiryDto that)) {
            return false;
        }
        return Objects.equals(title, that.title)
            && Objects.equals(summary, that.summary)
            && Objects.equals(text, that.text)
            && Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, summary, text, event);
    }

    @Override
    public String toString() {
        return "NewsInquiryDto{"
            + "title='" + title + '\''
            + ", summary='" + summary + '\''
            + ", text='" + text + '\''
            + '}';
    }


    public static final class NewsInquiryDtoBuilder {
        private String title;
        private String summary;
        private String text;
        private SimpleEventDto event;

        private NewsInquiryDtoBuilder() {
        }

        public static NewsInquiryDtoBuilder aNewsInquiryDto() {
            return new NewsInquiryDtoBuilder();
        }

        public NewsInquiryDtoBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public NewsInquiryDtoBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public NewsInquiryDtoBuilder withText(String text) {
            this.text = text;
            return this;
        }

        private NewsInquiryDtoBuilder withEvent(SimpleEventDto event) {
            this.event = event;
            return this;
        }

        public NewsInquiryDto build() {
            NewsInquiryDto newsInquiryDto = new NewsInquiryDto();
            newsInquiryDto.setTitle(title);
            newsInquiryDto.setSummary(summary);
            newsInquiryDto.setText(text);
            newsInquiryDto.setEvent(event);
            return newsInquiryDto;
        }
    }
}