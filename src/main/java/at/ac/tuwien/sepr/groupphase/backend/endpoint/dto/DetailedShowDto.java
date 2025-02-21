package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class DetailedShowDto extends SimpleShowDto {

    private String text;
    private int capacity;
    private int soldSeats;
    private int duration;
    private VenueDto venue;
    private HallDto hall;
    private List<TicketDto> tickets;
    private List<ShowSectorDto> showSectors;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public VenueDto getVenue() {
        return venue;
    }

    public void setVenue(VenueDto venue) {
        this.venue = venue;
    }

    public HallDto getHall() {
        return hall;
    }

    public void setHall(HallDto hall) {
        this.hall = hall;
    }

    public List<TicketDto> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketDto> tickets) {
        this.tickets = tickets;
    }

    public List<ShowSectorDto> getShowSectors() {
        return showSectors;
    }

    public void setShowSectors(List<ShowSectorDto> showSectors) {
        this.showSectors = showSectors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DetailedShowDto that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return capacity == that.capacity
            && soldSeats == that.soldSeats
            && duration == that.duration
            && Objects.equals(text, that.text)
            && Objects.equals(venue, that.venue)
            && Objects.equals(hall, that.hall)
            && Objects.equals(showSectors, that.showSectors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text, capacity, soldSeats, duration, venue, hall, showSectors);
    }

    @Override
    public String toString() {
        return "DetailedShowDto{"
            + "text='" + text + '\''
            + ", capacity=" + capacity
            + ", soldSeats=" + soldSeats
            + ", duration=" + duration
            + ", venue=" + venue
            + ", hall=" + hall
            + ", showSectors=" + showSectors
            + "} " + super.toString();
    }

    public static final class DetailedShowDtoBuilder {
        private Long id;
        private String name;
        private LocalDate date;
        private LocalTime time;
        private String summary;
        private String eventType;
        private String imageUrl;
        private String text;
        private int capacity;
        private int soldSeats;
        private int duration;
        private VenueDto venue;
        private HallDto hall;
        private List<TicketDto> tickets;
        private List<ShowSectorDto> showSectors;

        private DetailedShowDtoBuilder() {
        }

        public static DetailedShowDtoBuilder aDetailedShowDto() {
            return new DetailedShowDtoBuilder();
        }

        public DetailedShowDtoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public DetailedShowDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public DetailedShowDtoBuilder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public DetailedShowDtoBuilder withTime(LocalTime time) {
            this.time = time;
            return this;
        }

        public DetailedShowDtoBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        public DetailedShowDtoBuilder withEventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public DetailedShowDtoBuilder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public DetailedShowDtoBuilder withText(String text) {
            this.text = text;
            return this;
        }

        public DetailedShowDtoBuilder withCapacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public DetailedShowDtoBuilder withSoldSeats(int soldSeats) {
            this.soldSeats = soldSeats;
            return this;
        }

        public DetailedShowDtoBuilder withDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public DetailedShowDtoBuilder withVenue(VenueDto venue) {
            this.venue = venue;
            return this;
        }

        public DetailedShowDtoBuilder withHall(HallDto hall) {
            this.hall = hall;
            return this;
        }

        public DetailedShowDtoBuilder withTickets(List<TicketDto> tickets) {
            this.tickets = tickets;
            return this;
        }

        public DetailedShowDtoBuilder withShowSectors(List<ShowSectorDto> showSectors) {
            this.showSectors = showSectors;
            return this;
        }

        public DetailedShowDto build() {
            DetailedShowDto detailedShowDto = new DetailedShowDto();
            detailedShowDto.setId(id);
            detailedShowDto.setName(name);
            detailedShowDto.setDate(date);
            detailedShowDto.setTime(time);
            detailedShowDto.setSummary(summary);
            detailedShowDto.setEventType(eventType);
            detailedShowDto.setImageUrl(imageUrl);
            detailedShowDto.setText(text);
            detailedShowDto.setCapacity(capacity);
            detailedShowDto.setSoldSeats(soldSeats);
            detailedShowDto.setDuration(duration);
            detailedShowDto.setVenue(venue);
            detailedShowDto.setHall(hall);
            detailedShowDto.setTickets(tickets);
            detailedShowDto.setShowSectors(showSectors);
            return detailedShowDto;
        }
    }
}