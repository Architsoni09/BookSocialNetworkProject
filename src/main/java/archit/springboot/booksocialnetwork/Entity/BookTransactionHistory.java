package archit.springboot.booksocialnetwork.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class BookTransactionHistory extends BaseEntity{
    //user Relationship
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    //book Relationship
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "book_id")
    private Book book;

    private boolean returned;
    private boolean returnApprovedByTheOwner;

}
