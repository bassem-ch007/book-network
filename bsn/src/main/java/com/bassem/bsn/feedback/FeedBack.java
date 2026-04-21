package com.bassem.bsn.feedback;

import com.bassem.bsn.book.Book;
import com.bassem.bsn.common.BaseEntity;
import com.bassem.bsn.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class FeedBack extends BaseEntity {
    private Double score; //1-5 stars
    private String comment;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "book-id")
    private Book book;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user-id")
    private User user;
}
