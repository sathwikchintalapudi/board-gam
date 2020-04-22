package com.games.mancala.controller;

import com.games.mancala.domain.Error;
import com.games.mancala.exceptions.MancalaGameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerController.class);

    /**
     * Handles MancalaGameException and converts into error.
     *
     * @param exception catches generic MancalaGameException with error code and description
     * @return error with code and description
     */
    @ExceptionHandler({MancalaGameException.class})
    public ResponseEntity handleMancalaGameException(MancalaGameException exception) {
        LOGGER.error("Game Exception occurred : {}", exception.getMessage());
        return new ResponseEntity<>(exception.getError(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles Exception and converts into error.
     *
     * @param exception catches generic MancalaGameException with error code and description
     * @return error with code and description
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity handleExceptions(Exception exception) {
        LOGGER.error("Internal Exception occurred", exception);
        return new ResponseEntity<>(formError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Error formError() {
        Error error = new Error();
        error.setErrorDescription("Unexpected technical error occurred");
        error.setErrorCode("1100");
        return error;
    }
}
