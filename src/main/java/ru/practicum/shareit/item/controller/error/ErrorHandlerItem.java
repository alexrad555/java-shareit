package ru.practicum.shareit.item.controller.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exception.DuplicateException;
import ru.practicum.shareit.item.exception.ValidationException;
import ru.practicum.shareit.item.exception.DataNotFoundException;
import ru.practicum.shareit.item.exception.InternalServerErrorException;

@RestControllerAdvice
@Slf4j
public class ErrorHandlerItem {

    @ExceptionHandler()
    public ErrorResponse handlerDataNotFoundException(final DataNotFoundException exception) {
        log.error("Данные не найдены {}", exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(DuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlerDuplicateException(final DuplicateException exception) {
        log.error("содержит дубликат {}", exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerValidationException(final ValidationException exception) {
        log.error("Ошибка входящих данных {}", exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerInternalServerErrorException(final InternalServerErrorException exception) {
        log.error("Ошибка сервера {}", exception.getMessage());
        return new ErrorResponse(exception.getMessage());
    }
}
