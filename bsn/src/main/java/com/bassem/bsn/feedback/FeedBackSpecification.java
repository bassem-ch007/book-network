package com.bassem.bsn.feedback;

import org.springframework.data.jpa.domain.Specification;

public class FeedBackSpecification {
    public static Specification<FeedBack> ownedByBookID(Integer bookId) {
        return (root, query, criteriaBuilder) ->  criteriaBuilder.equal(root.get("book").get("id"),bookId);
    }
}
