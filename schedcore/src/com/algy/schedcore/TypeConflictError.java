package com.algy.schedcore;

public class TypeConflictError extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 3822436664660148234L;

    public TypeConflictError() {
        super();
    }

    public TypeConflictError(String message) {
        super(message);
    }

}
