package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Transient;
import jakarta.persistence.FetchType;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String summary;

    @Column(nullable = false, length = 10000)
    private String text;

    @Column(nullable = false)
    private LocalDate durationFrom;

    @Column(nullable = false)
    private LocalDate durationTo;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = true)
    private String imageUrl;

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    private List<Show> shows = new ArrayList<>();

    @Column(nullable = false)
    private Integer soldSeats;

    @ManyToMany
    @JoinTable(
        name = "event_artist",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private Set<Artist> artists = new HashSet<>();

    @Transient
    private Long[] showIds;

    public Long[] getShowIds() {
        return showIds;
    }

    public void setShowIds(Long[] showIds) {
        this.showIds = showIds;
    }

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

    public List<Show> getShows() {
        return shows;
    }

    public void setShows(List<Show> shows) {
        this.shows = shows;
    }

    public void addShow(Show show) {
        shows.add(show);
        show.setEvent(this);
    }

    public void removeShow(Show show) {
        shows.remove(show);
        show.setEvent(null);
    }

    public Set<Artist> getArtists() {
        return artists;
    }

    public void setArtists(Set<Artist> artists) {
        this.artists = artists;
    }

    public Integer getSoldSeats() {
        return soldSeats;
    }

    public void setSoldSeats(Integer soldSeats) {
        this.soldSeats = soldSeats;
    }

    // Overrides for equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event event)) {
            return false;
        }
        return Objects.equals(durationFrom, event.durationFrom)
            && Objects.equals(durationTo, event.durationTo)
            && Objects.equals(id, event.id)
            && Objects.equals(name, event.name)
            && Objects.equals(summary, event.summary)
            && Objects.equals(text, event.text)
            && Objects.equals(type, event.type)
            && Objects.equals(imageUrl, event.imageUrl)
            && Objects.equals(shows, event.shows)
            && Objects.equals(artists, event.artists)
            && Objects.equals(soldSeats, event.soldSeats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, summary, text, durationFrom, durationTo, type, soldSeats);
    }

    @Override
    public String toString() {
        return "Event{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", summary='" + summary + '\''
            + ", text='" + text + '\''
            + ", durationFrom=" + durationFrom + '\''
            + ", durationTo=" + durationTo + '\''
            + ", type='" + type + '\''
            + ", imageUrl='" + imageUrl + '\''
            + ", shows=" + shows + '\''
            + ", artists=" + artists + '\''
            + ", soldSeats=" + soldSeats + '\''
            + '}';
    }

    // Builder class
    public static final class EventBuilder {
        private Long id;
        private String name;
        private String summary;
        private String text;
        private LocalDate durationFrom;
        private LocalDate durationTo;
        private String type;
        private String imageUrl;
        private List<Show> shows = new ArrayList<>();
        private Set<Artist> artists = new HashSet<>();
        private Integer soldSeats;

        private EventBuilder() {
        }

        public static EventBuilder anEvent() {
            return new EventBuilder();
        }

        public EventBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public EventBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public EventBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public EventBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public EventBuilder withDurationFrom(LocalDate durationFrom) {
            this.durationFrom = durationFrom;
            return this;
        }

        public EventBuilder withDurationTo(LocalDate durationTo) {
            this.durationTo = durationTo;
            return this;
        }

        public EventBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public EventBuilder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public EventBuilder withShows(List<Show> shows) {
            this.shows = shows;
            return this;
        }

        public EventBuilder withArtists(Set<Artist> artists) {
            this.artists = artists;
            return this;
        }

        public EventBuilder withSoldSeats(Integer soldSeats) {
            this.soldSeats = soldSeats;
            return this;
        }

        public Event build() {
            Event event = new Event();
            event.setId(id);
            event.setName(name);
            event.setSummary(summary);
            event.setText(text);
            event.setDurationFrom(durationFrom);
            event.setDurationTo(durationTo);
            event.setType(type);
            event.setImageUrl(imageUrl);
            event.setShows(shows);
            event.setArtists(artists);
            event.setSoldSeats(soldSeats);
            return event;
        }
    }
}
