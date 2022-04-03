package com.zw.atmservice.controller;

import com.zw.atmservice.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ATMControllerAdvice extends ResponseEntityExceptionHandler {

    public static final String TRACE = "trace";

    @Value("${atm.exception.trace.enable:false}")
    private boolean printTrace;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Validation error. Check 'errors' field for details.");

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(),
                    fieldError.getDefaultMessage());
        }
        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleOtherException(
            RuntimeException exception,
            WebRequest request) {
        log.error("Unknown error occurred", exception);
        return createExceptionResponse(
                exception,
                "Unknown error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<ErrorResponse> handleItemNotFoundException(InvalidPinException exception,
                                                                     WebRequest request) {

        return createExceptionResponse(
                exception,
                exception.getMessage(),
                HttpStatus.NOT_FOUND,
                request);
    }

    @ExceptionHandler(ATMGeneralException.class)
    public ResponseEntity<ErrorResponse> handleATMGeneralException(ATMGeneralException exception,
                                                                   WebRequest request) {

        return createExceptionResponse(
                exception,
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }

    @ExceptionHandler(InactiveAccountException.class)
    public ResponseEntity<ErrorResponse> handleInactiveAccountException(InactiveAccountException exception,
                                                                        WebRequest request) {

        return createExceptionResponse(
                exception,
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }

    @ExceptionHandler(InsufficientAmountException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientAmountException(InsufficientAmountException exception,
                                                                           WebRequest request) {

        return createExceptionResponse(
                exception,
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }

    @ExceptionHandler(InactiveCardException.class)
    public ResponseEntity<ErrorResponse> handleInactiveCardException(InactiveCardException exception,
                                                                     WebRequest request) {

        return createExceptionResponse(
                exception,
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }

    private ResponseEntity<ErrorResponse> createExceptionResponse(Exception exception, HttpStatus httpStatus,
            WebRequest request) {
        return createExceptionResponse(
                exception,
                exception.getMessage(),
                httpStatus,
                request);
    }

    private ResponseEntity<ErrorResponse> createExceptionResponse(Exception exception, String message,
            HttpStatus httpStatus,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                httpStatus.value(),
                message);

        if (printTrace && isTraceOn(request)) {
            errorResponse.setStackTrace(Arrays.stream(exception.getStackTrace())
                    .map(StackTraceElement::toString).collect(Collectors.joining("--->")));
        }
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    private boolean isTraceOn(WebRequest request) {
        String[] value = request.getParameterValues(TRACE);
        return Objects.nonNull(value)
                && value.length > 0
                && value[0].contentEquals("true");
    }
}
