package archit.springboot.booksocialnetwork.Dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {
    private Long id;
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String owner;
    private boolean archived;
    private byte[] cover;
    private double rating;
    private boolean shareable;
}
