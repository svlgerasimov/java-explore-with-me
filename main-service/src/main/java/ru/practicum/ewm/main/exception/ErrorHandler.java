package ru.practicum.ewm.main.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleNotFoundException(NotFoundException e) {
        log.warn("Object not found", e);
        return ErrorDto.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason("The required object was not found.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // ConstraintViolationException - ошибка валидации параметра запроса @Validated
    // HttpMessageNotReadableException - нет тела запроса, возможно ещё что-то
    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleInvalidRequestException(RuntimeException e) {
        log.warn("Parameter validation error", e);
        return ErrorDto.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Ошибка валидации полей десериализируемого объекта @Valid
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Object validation error", e);
        return ErrorDto.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(e.getAllErrors().stream()
                        .map(objectError -> {
                            String text = objectError.getObjectName() + " : ";
                            if (objectError instanceof FieldError) {
                                text += ((FieldError) objectError).getField() + " : ";
                            }
                            text += objectError.getDefaultMessage();
                            return text;
                        })
                        .collect(Collectors.joining("; ")))
                .errors(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.warn("Db constraint violation", e);
        return ErrorDto.builder()
                .status(HttpStatus.CONFLICT)
                .reason("Integrity constraint has been violated.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleUnexpectedException(Throwable exception) {
        log.warn("Unexpected exception", exception);
        return ErrorDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .reason("Unexpected exception")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
