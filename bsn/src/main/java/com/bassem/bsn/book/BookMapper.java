package com.bassem.bsn.book;

import com.bassem.bsn.file.FileUtils;
import com.bassem.bsn.history.BookTransactionHistory;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {
    public Book toBook(@Valid BookRequest request) {
        return Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .synopsis(request.getSynopsis())
                .shareable(request.isShareable())
                .archived(false)
                .build();
    }

    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .shareable(book.isShareable())
                .owner(book.getOwner().fullname())
                .rate(book.getRate())
                .archived(book.isArchived())
                .cover(FileUtils.getFileFromLocation(book.getBookCover()))
                .build();

    }

    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory bookTransactionHistory) {
        return BorrowedBookResponse.builder()
                .id(bookTransactionHistory.getBook().getId())
                .title(bookTransactionHistory.getBook().getTitle())
                .author(bookTransactionHistory.getBook().getAuthor())
                .isbn(bookTransactionHistory.getBook().getIsbn())
                .rate(bookTransactionHistory.getBook().getRate())
                .returned(bookTransactionHistory.isReturned())
                .returnedApproved(bookTransactionHistory.isReturnedApproved())
                .build();
    }

    public DropDownResponse toDropDownResponse(Book book) {
        String synopsis = book.getSynopsis();
        String snippet;
        // Check if synopsis is null or shorter than 50 characters
        if (synopsis != null && synopsis.length() > 50) {
            snippet = synopsis.substring(0, 50) + "…"; // add ellipsis
        } else {
            snippet = synopsis + "…"; // full synopsis if shorter than 50 chars
        }
        return DropDownResponse.builder().
                id(book.getId()).
                title(book.getTitle()).
                author(book.getAuthor()).
                synopsis(snippet).
                build();
    }
}
