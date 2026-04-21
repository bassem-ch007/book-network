package com.bassem.bsn.waitinglist;

import com.bassem.bsn.book.BookResponse;
import com.bassem.bsn.file.FileUtils;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class WaitingListMapper {
    public BookResponse toWaitingList(WaitingList waitingList){
        return BookResponse.builder()
                .id(waitingList.getBook().getId())
                .title(waitingList.getBook().getTitle())
                .author(waitingList.getBook().getAuthor())
                .isbn(waitingList.getBook().getIsbn())
                .synopsis(waitingList.getBook().getSynopsis())
                .shareable(waitingList.getBook().isShareable())
                .owner(waitingList.getBook().getOwner().fullname())
                .rate(waitingList.getBook().getRate())
                .archived(waitingList.getBook().isArchived())
                .cover(FileUtils.getFileFromLocation(waitingList.getBook().getBookCover()))
                .build();
    }
}
