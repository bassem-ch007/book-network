package com.bassem.bsn.feedback;

import com.bassem.bsn.book.Book;
import com.bassem.bsn.book.BookRepository;
import com.bassem.bsn.common.PageResponse;
import com.bassem.bsn.exception.OperationNotPermittedException;
import com.bassem.bsn.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedBackService {
    private final FeedBackMapper feedBackMapper;
    private final FeedBackRepository feedBackRepository;
    private final BookRepository bookRepository;

    public Integer registerFeedback(FeedBackRequest request, Authentication authenticatedUser) {
        Book book=bookRepository.findById(request.getBookId()).orElseThrow(()->new RuntimeException("Book not found"));
        if (book.isArchived()){throw new OperationNotPermittedException("book is archived");}
        if (!book.isShareable()){throw new OperationNotPermittedException("book is not shareable");}
        User user = (User) authenticatedUser.getPrincipal();
        if(Objects.equals(user.getId(),book.getOwner().getId()))
        {
            throw new OperationNotPermittedException("you cannot add feedback to your own book");
        }
        return feedBackRepository.save(feedBackMapper.toFeedBack(request,user,book)).getId();
    }

    public PageResponse<FeedBackResponse> getFeedbacksByBookID(int bookId,int page,int size, Authentication authenticatedUser) {

        User user=(User)authenticatedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<FeedBack> feedBacks = feedBackRepository.findAll(FeedBackSpecification.ownedByBookID(bookId),pageable);
        List<FeedBackResponse> feedBackResponses=feedBacks.stream()
                .map(f -> feedBackMapper.toFeedBackResponse(f,user.getId()))
                .toList();
        return new PageResponse<>(feedBackResponses,
                feedBacks.getNumber(),
                feedBacks.getSize(),
                feedBacks.getTotalPages(),
                feedBacks.getTotalElements(),
                feedBacks.isFirst(),
                feedBacks.isLast());
    }

}
