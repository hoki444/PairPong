package com.algy.schedcore;

public class SchedcoreRuntimeError extends RuntimeException {
    public SchedcoreRuntimeError() {
        super();
    }

    public SchedcoreRuntimeError(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public SchedcoreRuntimeError(String arg0) {
        super(arg0);
    }

    public SchedcoreRuntimeError(Throwable arg0) {
        super(arg0);
    }

    private static final long serialVersionUID = -5512675263971185087L;
}
