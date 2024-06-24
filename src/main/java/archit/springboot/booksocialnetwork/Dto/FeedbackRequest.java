package archit.springboot.booksocialnetwork.Dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class FeedbackRequest {

    @Positive(message = "200")
    @Min(message = "201",value =0)
    @Max(message = "202",value =5)
    private Double note;

    @NotNull(message = "203")
    @NotEmpty(message = "203")
    @NotBlank(message = "203")
    private String comment;

    @NotNull(message = "204")
    private Long bookId;

}
