package archit.springboot.booksocialnetwork.Controller;

import archit.springboot.booksocialnetwork.Dto.BookDto;
import archit.springboot.booksocialnetwork.Dto.BookResponse;
import archit.springboot.booksocialnetwork.Dto.BorrowedBookResponse;
import archit.springboot.booksocialnetwork.Dto.PageResponse;
import archit.springboot.booksocialnetwork.Entity.Book;
import archit.springboot.booksocialnetwork.Service.BookService;
import jakarta.mail.Multipart;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @PostMapping()
    public ResponseEntity<Long> saveBook(@RequestBody @Valid BookDto bookDto, Authentication connectedUser) {
        return ResponseEntity.ok(bookService.saveBook(bookDto,connectedUser));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookService.getBookById(bookId));
    }
    @GetMapping("/allBooks")
    public ResponseEntity<PageResponse<BookResponse>> getAllBooks(@RequestParam(name = "page",defaultValue = "0",required = false) int page,
                                                                  @RequestParam(name = "size",defaultValue = "20",required = false) int size,
                                                                  Authentication connectedUser
                                                                  ){
        return ResponseEntity.ok(bookService.getAllBooks(page, size, connectedUser));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>>findAllBooksByOwner(@RequestParam(name = "page",defaultValue = "0",required = false) int page,
                                                                         @RequestParam(name = "size",defaultValue = "20",required = false) int size,
                                                                         Authentication connectedUser){
         return ResponseEntity.ok(bookService.getAllBooksByOwner(page, size, connectedUser));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(@RequestParam(name = "page",defaultValue = "0",required = false) int page,
                                                                                   @RequestParam(name = "size",defaultValue = "20",required = false) int size,
                                                                                   Authentication connectedUser){
        return ResponseEntity.ok(bookService.getAllBorrowedBooks(page, size, connectedUser));
    }

    @GetMapping("/returnedBooks")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(@RequestParam(name = "page",defaultValue = "0",required = false) int page,
                                                                                   @RequestParam(name = "size",defaultValue = "20",required = false) int size,
                                                                                   Authentication connectedUser){
        return ResponseEntity.ok(bookService.getAllReturnedBooks(page, size, connectedUser));
    }

    @PatchMapping("/shareable/{bookId}")
    public ResponseEntity<Long> updateShareableStatus(@PathVariable Long bookId,Authentication connectedUser){
        return ResponseEntity.ok(bookService.updateShareableStatus(bookId, connectedUser));
    }

    @PatchMapping("/archived/{bookId}")
    public ResponseEntity<Long> updateArchivedStatus(@PathVariable Long bookId,Authentication connectedUser){
        return ResponseEntity.ok(bookService.updateArchiveStatus(bookId,connectedUser));
    }

    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<Long>borrowBookById(@PathVariable Long bookId,Authentication connectedUser){
        return ResponseEntity.ok(bookService.borrowBookById(bookId,connectedUser));
    }

    @PatchMapping("/borrow/return/{bookId}")
    public ResponseEntity<Long>returnBorrowedBookById(@PathVariable Long bookId,Authentication connectedUser){
        return ResponseEntity.ok(bookService.returnBorrowedBookById(bookId,connectedUser));
    }
    @PatchMapping("/borrow/return/approve/{bookId}")
    public ResponseEntity<Long>approveReturnedBookById(@PathVariable Long bookId,Authentication connectedUser){
        return ResponseEntity.ok(bookService.approveReturnBorrowedBookById(bookId,connectedUser));
    }
    @PostMapping(value = "/cover/{bookId}",consumes = "multipart/form-data")
    public ResponseEntity<?>uploadBookCoverPicture(@PathVariable Long bookId,
                                                  @RequestPart("file")MultipartFile file, Authentication connectedUser){

        bookService.uploadBookCoverPicture(file,connectedUser,bookId);
        return ResponseEntity.accepted().build();
    }
}


