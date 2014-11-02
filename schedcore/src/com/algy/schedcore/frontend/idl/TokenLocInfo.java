package com.algy.schedcore.frontend.idl;

public class TokenLocInfo {
    public int stLine;
    public int edLine;
    public int stCol;
    public int edCol;
    
    @Override
    public String toString () {
        return "[line " + stLine + "-" + edLine + 
               ", col " + stCol + "-" + edCol + "]";
    }
}