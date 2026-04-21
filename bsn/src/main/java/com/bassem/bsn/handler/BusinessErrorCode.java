package com.bassem.bsn.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessErrorCode {
    NO_CODE(0, HttpStatus.NOT_IMPLEMENTED,"no code"),
    ACCOUNT_LOCKED(302,HttpStatus.LOCKED,"account locked"),
    ACCOUNT_DISABLED(303,HttpStatus.FORBIDDEN,"account disabled"),
    BAD_CREDENTIALS(304,HttpStatus.BAD_REQUEST,"Loging and / or password is incorrect"),
    INCORRECT_PASSWORD(301,HttpStatus.UNAUTHORIZED,"incorrect password"),
    NEW_PASSWORD_DOES_NOT_MATCH(302,HttpStatus.FORBIDDEN,"new password does not match"),
    TOKEN_EXPIRED(400,HttpStatus.UNAUTHORIZED,"token expired"),;

    private final int code;
    private final String description;
    private final HttpStatus httpStatusCode;
    BusinessErrorCode(int code,  HttpStatus httpStatusCode,String description){
        this.code = code;
        this.httpStatusCode = httpStatusCode;
        this.description = description;
    }
}
