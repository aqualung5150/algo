package com.seungjoon.algo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Iterator;

import static com.seungjoon.algo.exception.ExceptionCode.INTERNAL_SEVER_ERROR;
import static com.seungjoon.algo.exception.ExceptionCode.BAD_REQUEST;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.internalServerError().body(new ExceptionResponse(INTERNAL_SEVER_ERROR.getCode(), INTERNAL_SEVER_ERROR.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException e) {

//        log.warn(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ExceptionResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionResponse> handleUnauthorizedException(UnauthorizedException e) {
//        log.warn(e.getMessage(), e);
        return new ResponseEntity<>(new ExceptionResponse(e.getCode(), e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNoResourceFoundException(NoResourceFoundException e) {
//        log.warn(e.getMessage(), e);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        StringBuilder sb = new StringBuilder();

        Iterator<FieldError> iterator = e.getBindingResult().getFieldErrors().iterator();
        if (iterator.hasNext()) {
            sb.append("[").append(iterator.next().getDefaultMessage()).append("]");
            while (iterator.hasNext()) {
                sb.append(", ").append("[").append(iterator.next().getDefaultMessage()).append("]");
            }
        }

        String message = sb.isEmpty() ? BAD_REQUEST.getMessage() : sb.toString();

        return ResponseEntity.badRequest().body(new ExceptionResponse(BAD_REQUEST.getCode(), message));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(BAD_REQUEST.getCode(), BAD_REQUEST.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(BAD_REQUEST.getCode(), BAD_REQUEST.getMessage()));
    }

    @ExceptionHandler(ExistingAuthTypeException.class)
    public ResponseEntity<ExceptionResponse> handleExistingAuthTypeException(ExistingAuthTypeException e) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(e.getCode(), e.getMessage()));
    }
}
