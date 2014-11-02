package com.algy.schedcore.frontend.idl;

public class Token {
    public static enum TokType {
        tEndline, // \n
        tSlash, // /
        tDot, // .
        tColon, // :

        tPackage, // package
        tDirectory, // directory
        tUsing, // using
        tUse, // use
        tUseserver, // useserver
        tCreate, // create
        tModify, // modify
        tDef, // def
        tEnd, // end
        tNull, // null
        tName, // <name>
        
        tTrueFalse, // true | false
        tFloat, // 1., 1.2, .2
        tInteger, // 123
        tString, // "ff"
        tEOF,
        tError
    };
    public TokType type;
    public String value;
    public TokenLocInfo locinfo;
    
    public Token (TokType type, String value, 
                  int stLine, int stCol,
                  int edLine, int edCol) {
        this.type = type;
        this.value = value;
        this.locinfo = new TokenLocInfo();
        locinfo.stLine = stLine;
        locinfo.stCol = stCol;
        locinfo.edLine = edLine;
        locinfo.edCol = edCol;
    }
    
    @Override
    public String toString() {
        String v = value.equals("\n")? "\\n" : value;
        return type.toString() + " (" +  v + ") [line " + 
               locinfo.stLine + "-"  + locinfo.edLine + 
               ", col " + locinfo.stCol + "-" + locinfo.edCol + "]";
    }
}