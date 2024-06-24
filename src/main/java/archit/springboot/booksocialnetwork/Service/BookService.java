package archit.springboot.booksocialnetwork.Service;

import archit.springboot.booksocialnetwork.Dto.BookDto;
import archit.springboot.booksocialnetwork.Dto.BookResponse;
import archit.springboot.booksocialnetwork.Dto.BorrowedBookResponse;
import archit.springboot.booksocialnetwork.Dto.PageResponse;
import archit.springboot.booksocialnetwork.Entity.Book;
import archit.springboot.booksocialnetwork.Entity.BookTransactionHistory;
import archit.springboot.booksocialnetwork.Entity.User;
import archit.springboot.booksocialnetwork.Exceptions.OperationNotPermittedException;
import archit.springboot.booksocialnetwork.Repository.BookRepository;
import archit.springboot.booksocialnetwork.Repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final TransactionRepository transactionRepository;
    private final FileStorageService fileStorageService;

    // common response method for BookResponse and BorrowedBookResponse
    public PageResponse<BookResponse> returnBookResponse(Page<Book> books) {
        List<BookResponse> bookResponseList = books.stream().map(BookMapper::toBookResponse).toList();
        return new PageResponse<>(bookResponseList, books.getNumber(), books.getSize(), books.getTotalElements(), books.getTotalPages(), books.isFirst(), books.isLast());
    }

    public PageResponse<BorrowedBookResponse> returnBorrowedBookResponse(Page<BookTransactionHistory> books) {
        List<BorrowedBookResponse> borrowedBookResponseList = books.stream().map(BookMapper::toBorrowedBookResponse).toList();
        return new PageResponse<>(borrowedBookResponseList, books.getNumber(), books.getSize(), books.getTotalElements(), books.getTotalPages(), books.isFirst(), books.isLast());
    }


    public Long saveBook(BookDto bookDto, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book = bookMapper.toBook(bookDto);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        return bookMapper.toBookResponse(book);
    }

    public PageResponse<BookResponse> getAllBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        return returnBookResponse(books);
    }

    public PageResponse<BookResponse> getAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllBooksByOwner(pageable, user.getId());
        return returnBookResponse(books);
    }


    public PageResponse<BorrowedBookResponse> getAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> bookTransactionHistories = transactionRepository.findAllBorrowedBooks(pageable, user.getId());
        return returnBorrowedBookResponse(bookTransactionHistories);
    }


    public PageResponse<BorrowedBookResponse> getAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> bookTransactionHistories = transactionRepository.findAllReturnedBooks(pageable, user.getId());
        return returnBorrowedBookResponse(bookTransactionHistories);
    }

    public Long updateShareableStatus(Long bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        User user = (User) connectedUser.getPrincipal();
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update books shareable status owned by another user");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return book.getId();
    }

    public Long updateArchiveStatus(Long bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        User user = (User) connectedUser.getPrincipal();
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update books Archived status owned by another user.");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return book.getId();
    }


    public Long borrowBookById(Long bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        if(book.isArchived()||!book.isShareable()){
            throw new OperationNotPermittedException("Requested book cannot be borrowed since it is not shareable/archived");
        }
        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow your own book buddy.");
        }
        if(transactionRepository.isAlreadyBorrowedByUser(user.getId(),bookId)){
            throw new OperationNotPermittedException("Requested Book is already borrowed.");
        }
        BookTransactionHistory bookTransactionHistory=BookTransactionHistory.builder()
                .book(book).returned(false).returnApprovedByTheOwner(false)
                .user(user)
                .build();
        return transactionRepository.save(bookTransactionHistory).getId();
    }

    public Long returnBorrowedBookById(Long bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        if(book.isArchived()||!book.isShareable()){
            throw new OperationNotPermittedException("Requested book cannot be borrowed since it is not shareable/archived");
        }
        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow your own book buddy.");
        }
        BookTransactionHistory bookTransactionHistory= transactionRepository.findByBookIdAndUserId(bookId,user.getId());
        if(bookTransactionHistory==null)throw new OperationNotPermittedException("You cannot return a book which you didn't borrow");
        else{
            bookTransactionHistory.setReturned(true);
            return transactionRepository.save(bookTransactionHistory).getId();
        }
    }

    public Long approveReturnBorrowedBookById(Long bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        if(book.isArchived()||!book.isShareable()){
            throw new OperationNotPermittedException("Requested book cannot be borrowed since it is not shareable/archived");
        }
        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow your own book buddy.");
        }
        BookTransactionHistory bookTransactionHistory= transactionRepository.findByBookIdAndOwnerId(bookId,user.getId());
        if(bookTransactionHistory==null)throw new OperationNotPermittedException("You cannot return a book which you didn't borrow");
        else{
            bookTransactionHistory.setReturnApprovedByTheOwner(true);
            return transactionRepository.save(bookTransactionHistory).getId();
        }
    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        User user=(User)connectedUser.getPrincipal();
        var bookCover=fileStorageService.saveFile(file,user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }
}
