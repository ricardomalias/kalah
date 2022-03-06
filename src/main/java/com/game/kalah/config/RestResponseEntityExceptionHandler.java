package com.game.kalah.config;

import com.game.kalah.dto.ResponseError;
import com.game.kalah.util.MessageUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageUtil messageUtil;

    public RestResponseEntityExceptionHandler(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<ResponseError> handleConstraintViolationExceptions(
            ConstraintViolationException constraintViolationException) {

        String[] split = constraintViolationException.getMessage().split(":");
        String[] split1 = split[1].split(",");
        String messageCode = split1[0].trim();

        ResponseError responseError = new ResponseError(
                messageCode,
                messageUtil.getMessage(messageCode)
        );

        return ResponseEntity.badRequest().body(responseError);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public final ResponseEntity<ResponseError> handleConstraintViolationExceptions(
            ResponseStatusException responseStatusException) {

        String messageCode = responseStatusException.getReason();

        ResponseError responseError = new ResponseError(
                messageCode,
                messageUtil.getMessage(messageCode)
        );

        return ResponseEntity.badRequest().body(responseError);
    }

    @Override
    protected final ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        Optional<ResponseError> responseError = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(item -> new ResponseError(
                        item.getDefaultMessage(),
                        messageUtil.getMessage(item.getDefaultMessage()))
                )
                .findFirst();

        return ResponseEntity.badRequest().body(responseError);
    }
}
