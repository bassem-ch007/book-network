package com.bassem.bsn.feedback;

import com.bassem.bsn.book.Book;
import com.bassem.bsn.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedBackMapper {
    public FeedBack toFeedBack(FeedBackRequest feedBackRequest, User user, Book book) {
        return FeedBack.builder().
                comment(feedBackRequest.getComment()).
                score(feedBackRequest.getNote()) .
                book(book).
                user(user).
                build();
    }

    public FeedBackResponse toFeedBackResponse(FeedBack feedBack,int userId) {
        return FeedBackResponse.
                builder().
                comment(feedBack.getComment()).
                score(feedBack.getScore()).
                ownFeedback(Objects.equals(feedBack.getCreatedBy(),userId)).
                build();
    }
}
