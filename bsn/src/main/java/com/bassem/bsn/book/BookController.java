package com.bassem.bsn.book;

import com.bassem.bsn.common.CommonResponse;
import com.bassem.bsn.common.PageResponse;
import com.bassem.bsn.waitinglist.WaitingListRepository;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "book")
public class BookController {
    private final BookService bookService;
    private final WaitingListRepository waitingListRepository;

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<Integer>> registerBook(@RequestBody @Valid BookRequest book, Authentication authenticatedUser)
    {
        return ResponseEntity.ok(CommonResponse.<Integer>builder().data(bookService.save(book,authenticatedUser)).build());
    }
    @GetMapping("{book-id}")
    public ResponseEntity<BookResponse> findById(@PathVariable(name = "book-id")Integer bookId)
    {
        return ResponseEntity.ok(bookService.findById(bookId));
    }
    @GetMapping("/books")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(@RequestParam(name = "page",defaultValue = "0",required = false)int page,
                                                                   @RequestParam(name = "size",defaultValue = "10",required = false)int size,
                                                                   Authentication authenticatedUser )
    {
        return ResponseEntity.ok(bookService.findAllBooks(page,size,authenticatedUser));
    }
    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllPersonalBooks(@RequestParam(name = "page",defaultValue = "0",required = false)int page,
                                                                           @RequestParam(name = "size",defaultValue = "10",required = false)int size,
                                                                           Authentication authenticatedUser)
    {
        return ResponseEntity.ok(bookService.findAllBooksById(page,size,authenticatedUser));
    }
    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(@RequestParam(name = "page",defaultValue = "0",required = false)int page,
                                                                                   @RequestParam(name = "size",defaultValue = "10",required = false)int size,
                                                                                   Authentication authenticatedUser)
    {
        return ResponseEntity.ok(bookService.findAllBorrowedBooksByUserId(page,size,authenticatedUser));
    }
    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(@RequestParam(name = "page",defaultValue = "0",required = false)int page,
                                                                                   @RequestParam(name = "size",defaultValue = "10",required = false)int size,
                                                                                   Authentication authenticatedUser)
    {
        return ResponseEntity.ok(bookService.findAllReturnedBooksById(page,size,authenticatedUser));
    }
    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateShareableStatus(@PathVariable(name = "book-id")Integer bookId,
                                                         Authentication authenticatedUser)
    {
        return ResponseEntity.ok(bookService.updateShareableStatus(bookId,authenticatedUser));
    }
    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer> updateArchivedStatus(@PathVariable(name = "book-id")Integer bookId,
                                                        Authentication authenticatedUser)
    {
        return ResponseEntity.ok(bookService.updateArchivedStatus(bookId,authenticatedUser));
    }
    @PostMapping("/borrow/{book-id}")
    public ResponseEntity<Integer> borrowBook(@PathVariable(name = "book-id")Integer bookId,Authentication authenticatedUser)
    {
        return ResponseEntity.ok(bookService.borrowBook(bookId,authenticatedUser));
    }
    @PatchMapping("/borrow/return/{book-id}")
    public ResponseEntity<Integer> returnBorrowedBook(@PathVariable(name = "book-id")Integer bookId,Authentication authenticatedUser)
    {
        return ResponseEntity.ok(bookService.returnBorrowedBook(bookId,authenticatedUser));
    }
    @PatchMapping("/borrow/return/approve/{book-id}")
    public ResponseEntity<Integer> approveBorrowedBook(@PathVariable(name = "book-id")Integer bookId,Authentication authenticatedUser)
    {
        return ResponseEntity.ok(bookService.approveBorrowedBook(bookId,authenticatedUser));
    }
    @PostMapping(value = "/cover/{book-id}" ,consumes = "multipart/form-data")
    public ResponseEntity<?> uploadBookCoverPicture(@PathVariable(name = "book-id")Integer bookId
            ,@Parameter(description = "Cover image file (JPEG or PNG)") @RequestPart("file") MultipartFile file
            ,Authentication authenticatedUser) {
        bookService.uploadBookCoverPicture(file,bookId,authenticatedUser);
        return ResponseEntity.accepted().build();
    }
    @PostMapping("/waiting-list/toggle/{book-id}")
    public ResponseEntity<CommonResponse<Boolean>> toggleWishedBook(@PathVariable(name = "book-id")Integer bookId,Authentication authenticatedUser)
    {
        return ResponseEntity.ok(CommonResponse.<Boolean>builder().data(bookService.toggleWishedBook(bookId,authenticatedUser)).build());
    }

    @GetMapping("/my-waiting-list")
    public ResponseEntity<PageResponse<BookResponse>> MyWaitingList(@RequestParam(name = "page",defaultValue = "0",required = false)int page,
                                                                    @RequestParam(name = "size",defaultValue = "10",required = false)int size,
                                                                    Authentication authenticatedUser) {
        return ResponseEntity.ok(bookService.findMyWaitingList(page,size,authenticatedUser));
    }
    @GetMapping("/autocomplete")
    public ResponseEntity<List<DropDownResponse>> autocompleteBooks(@RequestParam("query") String query,Authentication authenticatedUser) {
        //return a lightweight DTO if you want to limit payload
        return ResponseEntity.ok(bookService.autocompleteBooks(authenticatedUser,query));
    }
    @GetMapping("/search")
    public ResponseEntity<PageResponse<BookResponse>> searchBooks(
            @RequestParam("query") String query,
            @RequestParam(name = "page",defaultValue = "0",required = false)int page,
            @RequestParam(name = "size",defaultValue = "10",required = false)int size,
            Authentication authenticatedUser
    ) {

        return ResponseEntity.ok(bookService.fullSearchBooks(authenticatedUser, query, page, size));
    }

}
