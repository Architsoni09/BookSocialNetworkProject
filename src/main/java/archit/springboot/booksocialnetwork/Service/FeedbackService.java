package archit.springboot.booksocialnetwork.Service;

import archit.springboot.booksocialnetwork.Dto.FeedbackRequest;
import archit.springboot.booksocialnetwork.Dto.FeedbackResponse;
import archit.springboot.booksocialnetwork.Dto.PageResponse;
import archit.springboot.booksocialnetwork.Entity.Book;
import archit.springboot.booksocialnetwork.Entity.Feedback;
import archit.springboot.booksocialnetwork.Entity.User;
import archit.springboot.booksocialnetwork.Exceptions.OperationNotPermittedException;
import archit.springboot.booksocialnetwork.Repository.BookRepository;
import archit.springboot.booksocialnetwork.Repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final BookRepository bookRepository;
    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;

    public Long saveFeedback(FeedbackRequest request, Authentication connectedUser) {
        Book book = bookRepository.findById(request.getBookId()).orElseThrow(() -> new RuntimeException("Book not found"));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You cannot give feedback for an archived or non-shareable book");
        }
        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot give feedback on your own book buddy.");
        }
        Feedback feedback = feedbackMapper.toFeedback(request);
        return feedbackRepository.save(feedback).getId();
    }


    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Long bookId,
                                                                 int page,
                                                                 int size,
                                                                 Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Feedback> feedbacks = feedbackRepository.findAllRelatedFeedbacks(pageable, bookId);
        List<FeedbackResponse> feedbackResponseList = feedbacks.stream().map(f -> feedbackMapper.toFeedbackResponse(f, user.getId())).toList();
        return new PageResponse<>(feedbackResponseList,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast());
    }
}
