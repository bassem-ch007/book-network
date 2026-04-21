package com.bassem.bsn.book;

import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {
    public static Specification<Book> isOwnedBy(int userId)
    {
        return((root, query, criteriaBuilder) ->  criteriaBuilder.equal(root.get("owner").get("id"),userId));
    }
    public static Specification<Book> isNotOwnedBy(int userId)
    {
        return((root, query, criteriaBuilder) ->  criteriaBuilder.notEqual(root.get("owner").get("id"),userId));
    }

    public static Specification<Book> isShareable()
    {
        return((root, query, criteriaBuilder) ->criteriaBuilder.isTrue(root.get("shareable")));
    }
    public static Specification<Book> isNotShareable()
    {
        return((root, query, criteriaBuilder) ->criteriaBuilder.isFalse(root.get("shareable")));
    }

    public static Specification<Book> isArchived()
    {
        return((root,query,criteriaBuilder) ->criteriaBuilder.isTrue(root.get("archived")));
    }
    public static Specification<Book> isNotArchived()
    {
        return((root,query,criteriaBuilder) ->criteriaBuilder.isFalse(root.get("archived")));
    }

    // show all the books except owned
    public static Specification<Book> displayableFor(int userId)
    {
        return isNotOwnedBy(userId)
                .and(isShareable())
                .and(isNotArchived());
    }
    public static Specification<Book> titleStartsWith(String title) {
        return (root, q, cb) -> cb.like(cb.lower(root.get("title")), "%" +title.toLowerCase() + "%");
    }
    public static Specification<Book> searchBooks(int userId,String title)
    {
        return isNotOwnedBy(userId)
                .and(isShareable())
                .and(isNotArchived())
                .and(titleStartsWith(title));
    }

}
