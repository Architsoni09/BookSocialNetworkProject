package archit.springboot.booksocialnetwork.Controller;

import archit.springboot.booksocialnetwork.Dto.FeedbackRequest;
import archit.springboot.booksocialnetwork.Dto.FeedbackResponse;
import archit.springboot.booksocialnetwork.Dto.PageResponse;
import archit.springboot.booksocialnetwork.Service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Long> saveFeedback(@Valid @RequestBody FeedbackRequest request, Authentication connectedUser){
        return ResponseEntity.ok(feedbackService.saveFeedback(request, connectedUser));
    }
    @GetMapping("/book/{book-id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbacksByBook(
            @PathVariable("book-id") Long bookId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(feedbackService.findAllFeedbacksByBook(bookId, page, size, connectedUser));
    }
}
