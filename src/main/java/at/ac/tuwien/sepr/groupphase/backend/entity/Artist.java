package at.ac.tuwien.sepr.groupphase.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.FetchType;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@JsonIgnoreProperties({"shows", "events"})
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String summary;

    @Column(nullable = false, length = 10000)
    private String text;

    @Column(nullable = true)
    private String imageUrl;

    @ManyToMany(mappedBy = "artists", fetch = FetchType.EAGER)
    private Set<Event> events = new HashSet<>();

    @ManyToMany(mappedBy = "artists", fetch = FetchType.EAGER)
    private Set<Show> shows = new HashSet<>();

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

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public Set<Show> getShows() {
        return shows;
    }

    public void setShows(Set<Show> shows) {
        this.shows = shows;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Artist artist)) {
            return false;
        }
        return Objects.equals(id, artist.id)
            && Objects.equals(name, artist.name)
            && Objects.equals(summary, artist.summary)
            && Objects.equals(text, artist.text)
            && Objects.equals(imageUrl, artist.imageUrl)
            && Objects.equals(events, artist.events)
            && Objects.equals(shows, artist.shows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, summary, text, imageUrl, events, shows);
    }

    @Override
    public String toString() {
        return "Artist{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", summary='" + summary + '\''
            + ", text='" + text + '\''
            + ", imageUrl='" + imageUrl + '\''
            + '}';
    }


    public static final class ArtistBuilder {
        private Long id;
        private String name;
        private String summary;
        private String text;
        private String imageUrl;
        private Set<Event> events = new HashSet<>();
        private Set<Show> shows = new HashSet<>();

        private ArtistBuilder() {
        }

        public static Artist.ArtistBuilder anArtist() {
            return new Artist.ArtistBuilder();
        }

        public Artist.ArtistBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public Artist.ArtistBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public Artist.ArtistBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public Artist.ArtistBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public Artist.ArtistBuilder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Artist.ArtistBuilder withEvents(Set<Event> events) {
            this.events = events;
            return this;
        }

        public Artist.ArtistBuilder withShows(Set<Show> shows) {
            this.shows = shows;
            return this;
        }


        public Artist build() {
            Artist artist = new Artist();
            artist.setId(id);
            artist.setName(name);
            artist.setSummary(summary);
            artist.setText(text);
            artist.setImageUrl(imageUrl);
            artist.setEvents(events);
            artist.setShows(shows);
            return artist;
        }
    }

}