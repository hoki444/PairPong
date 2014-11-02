package com.algy.schedcore.frontend.idl;

public class IDLParsingError {
    public IDLParsingError(String errorMsg, TokenLocInfo errLoc) {
        super();
        this.errorMsg = errorMsg;
        this.errLoc = errLoc;
    }

    public String errorMsg;
    public TokenLocInfo errLoc;
}
