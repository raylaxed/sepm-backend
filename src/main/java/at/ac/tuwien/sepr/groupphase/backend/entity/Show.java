package at.ac.tuwien.sepr.groupphase.backend.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;

@Entity
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int soldSeats;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private int duration;

    @Column(nullable = false, length = 500)
    private String summary;

    @Column(nullable = false, length = 10000)
    private String text;

    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = false)
    private double minPrice;

    @Column(nullable = false)
    private double maxPrice;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "show_artist",
        joinColumns = @JoinColumn(name = "show_id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private Set<Artist> artists = new LinkedHashSet<>();

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @ManyToOne
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @OneToMany(mappedBy = "show")
    private List<Ticket> tickets = new ArrayList<>();

    @Transient
    private Long hallId;

    @Transient
    private Long[] artistIds;

    @Transient
    private Long venueId;

    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShowSector> showSectors = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getSoldSeats() {
        return soldSeats;
    }

    public void setSoldSeats(int soldSeats) {
        this.soldSeats = soldSeats;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Set<Artist> getArtists() {
        return artists;
    }

    public void setArtists(Set<Artist> artists) {
        this.artists = artists;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Long getHallId() {
        return hallId;
    }

    public void setHallId(Long hallId) {
        this.hallId = hallId;
    }

    public Long[] getArtistIds() {
        return artistIds;
    }

    public void setArtistIds(Long[] artistIds) {
        this.artistIds = artistIds;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public List<ShowSector> getShowSectors() {
        return showSectors;
    }

    public void setShowSectors(List<ShowSector> showSectors) {
        this.showSectors.clear();
        if (showSectors != null) {
            showSectors.forEach(this::addShowSector);
        }
    }

    public void addShowSector(ShowSector showSector) {
        showSectors.add(showSector);
        showSector.setShow(this);
    }

    public void removeShowSector(ShowSector showSector) {
        showSectors.remove(showSector);
        showSector.setShow(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Show show)) {
            return false;
        }
        return Objects.equals(id, show.id)
            && Objects.equals(name, show.name)
            && Objects.equals(date, show.date)
            && Objects.equals(time, show.time)
            && Objects.equals(capacity, show.capacity)
            && Objects.equals(soldSeats, show.soldSeats)
            && Objects.equals(eventType, show.eventType)
            && Objects.equals(duration, show.duration)
            && Objects.equals(summary, show.summary)
            && Objects.equals(text, show.text)
            && Objects.equals(imageUrl, show.imageUrl)
            && Objects.equals(event, show.event)
            && Objects.equals(artists, show.artists)
            && Objects.equals(venue, show.venue)
            && Objects.equals(hall, show.hall)
            && Objects.equals(hallId, show.hallId)
            && Objects.equals(minPrice, show.minPrice)
            && Objects.equals(maxPrice, show.maxPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, date, time, capacity, soldSeats,
                          eventType, duration, summary, text, imageUrl, maxPrice, minPrice);
    }

    @Override
    public String toString() {
        return "Show{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", date=" + date
            + ", time=" + time
            + ", capacity=" + capacity
            + ", soldSeats=" + soldSeats
            + ", eventType='" + eventType + '\''
            + ", duration=" + duration
            + ", summary='" + summary + '\''
            + ", text='" + text + '\''
            + ", imageUrl='" + imageUrl + '\''
            + ", minPrice=" + minPrice + '\''
            + ", maxPrice=" + maxPrice + '\''
            + '}';
    }


    public static final class ShowBuilder {
        private Long id;
        private String name;
        private LocalDate date;
        private LocalTime time;
        private int capacity;
        private int soldSeats;
        private String eventType;
        private int duration;
        private String summary;
        private String text;
        private String imageUrl;
        private Event event;
        private Set<Artist> artists = new LinkedHashSet<>();
        private Venue venue;
        private Long hallId;
        private Hall hall;
        private double minPrice;
        private double maxPrice;

        private ShowBuilder() {
        }

        public static ShowBuilder aShow() {
            return new ShowBuilder();
        }

        public ShowBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ShowBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ShowBuilder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public ShowBuilder withTime(LocalTime time) {
            this.time = time;
            return this;
        }

        public ShowBuilder withCapacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public ShowBuilder withSoldSeats(int soldSeats) {
            this.soldSeats = soldSeats;
            return this;
        }

        public ShowBuilder withEventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public ShowBuilder withDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public ShowBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public ShowBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public ShowBuilder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public ShowBuilder withEvent(Event event) {
            this.event = event;
            return this;
        }

        public ShowBuilder withArtists(Set<Artist> artists) {
            this.artists = artists;
            return this;
        }

        public ShowBuilder withVenue(Venue venue) {
            this.venue = venue;
            return this;
        }

        public ShowBuilder withHallId(Long hallId) {
            this.hallId = hallId;
            return this;
        }

        public ShowBuilder withMinPrice(double minPrice) {
            this.minPrice = minPrice;
            return this;
        }

        public ShowBuilder withMaxPrice(double maxPrice) {
            this.maxPrice = maxPrice;
            return this;
        }

        public ShowBuilder withHall(Hall hall) {
            this.hall = hall;
            return this;
        }

        public Show build() {
            Show show = new Show();
            show.setId(id);
            show.setName(name);
            show.setDate(date);
            show.setTime(time);
            show.setCapacity(capacity);
            show.setSoldSeats(soldSeats);
            show.setEventType(eventType);
            show.setDuration(duration);
            show.setSummary(summary);
            show.setText(text);
            show.setImageUrl(imageUrl);
            show.setEvent(event);
            show.setArtists(artists);
            show.setVenue(venue);
            show.setHallId(hallId);
            show.setHall(hall);
            show.setMinPrice(minPrice);
            show.setMaxPrice(maxPrice);
            return show;
        }
    }
}