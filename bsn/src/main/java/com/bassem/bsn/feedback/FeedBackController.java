package com.bassem.bsn.feedback;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("feedbacks")
@Tag(name = "feedback")
public class FeedBackController {
    private final FeedBackService feedBackService;
    @PostMapping("/register")
    public ResponseEntity<Integer> registerFeedback(
            @RequestBody @Valid FeedBackRequest request, Authentication authenticatedUser){
        return ResponseEntity.ok(feedBackService.registerFeedback(request,authenticatedUser));
    }
    @GetMapping("/feedbacks/{book-id}")
    public ResponseEntity<?> getFeedbacksByBookID(
            @PathVariable(name = "book-id")  Integer bookId,
            @RequestParam(name = "page",defaultValue = "0",required = false)int page,
            @RequestParam(name = "size",defaultValue = "10",required = false)int size,
            Authentication authenticatedUser){
        return ResponseEntity.ok(feedBackService.getFeedbacksByBookID(bookId,page,size,authenticatedUser));
    }
}
