package com.edu.ulab.app.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.webjars.NotFoundException;
import javax.validation.ValidationException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {
//    Entity
    @ExceptionHandler({NotFoundException.class})
    protected ResponseEntity<Object> handleEntityNotFoundEx(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(basicActions(request, "Entity not found ex", ex.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> handleHttpMessageNotReadable(Exception ex, WebRequest request) {
        return new ResponseEntity<>(basicActions(request, "Malformed JSON Request", ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, WebRequest request) {
        String msg = String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
                ex.getPropertyName(), ex.getValue(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName());
        return new ResponseEntity<>(basicActions(request, msg, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return new ResponseEntity<>(basicActions(request, "Method Argument Not Valid", ex.getMessage() + errors),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    ResponseEntity<?> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {
        return new ResponseEntity<>(basicActions(request, "Http request method not supported", ex.getMessage()),
                HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    ResponseEntity<?> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException ex, WebRequest request) {
        return new ResponseEntity<>(basicActions(request, "Http media type not supported", ex.getMessage()),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler({AccessDeniedException.class})
    ResponseEntity<?> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        return new ResponseEntity<>(basicActions(request, "Access denied for user", ex.getMessage()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    ResponseEntity<?> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, WebRequest request) {
        return new ResponseEntity<>(basicActions(request, "Missing servlet request parameter", ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NoHandlerFoundException.class})
    ResponseEntity<?> handleNoHandlerFoundException(NoHandlerFoundException ex, WebRequest request) {
        return new ResponseEntity<>(basicActions(request, "Method or url not found", ex.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ValidationException.class})
    ResponseEntity<?> handleValidationException(ValidationException ex, WebRequest request) {
        return new ResponseEntity<>(basicActions(request, "ValidationException", ex.getMessage()),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    //Обработчик по умолчанию
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(basicActions(request, "some exception", ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private /*ApiException*/ MyNotFoundException basicActions(WebRequest request, String message, String debugMessage) {
        log.info("Request URL : {}, Exception : {}",
                ((ServletWebRequest)request).getRequest().getRequestURI(), debugMessage);
        return new /*ApiException*/ MyNotFoundException(message + debugMessage);
    }
}
