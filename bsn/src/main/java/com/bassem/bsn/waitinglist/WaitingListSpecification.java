package com.bassem.bsn.waitinglist;

import org.springframework.data.jpa.domain.Specification;

public class WaitingListSpecification {
    public static Specification<WaitingList> isForUser(Integer userId) {
        return (root, query, criteriaBuilder) ->  criteriaBuilder.equal(root.get("user").get("id"), userId);
    }
    private static Specification<WaitingList> isForBook(Integer bookId) {
        return (root, query, criteriaBuilder) ->criteriaBuilder.equal(root.get("book").get("id"), bookId);
    }
    public static Specification<WaitingList> isWished(Integer bookId, Integer userId) {
        return Specification.where(isForUser(userId)).and(isForBook(bookId));
    }
    public static Specification<WaitingList> isActive() {
        return  (root, query, criteriaBuilder) ->criteriaBuilder.isTrue(root.get("active"));
    }
    public static Specification<WaitingList> displayableForUser(Integer userId) {
        return Specification.where(isForUser(userId)).and(isActive());
    }
}
