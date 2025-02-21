package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;


import java.util.Objects;

@Entity
@Table(name = "show_sector")
public class ShowSector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id")
    private Sector sector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "standing_sector_id")
    private StandingSector standingSector;

    @Column(nullable = false, columnDefinition = "DECIMAL(12,2)")
    private Double price;

    // Constructors
    public ShowSector() {
    }

    public ShowSector(Show show, Sector sector, StandingSector standingSector, Double price) {
        this.show = show;
        this.sector = sector;
        this.standingSector = standingSector;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public StandingSector getStandingSector() {
        return standingSector;
    }

    public void setStandingSector(StandingSector standingSector) {
        this.standingSector = standingSector;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    // equals and hashCode based on show and sector/standingSector
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShowSector)) {
            return false;
        }
        ShowSector that = (ShowSector) o;
        return Objects.equals(show, that.show)
            && Objects.equals(sector, that.sector)
            && Objects.equals(standingSector, that.standingSector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(show, sector, standingSector);
    }

    // toString method
    @Override
    public String toString() {
        return "ShowSector{"
            + "id=" + id
            + ", show=" + (show != null ? show.getId() : null)
            + ", sector=" + (sector != null ? sector.getId() : null)
            + ", standingSector=" + (standingSector != null ? standingSector.getId() : null)
            + ", price=" + price
            + '}';
    }
}