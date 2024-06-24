package archit.springboot.booksocialnetwork.Dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BorrowedBookResponse {
    private Long id;
    private String title;
    private String authorName;
    private String isbn;
    private double rating;
    private boolean shareable;
    private boolean returned;
    private boolean returnApproved;

}
