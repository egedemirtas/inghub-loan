package com.inghub.loan.exception.handler;

import com.inghub.loan.dto.ErrorResponseDto;
import com.inghub.loan.exception.LimitNotSufficientException;
import com.inghub.loan.exception.ResourceNotFoundException;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class LoanExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource msgSource;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(request.getDescription(false),
                ex.getFieldErrors().stream().map(err -> err.getField() + ": " + err.getDefaultMessage())
                        .collect(Collectors.joining(", ")), LocalDateTime.now());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ErrorResponseDto handleResourceNotFoundException(Exception ex, WebRequest webRequest) {
        return new ErrorResponseDto(webRequest.getDescription(false), ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(JdbcSQLIntegrityConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ErrorResponseDto handleSQLIntegrityViolationException(Exception ex, WebRequest webRequest) {
        return new ErrorResponseDto(webRequest.getDescription(false), "Record already exists",
                LocalDateTime.now());
    }

    @ExceptionHandler(LimitNotSufficientException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ErrorResponseDto handleLimitNotSufficientException(Exception ex, WebRequest webRequest) {
        return new ErrorResponseDto(webRequest.getDescription(false), ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public final ErrorResponseDto handleUsernameNotFoundException(Exception ex, WebRequest webRequest) {
        return new ErrorResponseDto(webRequest.getDescription(false), ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ErrorResponseDto handleDataIntegrityViolationException(Exception ex, WebRequest webRequest) {
        return new ErrorResponseDto(webRequest.getDescription(false), "Data already exists",
                LocalDateTime.now());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final ErrorResponseDto handleGlobalException(Exception ex, WebRequest webRequest) {
        return new ErrorResponseDto(webRequest.getDescription(false), ex.getMessage(), LocalDateTime.now());
    }
}
