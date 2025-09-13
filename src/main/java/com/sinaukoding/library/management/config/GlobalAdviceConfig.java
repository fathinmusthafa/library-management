package com.sinaukoding.library.management.config;

import com.sinaukoding.library.management.model.response.BaseResponse;
import com.sun.jdi.request.DuplicateRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalAdviceConfig {

    @ExceptionHandler({RuntimeException.class, Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse<?> handleException(Exception e) {
        return BaseResponse.error("Something bad happen on app server please try again later, contact support for this error", e.getMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleExceptionBadRequest(Exception e) {
        return BaseResponse.badRequest(e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public BaseResponse<?> handleExceptionForbidden(AccessDeniedException e) {
        return BaseResponse.forbiddenAccess("You don't have permission to access this resource.");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleValidationException(MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return BaseResponse.badRequest(errorMessage);
    }

    @ExceptionHandler({DuplicateRequestException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public BaseResponse<?> handleDuplicateResourceException(DuplicateRequestException ex) {
        return BaseResponse.conflict(ex.getMessage());
    }
}
