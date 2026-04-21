package com.bassem.bsn.book;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookRequest {
    private Integer id;
    @NotEmpty(message = "Title is mandatory")
    private String title;
    @NotEmpty(message = "Author is mandatory")
    private String author;
    @NotEmpty(message = "Isbn is mandatory")
    private String isbn;
    @NotEmpty(message = "Synopsis is mandatory")
    private String synopsis;

    private boolean shareable;
}
