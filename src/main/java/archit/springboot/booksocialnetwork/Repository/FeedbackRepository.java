package archit.springboot.booksocialnetwork.Repository;

import archit.springboot.booksocialnetwork.Entity.Book;
import archit.springboot.booksocialnetwork.Entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback,Long> {

    @Query("select f from Feedback f where f.book.id=:id")
    Page<Feedback> findAllRelatedFeedbacks(Pageable pageable, Long id);
}
