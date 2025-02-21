package at.ac.tuwien.sepr.groupphase.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;

import java.util.ArrayList;
import java.util.List;

@Entity
public class SeenNews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    @ManyToMany
    @JoinTable(
        name = "seen_news_mapping",
        joinColumns = @JoinColumn(name = "seen_news_id"),
        inverseJoinColumns = @JoinColumn(name = "news_id")
    )
    private List<News> seenNews = new ArrayList<>();

    public void setUser(ApplicationUser user) {
        this.user = user;
    }

    public List<News> getSeenNews() {
        return this.seenNews;
    }
}
