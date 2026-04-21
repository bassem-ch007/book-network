package com.bassem.bsn.waitinglist;

import com.bassem.bsn.book.Book;
import com.bassem.bsn.common.BaseEntity;
import com.bassem.bsn.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class WaitingList extends BaseEntity {
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user-id")
    private User user;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "book-id")
    private Book book;
    @Column
    private boolean active;
}
