package com.bassem.bsn.handler;

import com.bassem.bsn.exception.OperationNotPermittedException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.DigestException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp){
        log.error(exp.getMessage(),exp);
        return  ResponseEntity.status(HttpStatus.LOCKED).body(
                ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCode.ACCOUNT_LOCKED.getCode())
                        .businessErrorDescription(BusinessErrorCode.ACCOUNT_LOCKED.getDescription())
                        .error(exp.getMessage())
                        .build()
        );
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> handleExpiredJwtException(ExpiredJwtException exp) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCode.TOKEN_EXPIRED.getCode())
                        .businessErrorDescription(BusinessErrorCode.TOKEN_EXPIRED.getDescription())
                        .error(exp.getMessage())
                        .build()
                );
    }
    //If the offset or length you provide in digest(byte[] buf, int offset, int len) is invalid.
    //byte[] smallBuffer = new byte[10]; // too small for SHA-256 result (needs 32 bytes)
    @ExceptionHandler(value = DigestException.class)
    public ResponseEntity<ExceptionResponse> handleException(DigestException exp){
        log.error(exp.getMessage(),exp);
        exp.printStackTrace();
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCode.ACCOUNT_DISABLED.getCode())
                        .businessErrorDescription(BusinessErrorCode.ACCOUNT_DISABLED.getDescription())
                        .error(exp.getMessage())
                        .build()
        );
    }
    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp){
        log.error(exp.getMessage(),exp);
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCode.BAD_CREDENTIALS.getCode())
                        .businessErrorDescription(BusinessErrorCode.BAD_CREDENTIALS.getDescription())
                        .error(BusinessErrorCode.BAD_CREDENTIALS.getDescription())
                        .build()
        );
    }
    @ExceptionHandler(value = MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exp){
        log.error(exp.getMessage(),exp);
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ExceptionResponse.builder().error(exp.getMessage()).build()
        );
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Set<String> validationErrors = new HashSet<>();
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(err -> {
            validationErrors.add(err.getDefaultMessage());
            fieldErrors.put(err.getField(), err.getDefaultMessage());
        });

        ExceptionResponse response = ExceptionResponse.builder()
                .validationErrors(validationErrors)
                .errors(fieldErrors)
                .businessErrorCode(100) // optional code
                .businessErrorDescription("Validation failed")
                .error("Bad Request")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
    @ExceptionHandler(value = OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exp){
        log.error(exp.getMessage(),exp);
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ExceptionResponse.builder().error(exp.getMessage()).build()
        );
    }
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp){
        //todo log the exception
        log.error(exp.getMessage(),exp);
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ExceptionResponse.builder().businessErrorDescription("Internal error, contact the admin")
                        .error(exp.getMessage()).build()
        );
    }
}
