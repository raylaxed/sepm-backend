package at.ac.tuwien.sepr.groupphase.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class StandingSector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sectorName;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @Column(nullable = false)
    @NotNull
    private Integer capacity;

    @Column(nullable = false)
    @NotNull
    private Integer takenCapacity;

    @Column(nullable = false)
    @NotNull
    private Integer positionX1;

    @Column(nullable = false)
    @NotNull
    private Integer positionY1;

    @Column(nullable = false)
    @NotNull
    private Integer positionX2;

    @Column(nullable = false)
    @NotNull
    private Integer positionY2;

    @Column(nullable = false)
    @NotNull
    private Long price;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSectorName() {
        return sectorName;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getTakenCapacity() {
        return takenCapacity;
    }

    public void setTakenCapacity(Integer takenCapacity) {
        this.takenCapacity = takenCapacity;
    }

    public Integer getPositionX1() {
        return positionX1;
    }

    public void setPositionX1(Integer positionX1) {
        this.positionX1 = positionX1;
    }

    public Integer getPositionY1() {
        return positionY1;
    }

    public void setPositionY1(Integer positionY1) {
        this.positionY1 = positionY1;
    }

    public Integer getPositionX2() {
        return positionX2;
    }

    public void setPositionX2(Integer positionX2) {
        this.positionX2 = positionX2;
    }

    public Integer getPositionY2() {
        return positionY2;
    }

    public void setPositionY2(Integer positionY2) {
        this.positionY2 = positionY2;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "StandingSector{"
            + "id=" + id
            + ", sectorName=" + sectorName
            + ", capacity=" + capacity
            + ", takenCapacity=" + takenCapacity
            + ", positionX1=" + positionX1
            + ", positionY1=" + positionY1
            + ", positionX2=" + positionX2
            + ", positionY2=" + positionY2
            + ", price=" + price
            + '}';
    }
}