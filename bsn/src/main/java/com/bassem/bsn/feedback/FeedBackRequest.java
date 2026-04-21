package com.bassem.bsn.feedback;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedBackRequest {
    @Positive(message = "note must be positive")
    @Min(value =0 ,message = "have to be at least 0")
    @Max(value = 5,message = "have to be at most 5")
    private Double note;
    private String comment ;
    @NotNull(message = "bookId is mandatory")
    private Integer bookId;
}




