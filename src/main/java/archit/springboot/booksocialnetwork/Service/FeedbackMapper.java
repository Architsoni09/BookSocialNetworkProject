package archit.springboot.booksocialnetwork.Service;

import archit.springboot.booksocialnetwork.Dto.FeedbackRequest;
import archit.springboot.booksocialnetwork.Dto.FeedbackResponse;
import archit.springboot.booksocialnetwork.Entity.Book;
import archit.springboot.booksocialnetwork.Entity.Feedback;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {
    public Feedback toFeedback(FeedbackRequest request) {
        return Feedback.builder()
                .note(request.getNote())
                .comment(request.getComment())
                .book(Book.builder()
                        .id(request.getBookId())
                        .shareable(false)
                        .archived(false)
                        .build()
                )
                .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Long id) {
        return FeedbackResponse.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), id))
                .build();
    }
}
