package com.bassem.bsn.feedback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedBackResponse {
    private Double score;
    private String comment;
    private boolean ownFeedback;
}
