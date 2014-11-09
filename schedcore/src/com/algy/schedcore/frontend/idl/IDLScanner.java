package com.algy.schedcore.frontend.idl;

import java.util.HashMap;

import com.algy.schedcore.frontend.idl.Token.TokType;


public class IDLScanner {
    private IDLInput input;
    private Token pended = null;

    private boolean endlineScanned = false;
    
    private static HashMap<String, TokType> keywords;
    static {
        keywords = new HashMap<String, TokType>();
        keywords.put("def", TokType.tDef);
        keywords.put("directory", TokType.tDirectory);
        keywords.put("package", TokType.tPackage);
        keywords.put("using", TokType.tUsing);
        keywords.put("use", TokType.tUse);
        keywords.put("create", TokType.tCreate);
        keywords.put("modify", TokType.tModify);
        keywords.put("end", TokType.tEnd);
        keywords.put("null", TokType.tNull);
        keywords.put("true", TokType.tTrueFalse);
        keywords.put("false", TokType.tTrueFalse);
        keywords.put("useserver", TokType.tUseserver);
        keywords.put("modifyserver", TokType.tModifyserver);
        keywords.put("as", TokType.tAs);
    }

    public IDLScanner (final String source) {
        input = new IDLInput() {
            int idx = 0;
            int len = source.length();
            int line = 1;
            int col = 1;

            @Override
            public char pop() {
                char result = peek();
                if (result == '\n') {
                    line++;
                    col = 1;
                } else if (result != 0) {
                    col++;
                } 
                if (result != 0)
                    idx++;
                return result;
            }
            
            @Override
            public char peek() {
                if (idx >= len)
                    return 0;
                else
                    return source.charAt(idx);
            }
            
            @Override
            public boolean eof() {
                return idx >= len;
            }

            @Override
            public int currentLine() {
                return line;
            }

            @Override
            public int currentCol() {
                return col;
            }
        };
    }

    public Token pop () {
        Token result;
        if (pended != null) {
            result = pended;
            pended = null;
        } else {
            result = lex ();
        }
        return result;
    }

    public Token peek () {
        if (pended != null) {
            return pended;
        } else {
            pended = lex ();
            return pended;
        }
    }

    private Token lex () {
        int stLine, edLine, stCol, edCol;

        Token.TokType type = null;
        boolean signFlag = false;
        StringBuilder builder = new StringBuilder();
        while (true) {
            stLine = input.currentLine();
            stCol = input.currentCol();
            if (input.eof()) {
                return new Token(Token.TokType.tEOF,
                                 "",
                                 stLine, 
                                 stCol, 
                                 stLine, 
                                 stCol);
            }
            char c = input.peek();

            stLine = input.currentLine();
            stCol = input.currentCol();
            
            if (c == '-') {
                builder.append(input.pop());
                stLine = input.currentLine();
                stCol = input.currentCol();
                signFlag = true;

                c = input.peek();
            }

            if (isNumber (c)) {
                builder.append(input.pop());
                while (isNumber (input.peek()))
                    builder.append(input.pop());
                if (input.peek() == '.') {
                    builder.append(input.pop());
                    while (isNumber (input.peek()))
                        builder.append(input.pop());
                    type = TokType.tFloat;
                } else {
                    type = TokType.tInteger;
                }
            } else if (c == '.') {
                builder.append(input.pop());
                if (isNumber(input.peek())) {
                    builder.append(input.pop());
                    while (isNumber (input.peek()))
                        builder.append(input.pop());
                    type = TokType.tFloat;
                } else {
                    type = TokType.tDot;
                }
            } else if (isFirstLetter (c)) {
                builder.append(input.pop());
                while (isLetter (input.peek()))
                    builder.append(input.pop());
                String value = builder.toString();
                if (keywords.containsKey(value))
                    type = keywords.get(value);
                else
                    type = TokType.tName;
            } else if (c == ' ' || c == '\t') {
                input.pop();
                continue;
            } else if (c == '#') {
                input.pop();
                if (input.peek() == '|') {
                    // multi-line comment #| abcdef |#
                    input.pop();
                    while (true)  {
                        while (!input.eof() && input.peek() != '|') input.pop();
                        if (input.eof())
                            return errorToken("EOF encountered while reading multiline comment block");
                        else if (input.pop() == '|') {
                            if (!input.eof() && input.pop() == '#')
                                break;
                            continue;
                        }
                    }
                } else 
                    while (!input.eof() && input.peek() != '\n') input.pop();
                continue;
            } else if (c == ':') {
                builder.append(input.pop());
                type = TokType.tColon;
            } else if (c == '\n') {
                if (endlineScanned) {
                    input.pop();
                    continue;
                } else {
                    builder.append(input.pop());
                    type = TokType.tEndline;
                }
            } else if (c == '/') {
                builder.append(input.pop());
                type = TokType.tSlash;
            } else if (c == '\'' || c == '\"') {
                char qmark = input.pop();
                while (!input.eof() && 
                       input.peek() != '\n' &&
                       input.peek() != qmark) {
                    builder.append(input.pop());
                }
                if (input.eof()) {
                    return errorToken("Encountered EOF while reading string literal");
                } else if (input.peek() == '\n') {
                    return errorToken("Encountered end of line while reading string literal");
                } else {
                    input.pop();
                }
                type = TokType.tString;
            } else if (c == '>') {
                builder.append(input.pop());
                if (input.peek() == '>') {
                    builder.append(input.pop());
                    type = TokType.tRShift;
                } else {
                    type = TokType.tGT;
                }
            } else {
                return errorToken("Invalid character. Got '" + input.peek() + "'");
            }
            edLine = input.currentLine();
            edCol = input.currentCol() - 1;
            break;
        }
        
        if (signFlag && type != TokType.tInteger && type != TokType.tFloat) {
            return errorToken("Number expected after minus sign");
        }

        endlineScanned = (type == TokType.tEndline);
        return new Token(type, builder.toString(),
                         stLine, stCol,
                         edLine, edCol);
    }
    
    public TokenLocInfo getCurrentLocInfo () {
        int line = input.currentLine();
        int col = input.currentCol();
        TokenLocInfo locInfo = new TokenLocInfo();
        locInfo.stLine = locInfo.edLine = line;
        locInfo.stCol = locInfo.edCol = col;
        return locInfo;
    }
    private Token errorToken (String val) {
        int line = input.currentLine();
        int col = input.currentCol();
        return new Token(TokType.tError, 
                         val,
                         line, col, line, col);
    }
    
    
    public static boolean isNumber (char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isLetter (char c) {
        return c >= 'a' && c <= 'z' ||
               c >= 'A' && c <= 'Z' ||
               c >= '0' && c <= '9' ||
               c == '_';
    }

    public static boolean isFirstLetter (char c) {
        return c >= 'a' && c <= 'z' ||
               c >= 'A' && c <= 'Z' ||
               c == '_';
    }
}