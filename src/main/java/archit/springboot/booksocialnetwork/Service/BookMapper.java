package archit.springboot.booksocialnetwork.Service;

import archit.springboot.booksocialnetwork.Dto.BookDto;
import archit.springboot.booksocialnetwork.Dto.BookResponse;
import archit.springboot.booksocialnetwork.Dto.BorrowedBookResponse;
import archit.springboot.booksocialnetwork.Dto.FileUtil;
import archit.springboot.booksocialnetwork.Entity.Book;
import archit.springboot.booksocialnetwork.Entity.BookTransactionHistory;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {
    public static Book toBook(BookDto bookDto) {


        return Book.builder().id(bookDto.id())
                .title(bookDto.title())
                .authorName(bookDto.authorName())
                .synopsis(bookDto.synopsis())
                .archived(false)
                .shareable(bookDto.sharable())
                .build();
    }
    public static BookResponse toBookResponse(Book book){
        return BookResponse.builder().isbn(book.getIsbn()).id(book.getId())
                .title(book.getTitle()).authorName(book.getAuthorName())
                .synopsis(book.getSynopsis()).archived(book.isArchived())
                .cover(FileUtil.readFileFromLocation(book.getBookCover()))
                .owner(book.getOwner().fullName())
                .shareable(book.isShareable()).rating(book.getRate()).build();
    }
    public static BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory bookTransaction){
        return BorrowedBookResponse.builder().id(bookTransaction.getId())
                .title(bookTransaction.getBook().getTitle()).authorName(bookTransaction.getBook().getAuthorName())
                .isbn(bookTransaction.getBook().getIsbn()).rating(bookTransaction.getBook().getRate()).shareable(bookTransaction.getBook().isShareable())
                .returned(bookTransaction.isReturned()).returnApproved(bookTransaction.isReturnApprovedByTheOwner()).build();
    }
}
