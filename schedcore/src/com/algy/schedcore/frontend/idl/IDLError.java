package com.algy.schedcore.frontend.idl;

import com.algy.schedcore.SchedcoreRuntimeError;

public class IDLError extends SchedcoreRuntimeError {
    public IDLError() {
        super();
    }

    public IDLError(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public IDLError(String arg0) {
        super(arg0);
    }

    public IDLError(Throwable arg0) {
        super(arg0);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -31005703422300326L;

}
