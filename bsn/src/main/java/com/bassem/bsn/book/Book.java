package com.bassem.bsn.book;

import com.bassem.bsn.common.BaseEntity;
import com.bassem.bsn.feedback.FeedBack;
import com.bassem.bsn.history.BookTransactionHistory;
import com.bassem.bsn.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Book extends BaseEntity {
    private String title;
    private String author;
    //(International Standard Book Number)
    // is a unique identifier for a specific edition of a book — like a fingerprint for published works.
    private String isbn;
    private String synopsis;
    //this String is the file path of our uploaded file/picture
    private String bookCover;
    private boolean archived;
    private boolean shareable;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "owner-id",nullable = false)
    private User owner;
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<FeedBack> feedBacks;
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BookTransactionHistory> bookTransactionHistories;
    @Transient
    public Double getRate(){
        var rate= (feedBacks == null || feedBacks.isEmpty()) ? 0.0 : this.feedBacks
                .stream()
                .mapToDouble(FeedBack::getScore)
                .average()
                .orElse(0.0);
        return Math.round(rate * 100.0) / 100.0;
    }
}
