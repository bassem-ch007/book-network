package com.bassem.bsn.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {
    @Id
    @GeneratedValue
    private Integer id;
    @Column
    private String token;
    @Column
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime expiresAt;
    @Column
    private LocalDateTime validatedAt;
    @ManyToOne
    @JoinColumn(name = "user-id")
    private User user;
}
