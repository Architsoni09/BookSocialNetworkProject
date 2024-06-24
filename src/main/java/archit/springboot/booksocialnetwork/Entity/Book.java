package archit.springboot.booksocialnetwork.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Table(name = "book")
@EntityListeners(AuditingEntityListener.class)
public class Book extends BaseEntity {
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "book")
    List<BookTransactionHistory> bookTransactionHistory;

    @Transient
    public double getRate() {
        if (feedbacks.isEmpty()) return 0;
        double totalRate = 0;
        for (Feedback feedback : feedbacks) {
            totalRate+= feedback.getNote();
        }
        return Math.floor(totalRate / feedbacks.size());
    }
}
