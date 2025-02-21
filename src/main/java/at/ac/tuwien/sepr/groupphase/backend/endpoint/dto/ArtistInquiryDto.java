package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;

public class ArtistInquiryDto {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Summary is required")
    @Size(max = 500, message = "Summary cannot exceed 500 characters")
    private String summary;

    @NotBlank(message = "Description is required")
    @Size(max = 10000,  message = "Description cannot exceed 10 000 characters")
    private String text;

    private String imageUrl;

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
        if (!(o instanceof ArtistInquiryDto that)) {
            return false;
        }
        return Objects.equals(name, that.name)
            && Objects.equals(summary, that.summary)
            && Objects.equals(text, that.text)
            && Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, summary, text, imageUrl);
    }

    @Override
    public String toString() {
        return "ArtistInquiryDto{"
            + "name='" + name + '\''
            + ", summary='" + summary + '\''
            + ", text='" + text + '\''
            + ", imageUrl='" + imageUrl + '\''
            + '}';
    }

    public static final class ArtistInquiryDtoBuilder {
        private String name;
        private String summary;
        private String text;
        private String imageUrl;

        private ArtistInquiryDtoBuilder() {
        }

        public static ArtistInquiryDtoBuilder anArtistInquiryDto() {
            return new ArtistInquiryDtoBuilder();
        }

        public ArtistInquiryDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ArtistInquiryDtoBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public ArtistInquiryDtoBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public ArtistInquiryDtoBuilder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public ArtistInquiryDto build() {
            ArtistInquiryDto artistInquiryDto = new ArtistInquiryDto();
            artistInquiryDto.setName(name);
            artistInquiryDto.setSummary(summary);
            artistInquiryDto.setText(text);
            artistInquiryDto.setImageUrl(imageUrl);
            return artistInquiryDto;
        }
    }
}