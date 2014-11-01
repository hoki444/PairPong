package com.algy.schedcore;

public class KeyError extends SchedcoreRuntimeError {
    /**
     * This class represents an exception object that is thrown 
     * when intended key is not found in some kind of container acting like Map
     * 
     * I don't want use null value to check whether key is valid or not, 
     * because I believe it leads messy coding style. It is derived from RuntimeException
     * so that user need not be annoyed by "the hell of throws declaration chains".
     * 
     * It is quite useful for fetching a component from item, in which string is used to fetch a comp and 
     * some typo should raise KeyError.
     * 
     */

    private static final long serialVersionUID = -9129368228318302079L;

    public KeyError() {
        super("KeyError");
    }

    public KeyError(String key) {
        super("KeyError: " + key);
    }

}
