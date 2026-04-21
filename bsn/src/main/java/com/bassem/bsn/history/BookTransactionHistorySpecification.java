package com.bassem.bsn.history;

import org.springframework.data.jpa.domain.Specification;

public class BookTransactionHistorySpecification {
    public static Specification<BookTransactionHistory> isOwnedBy(int userId)
    {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("book").get("owner").get("id"), userId);
    }
    public static Specification<BookTransactionHistory> isBorrowedBy(int userId)
    {
        return (root, query, criteriaBuilder) ->  criteriaBuilder.equal(root.get("user").get("id"), userId);
    }
    public static Specification<BookTransactionHistory> isOwnedByBook(int bookId)
    {
        return (root, query, criteriaBuilder) ->   criteriaBuilder.equal(root.get("book").get("id"), bookId);
    }
    public static Specification<BookTransactionHistory> isReturnedApproved()
    {
        return (root, query, criteriaBuilder) ->  criteriaBuilder.isTrue(root.get("returnedApproved"));
    }
    public static Specification<BookTransactionHistory> isNotReturnedApproved()
    {
        return (root, query, criteriaBuilder) ->  criteriaBuilder.isFalse(root.get("returnedApproved"));
    }

    public static Specification<BookTransactionHistory> isReturned()
    {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("returned"));
    }
    public static Specification<BookTransactionHistory> isNotReturned()
    {
        return (root, query, criteriaBuilder) ->  criteriaBuilder.isFalse(root.get("returned"));
    }

    public static Specification<BookTransactionHistory> displayReturnedBooksFor(int userId)
    {
        return isOwnedBy(userId)
                .and(isReturned());
    }
    public static Specification<BookTransactionHistory> isAlreadyBorrowedByUser(Integer bookId, Integer userId)
    {
        return isOwnedByBook(bookId).and(isBorrowedBy(userId)).and(isNotReturnedApproved()).and(isNotReturned());
    }
    public static Specification<BookTransactionHistory> isAlreadyBorrowed(Integer bookId)
    {
        return isOwnedByBook(bookId).and(isNotReturnedApproved());
    }
    public static Specification<BookTransactionHistory> isAlreadyReturnedNotApproved(Integer bookId,Integer userId)
    {
        return isOwnedByBook(bookId).and(isOwnedBy(userId)).and(isReturned()).and(isNotReturnedApproved());
    }

}
