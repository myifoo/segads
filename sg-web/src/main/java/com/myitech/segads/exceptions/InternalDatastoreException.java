package com.myitech.segads.exceptions;

/**
 * Created by A.T on 2018/1/9.
 */
public class InternalDatastoreException extends RuntimeException {
    public enum State {ACTIVE, INACTIVE}

    private State state;

    public InternalDatastoreException(State state) {
        this.state = state;
    }

    public InternalDatastoreException(String message) {
        super(message);
    }

    public InternalDatastoreException(Throwable cause) {
        super(cause);
    }

    public State getState() {
        return state;
    }
}
