package com.bassem.bsn.book;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,Integer> , JpaSpecificationExecutor<Book> {
    //    @Query("""
//            select book
//            from Book book where book.archived = false
//            and book.shareable = true   and book.owner.id != :userId
//                        """)
//    Page<Book> findAllDisplayableBooks(Pageable pageable, Integer userId);
    @Query("""
        SELECT b 
        FROM Book b 
        LEFT JOIN b.feedBacks f
        WHERE b.owner.id <> :userId
          AND b.shareable = true
          AND b.archived = false
          AND LOWER(b.title) LIKE CONCAT('%', :title, '%')
        GROUP BY b.id
        ORDER BY COALESCE(AVG(f.score), 0) DESC
        """)
    List<Book> findTopRatedBooksByTitleStartingWith(int userId, String title, Pageable pageable);

}
