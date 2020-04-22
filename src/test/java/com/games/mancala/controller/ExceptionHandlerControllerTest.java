package com.games.mancala.controller;

import com.games.mancala.domain.Error;
import com.games.mancala.exceptions.MancalaGameException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;

public class ExceptionHandlerControllerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ExceptionHandlerController exceptionHandlerController;

    @Before
    public void initiateTest() {
        exceptionHandlerController = new ExceptionHandlerController();
    }


    @Test
    public void handleMancalaGameException() {
        ResponseEntity resp = exceptionHandlerController.handleMancalaGameException(formMancalaGameException());
        assertEquals("Exception message is not as expected", ((Error) resp.getBody()).getErrorDescription(), "Exception-Test");
        assertEquals("Exception code is not as expected", ((Error) resp.getBody()).getErrorCode(), "333");
        assertEquals("Status code is as expected", resp.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void handleExceptions() {
        ResponseEntity resp = exceptionHandlerController.handleExceptions(new NullPointerException());
        assertEquals("Exception message is not as expected", ((Error) resp.getBody()).getErrorDescription(), "Unexpected technical error occurred");
        assertEquals("Exception code is not as expected", ((Error) resp.getBody()).getErrorCode(), "1100");
        assertEquals("Status code is as expected", resp.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private MancalaGameException formMancalaGameException() {
        return new MancalaGameException("Exception-Test", "333");
    }
}
