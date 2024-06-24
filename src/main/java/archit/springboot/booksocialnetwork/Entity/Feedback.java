package archit.springboot.booksocialnetwork.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Feedback extends BaseEntity {

    private Double note; // 1-5 stars
    private String comment;

    @ManyToOne
    @JoinColumn(name="book_id")
    @JsonIgnore
    private Book book;
}
