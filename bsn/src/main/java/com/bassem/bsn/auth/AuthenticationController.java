package com.bassem.bsn.auth;

import com.bassem.bsn.common.CommonResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
@Tag(name = "authentication")
public class AuthenticationController {
    //private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<CommonResponse<String>> registerUser(@Valid @RequestBody RegistrationRequest request) throws MessagingException {
        return ResponseEntity.accepted().body(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping("/activate-account")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<CommonResponse<String>> confirm(@RequestParam String token) throws MessagingException {
        authenticationService.activateAccount(token);
        CommonResponse<String> response = CommonResponse.<String>builder()
                .data("Account activated successfully.")
                .build();

        return ResponseEntity.accepted().body(response);
    }
}
