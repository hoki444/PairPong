package com.algy.schedcore.frontend.idl;

public interface IDLInput {
    public char peek();
    public char pop();
    public boolean eof();
    
    public int currentLine ();
    public int currentCol ();
}