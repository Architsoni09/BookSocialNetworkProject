package archit.springboot.booksocialnetwork.Repository;

import archit.springboot.booksocialnetwork.Entity.Book;
import archit.springboot.booksocialnetwork.Entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {

    @Query("select book from Book book where book.archived=false and book.shareable=true and book.owner.id!=:userId")
    Page<Book> findAllDisplayableBooks(Pageable pageable,@Param("userId") Long id);

    @Query("select book from Book book where book.archived=false and book.shareable=true and book.owner.id=:userId")
    Page<Book> findAllBooksByOwner(Pageable pageable,@Param("userId")  Long userId);
}
