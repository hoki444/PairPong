package com.algy.schedcore.frontend.idl;

public class IDLParsingError extends IDLError {
    /**
     * 
     */
    private static final long serialVersionUID = -3452652983278128677L;
    public IDLParsingError(String errorMsg, TokenLocInfo errLoc) {
        super(errorMsg);
        this.errorMsg = errorMsg;
        this.errLoc = errLoc;
    }

    public String errorMsg;
    public TokenLocInfo errLoc;
}
