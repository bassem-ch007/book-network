package com.bassem.bsn.book;

import com.bassem.bsn.common.PageResponse;
import com.bassem.bsn.exception.OperationNotPermittedException;
import com.bassem.bsn.file.FileStorageService;
import com.bassem.bsn.history.BookTransactionHistory;
import com.bassem.bsn.history.BookTransactionHistoryRepository;
import com.bassem.bsn.history.BookTransactionHistorySpecification;
import com.bassem.bsn.user.User;
import com.bassem.bsn.waitinglist.WaitingList;
import com.bassem.bsn.waitinglist.WaitingListMapper;
import com.bassem.bsn.waitinglist.WaitingListRepository;
import com.bassem.bsn.waitinglist.WaitingListSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookService {
    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final WaitingListMapper waitingListMapper;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
    private final FileStorageService fileStorageService;
    private final WaitingListRepository waitingListRepository;

    public Integer save(@Valid BookRequest request, Authentication authenticatedUser) {
        User user =(User)authenticatedUser.getPrincipal();
        Book book=bookMapper.toBook(request);
        book.setOwner(user);
        if(request.getId()!=null){
            book.setId(request.getId());
        }
        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId).map(bookMapper::toBookResponse).orElseThrow(()->new EntityNotFoundException("Book not found"));
    }
    //shows books except owned by authenticated user
    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication authenticatedUser)
    {
        if (page < 0){log.error("page is negative");}
        User user =(User)authenticatedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Book> books = bookRepository.findAll(BookSpecification.displayableFor(user.getId()),pageable);
        List<BookResponse> bookResponses=books
                .stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalPages(),
                books.getTotalElements(),
                books.isFirst(),
                books.isLast());
    }

    public PageResponse<BookResponse>   findAllBooksById(int page, int size, Authentication authenticatedUser)
    {
        User user=(User)authenticatedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Book> books = bookRepository.findAll(BookSpecification.isOwnedBy(user.getId()),pageable);
        List<BookResponse>bookResponses=books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalPages(),
                books.getTotalElements(),
                books.isFirst(),
                books.isLast());
    }
    //shows the borrowed books of the authenticated user
    public PageResponse<BorrowedBookResponse> findAllBorrowedBooksByUserId(int page, int size, Authentication authenticatedUser)
    {
        User user=(User)authenticatedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<BookTransactionHistory> bookTransactionHistories = bookTransactionHistoryRepository.findAll(BookTransactionHistorySpecification.isBorrowedBy(user.getId()),pageable);
        List<BorrowedBookResponse>borrowedBookResponses=bookTransactionHistories.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(borrowedBookResponses,
                bookTransactionHistories.getNumber(),
                bookTransactionHistories.getSize(),
                bookTransactionHistories.getTotalPages(),
                bookTransactionHistories.getTotalElements(),
                bookTransactionHistories.isFirst(),
                bookTransactionHistories.isLast());
    }


    public PageResponse<BorrowedBookResponse> findAllReturnedBooksById(int page, int size, Authentication authenticatedUser)
    {
        User user=(User)authenticatedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<BookTransactionHistory> bookTransactionHistories = bookTransactionHistoryRepository.findAll(BookTransactionHistorySpecification.displayReturnedBooksFor(user.getId()),pageable);
        List<BorrowedBookResponse>borrowedBookResponses=bookTransactionHistories.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(borrowedBookResponses,
                bookTransactionHistories.getNumber(),
                bookTransactionHistories.getSize(),
                bookTransactionHistories.getTotalPages(),
                bookTransactionHistories.getTotalElements(),
                bookTransactionHistories.isFirst(),
                bookTransactionHistories.isLast());
    }

    public Integer updateShareableStatus(Integer bookId, Authentication authenticatedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("Book not found"));
        User user = (User)authenticatedUser.getPrincipal();
        if (!Objects.equals(user.getId(),book.getOwner().getId())) {throw new OperationNotPermittedException("cannot update book shareable status,only owner");}
        book.setShareable(!book.isShareable());
        return bookRepository.save(book).getId();
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication authenticatedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("Book not found"));
        User user = (User)authenticatedUser.getPrincipal();
        if (!Objects.equals(user.getId(),book.getOwner().getId())) {throw new OperationNotPermittedException("cannot update book archived status,only owner");}
        book.setArchived(!book.isArchived());
        return bookRepository.save(book).getId();
    }

    public Integer borrowBook(Integer bookId, Authentication authenticatedUser) {
        Book book=bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("Book not found"));
        if (book.isArchived()) {
            throw new OperationNotPermittedException("book is archived");
        }
        if (!book.isShareable()) {
            throw new OperationNotPermittedException("book is not shareable");
        }
        User user = (User)authenticatedUser.getPrincipal();
        if (Objects.equals(user.getId(),book.getOwner().getId())) {
            throw new OperationNotPermittedException("cannot borrow your own book");
        }
        if (bookTransactionHistoryRepository.findOne(BookTransactionHistorySpecification.isAlreadyBorrowedByUser(bookId,user.getId())).isPresent())
        {
            log.warn("User {} attempted to borrow book {} which is already borrowed in transaction", user.getId(), bookId);
            throw new OperationNotPermittedException("you have already borrowed the book");
        }
        if (bookTransactionHistoryRepository.exists(BookTransactionHistorySpecification.isAlreadyBorrowed(bookId)))
        {
            throw new OperationNotPermittedException("book is already borrowed");
        }

        BookTransactionHistory bookTransactionHistory=BookTransactionHistory.builder()
                .book(book)
                .user(user)
                .returned(false)
                .returnedApproved(false)
                .build();
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication authenticatedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("Book not found"));
        if (book.isArchived()) {
            throw new OperationNotPermittedException("book is archived");
        }
        if (!book.isShareable()) {
            throw new OperationNotPermittedException("book is not shareable");
        }
        User user = (User)authenticatedUser.getPrincipal();
        if (Objects.equals(user.getId(),book.getOwner().getId()))
        {
            throw new OperationNotPermittedException("cannot return your own book");
        }
        BookTransactionHistory bookTransactionHistory=bookTransactionHistoryRepository
                .findOne(BookTransactionHistorySpecification.isAlreadyBorrowedByUser(bookId,user.getId()))
                .orElseThrow(()->new OperationNotPermittedException("you did not borrow the book"));
        bookTransactionHistory.setReturned(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer approveBorrowedBook(Integer bookId, Authentication authenticatedUser) {
        Book book=bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("Book not found"));
        if (book.isArchived())
        {
            throw new OperationNotPermittedException("book is archived");
        }
        if (!book.isShareable())
        {
            throw new OperationNotPermittedException("book is not shareable");
        }
        User user = (User)authenticatedUser.getPrincipal();
        if (!Objects.equals(user.getId(),book.getOwner().getId()))
        {
            throw new OperationNotPermittedException("cannot approve the book,only owner");
        }
        if (bookTransactionHistoryRepository.findOne(BookTransactionHistorySpecification.isAlreadyBorrowed(bookId)).isEmpty())
        {
            throw new OperationNotPermittedException("book is not borrowed");
        }
        BookTransactionHistory bookTransactionHistory=bookTransactionHistoryRepository
                .findOne(BookTransactionHistorySpecification.isAlreadyReturnedNotApproved(bookId,user.getId()))
                .orElseThrow(()->new OperationNotPermittedException("book is not returned"));
        bookTransactionHistory.setReturnedApproved(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void uploadBookCoverPicture(MultipartFile file, Integer bookId, Authentication authenticatedUser){
        Book book=bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("Book not found"));
        User user = (User)authenticatedUser.getPrincipal();
        if (!Objects.equals(user.getId(),book.getOwner().getId())){
            throw new OperationNotPermittedException("cannot upload cover picture,only the owner of the book");
        }
        var bookCover=fileStorageService.saveFile(file,user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }

    public PageResponse<BookResponse> findMyWaitingList(int page, int size, Authentication authenticatedUser) {
        if (page < 0)log.error("page is negative");
        User user =(User)authenticatedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<WaitingList> awaitingBooks = waitingListRepository.findAll(WaitingListSpecification.displayableForUser(user.getId()),pageable);
        List<BookResponse> bookResponses=awaitingBooks
                .stream()
                .map(waitingListMapper::toWaitingList)
                .toList();
        return new PageResponse<>(bookResponses,
                awaitingBooks.getNumber(),
                awaitingBooks.getSize(),
                awaitingBooks.getTotalPages(),
                awaitingBooks.getTotalElements(),
                awaitingBooks.isFirst(),
                awaitingBooks.isLast());
    }

    public List<DropDownResponse> autocompleteBooks(Authentication authenticatedUser, String query) {
        User user =(User)authenticatedUser.getPrincipal();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdDate"));
        List<Book> books = bookRepository.findTopRatedBooksByTitleStartingWith(user.getId(),query.toLowerCase(),pageable);
        return  books
                .stream()
                .map(bookMapper::toDropDownResponse)
                .toList();
    }

    public PageResponse<BookResponse> fullSearchBooks(Authentication authenticatedUser, String query, int page, int size) {
        if (page < 0)log.error("page is negative");
        User user =(User)authenticatedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Book> books = bookRepository.findAll(BookSpecification.searchBooks(user.getId(),query),pageable);
        List<BookResponse> bookResponses=books
                .stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalPages(),
                books.getTotalElements(),
                books.isFirst(),
                books.isLast());
    }
    public Boolean toggleWishedBook(Integer bookId, Authentication authenticatedUser) {
        Book book=bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("Book not found"));
        if (book.isArchived()) {
            throw new OperationNotPermittedException("book is archived");
        }
        if (!book.isShareable()) {
            throw new OperationNotPermittedException("book is not shareable");
        }
        User user = (User)authenticatedUser.getPrincipal();
        if (Objects.equals(user.getId(),book.getOwner().getId())) {
            throw new OperationNotPermittedException("cannot add your own book to your waiting list");
        }
        WaitingList wl = waitingListRepository.findOne(WaitingListSpecification.isWished(bookId,user.getId()))
                .orElse(WaitingList.builder().
                        book(book).
                        user(user).
                        active(false).
                        build());
        wl.setActive(!wl.isActive());
        return waitingListRepository.save(wl).isActive();
    }

}

