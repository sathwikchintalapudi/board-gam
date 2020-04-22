package com.games.mancala.exceptions;

import com.games.mancala.domain.Error;

public class MancalaGameException extends RuntimeException {

    Error error = new Error();

    public MancalaGameException(String message, String errorCode) {
        super(message);
        error.setErrorCode(errorCode);
        error.setErrorDescription(message);
    }


    public Error getError() {
        return this.error;
    }

}
