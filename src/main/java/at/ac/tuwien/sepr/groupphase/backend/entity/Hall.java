package at.ac.tuwien.sepr.groupphase.backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Hall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer canvasWidth;

    @Column(nullable = false)
    private Integer canvasHeight;

    @JsonManagedReference
    @OneToOne(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
    private Stage stage;

    @JsonManagedReference
    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL)
    private Set<Sector> sectors = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = true)
    private Venue venue;

    @OneToMany
    @JoinColumn(name = "hall_id")
    private List<Show> shows = new ArrayList<>();

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @JsonManagedReference
    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL)
    private Set<StandingSector> standingSectors = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(Integer canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public Integer getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(Integer canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        if (stage != null) {
            stage.setHall(this);
        }
    }

    public Set<Sector> getSectors() {
        return sectors;
    }

    public void setSectors(Set<Sector> sectors) {
        this.sectors = sectors;
    }

    public void addSector(Sector sector) {
        sectors.add(sector);
        sector.setHall(this);  // This is crucial for bidirectional relationship
    }

    public void removeSector(Sector sector) {
        sectors.remove(sector);
        sector.setHall(null);
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public List<Show> getShows() {
        return shows;
    }

    public void setShows(List<Show> shows) {
        this.shows = shows;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Set<StandingSector> getStandingSectors() {
        return standingSectors;
    }

    public void setStandingSectors(Set<StandingSector> standingSectors) {
        this.standingSectors = standingSectors;
    }

    public void addStandingSector(StandingSector standingSector) {
        standingSectors.add(standingSector);
        standingSector.setHall(this);
    }

    public void removeStandingSector(StandingSector standingSector) {
        standingSectors.remove(standingSector);
        standingSector.setHall(null);
    }

    @Override
    public String toString() {
        return "Hall{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", capacity=" + capacity
            + ", canvasWidth=" + canvasWidth
            + ", canvasHeight=" + canvasHeight
            + ", stage=" + stage
            + ", sectors=" + sectors
            + '}';
    }
}