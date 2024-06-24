package archit.springboot.booksocialnetwork.Repository;
import archit.springboot.booksocialnetwork.Entity.Book;
import archit.springboot.booksocialnetwork.Entity.BookTransactionHistory;
import archit.springboot.booksocialnetwork.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<BookTransactionHistory,Long> {

    @Query("select bt from BookTransactionHistory bt where bt.user.id=:userId")
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable,@Param("userId") Long userId);

    @Query("select bt from BookTransactionHistory bt where bt.book.owner.id=:userId and bt.returned=true")
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable,@Param("userId") Long userId);

    @Query("select (count(*)>0) as isBorrowed from BookTransactionHistory bt where bt.book.id=:bookId and bt.user.id=:id and bt.returned=false")
    boolean isAlreadyBorrowedByUser(@Param("id") Long id, @Param("bookId")Long bookId);

    @Query("select bt from BookTransactionHistory as bt where bt.user.id=:userId and bt.book.id=:bookId and bt.returned=false and bt.returnApprovedByTheOwner=false")
    BookTransactionHistory findByBookIdAndUserId(Long bookId,@Param("userId") Long userId);

    @Query("select bt from BookTransactionHistory as bt where bt.book.owner.id=:userId and bt.book.id=:bookId and bt.returned=true and bt.returnApprovedByTheOwner=false")
    BookTransactionHistory findByBookIdAndOwnerId(Long bookId,@Param("userId") Long userId);
}
