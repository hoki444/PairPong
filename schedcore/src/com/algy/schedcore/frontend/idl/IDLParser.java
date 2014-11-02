package com.algy.schedcore.frontend.idl;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.algy.schedcore.frontend.idl.Token.TokType;

/*
 * Author: Alchan Kim
 * Decent-recursive parser for item/scene definition language
 */


public class IDLParser {
    private IDLScanner scanner;

    @SuppressWarnings("serial")
    private static class ParseJumpException extends RuntimeException {
        public String errMsg;
        public Token errToken;
        public ParseJumpException(String errMsg, Token errToken) {
            super(errMsg);
            this.errMsg = errMsg;
            this.errToken = errToken;
        }
    }
    public IDLParser (IDLScanner scanner) {
        this.scanner = scanner;
    }
    
    public IDLParser (String source) {
        this.scanner = new IDLScanner(source);
    }
    

    public final void parseSceneLang () {
        try {
            sceneModule();
        } catch (ParseJumpException e) {
            throw new IDLParsingError(e.errMsg + " " + e.errToken.locinfo, e.errToken.locinfo);
        }
    }
    
    public final void parseItemLang () {
        try {
            itemDefModule();
        } catch (ParseJumpException e) {
            throw new IDLParsingError(e.errMsg, e.errToken.locinfo);
        }
    }

    public final TokenLocInfo getCurrentScannerLoc() {
        return scanner.getCurrentLocInfo();
    }
    
    protected String usedPackageName = "";
    protected String usedDirName = "";
    private void actionUsingPackage(String packageName) {
        this.usedPackageName = packageName;
    }


    private void actionUsingDir(String dirname) {
        this.usedDirName = dirname;
            
    }

    protected void actionDefItem(String assetName,
            ArrayList<CompDescriptor> creationList) {
    }
    
    protected void actionUseItem(String assetName, ArrayList<CompDescriptor> modificationList) { }
    protected void actionUseServer(String assetName,
            Map<String, IDLValue> creationDict,
            Map<String, IDLValue> modificationDict) {
    }
            

    protected void actionCreateItem(ArrayList<CompDescriptor> creationList) {
    }

    private Token expect (Token.TokType ... toks) {
        Token result = consume ();
        for (Token.TokType type : toks) {
            if (result.type == type) {
                return result;
            }
        }
        StringBuilder builder = new StringBuilder();
        if (toks.length > 0) {
            builder.append(toks[0]);
            if (toks.length > 1) {
                for (int idx = 1; idx < toks.length - 1; idx++) {
                    builder.append(", ");
                    builder.append(toks[idx]);
                }
                builder.append(" or ");
                builder.append(toks[toks.length - 1]);
            }
        }
        throw new ParseJumpException("expected " + builder + ", got " + result.type, result);
    }
    
    private Token consume () {
        Token result = scanner.pop();
        if (result.type == TokType.tError) {
            throw new ParseJumpException(result.value, result);
        }
        return result;
    }
    
    private Token.TokType peekType () {
        Token peeked = scanner.peek();
        Token.TokType type = peeked.type;
        if (type == TokType.tError) 
            throw new ParseJumpException(peeked.value, peeked);
        return type;
    }
    
    private void sceneModule () {
        MODULE_LOOP: 
        while (true) {
            switch (peekType()) {
            case tUse:
                consume();
                String assetName = assetName();
                expect (TokType.tEndline);
                ArrayList<CompDescriptor> modificationList = compDeclList (TokType.tModify);
                expect (TokType.tEnd);
                
                if (peekType() != TokType.tEOF) {
                    expect (TokType.tEndline);
                }
                actionUseItem (assetName, modificationList);
                break;
            case tUseserver:
                consume ();
                String serverName = assetName();
                Map<String, IDLValue> modificationDict = null, creationDict = null;
                expect (TokType.tEndline);

                boolean bothNotSpecified = true;
                if (peekType() == TokType.tCreate) {
                    consume ();
                    creationDict = dictExpr();
                    expect (TokType.tEndline);
                    bothNotSpecified = false;
                }
                if (peekType() == TokType.tModify) {
                    consume ();
                    modificationDict = dictExpr();
                    expect (TokType.tEndline);
                    bothNotSpecified  = false;
                }

                if (bothNotSpecified) {
                    Token errTok = consume ();
                    throw new ParseJumpException("At least one declaration among 'create' or 'modify' " + 
                                                 "should be specified.",
                                                 errTok);
                }
                expect (TokType.tEnd);
                if (peekType() != TokType.tEOF) {
                    expect (TokType.tEndline);
                }
                actionUseServer(serverName, creationDict, modificationDict);
                break;
            case tCreate:
                consume();
                expect (TokType.tEndline);
                ArrayList<CompDescriptor> creationList = compDeclList (null) ;
                expect (TokType.tEnd);
                if (peekType() != TokType.tEOF) {
                    expect (TokType.tEndline);
                }
                actionCreateItem (creationList);
                break;
            case tUsing:
                using ();
                break;
            case tEndline:
                consume();
                break;
            case tEOF:
                break MODULE_LOOP;
            default:
                expect (TokType.tUse, TokType.tUseserver,
                        TokType.tCreate, TokType.tUsing,
                        TokType.tEndline, TokType.tEOF);
                break;
            }
        }
    }

    private void itemDefModule () {
        MODULE_LOOP: 
        while (true) {
            switch (peekType()) {
                case tDef:
                    consume();
                    String assetName = assetName ();
                    expect (TokType.tEndline);
                    ArrayList<CompDescriptor> creationList = compDeclList (null) ;
                    expect(TokType.tEnd);

                    if (peekType() != TokType.tEOF) {
                        expect (TokType.tEndline);
                    }
                    actionDefItem (assetName, creationList);
                    break;
                case tUsing:
                    using ();
                    break;
                case tEndline:
                    consume();
                    break;
                case tEOF:
                    break MODULE_LOOP;
                default:
                    expect (TokType.tDef, TokType.tUsing, TokType.tEndline, TokType.tEOF);
                    break;
            }
        }
    }
    
    private void using () {
        expect (TokType.tUsing);
        switch (expect(TokType.tDirectory, TokType.tPackage).type) {
        case tDirectory:
            String dirname;
            if (peekType() != TokType.tEndline &&
                peekType() != TokType.tEOF) {
                dirname = assetName();
            } else
                dirname = "";
            if (peekType() != TokType.tEOF) {
                expect (TokType.tEndline);
            }
            actionUsingDir (dirname);
            break;

        case tPackage:
            String packageName;
            if (peekType() != TokType.tEndline &&
                peekType() != TokType.tEOF) {
                packageName = dottedName ();
            } else 
                packageName = "";
            
            if (peekType() != TokType.tEOF) {
                expect (TokType.tEndline);
            }
            actionUsingPackage (packageName);
            break;
        default:
            // NON-REACHABLE
            break;
        }
    }

    private String slashName () {
        StringBuilder builder = new StringBuilder();
        Token tok = expect (TokType.tName);
        builder.append(tok.value);
        while (peekType() == TokType.tSlash) {
            consume();
            builder.append("/");
            builder.append(expect(TokType.tName).value);
        }
        
        return builder.toString();
    }

    private String assetName () {
        String result = slashName ();
        if ("".equals(usedDirName)) {
            return result;
        } else
            return usedDirName + "/" + result;
    }

    private String dottedName () {
        StringBuilder builder = new StringBuilder();
        Token tok = expect (TokType.tName);
        builder.append(tok.value);
        while (peekType() == TokType.tDot) {
            consume();
            builder.append(".");
            builder.append(expect(TokType.tName).value);
        }
        return builder.toString();
    }
    
    private String className () {
        String className;
        String postfix = dottedName ();
        if ("".equals(usedPackageName)) {
            className = postfix;
        } else {
            className = usedPackageName + "." + postfix;
        }
        return className;
    }

    protected static class CompDescriptor {
        public CompDescriptor(String compName, Map<String, IDLValue> dict) {
            this.compName = compName;
            this.dict = dict;
        }
        public String compName;
        public Map<String, IDLValue> dict;
    }
    

    private ArrayList<CompDescriptor> compDeclList (TokType headLabel) {
        /*
         * represent both compCreationList and compModificationList
         */
        ArrayList<CompDescriptor> result = new ArrayList<CompDescriptor>();
        
        while (peekType() != TokType.tEnd) {
            if (headLabel != null)
                expect (headLabel);

            String compName = assetName ();
            Map<String, IDLValue> dict = dictExpr ();
            expect (TokType.tEndline);
            result.add(new CompDescriptor(compName, dict));
        }
        return result;
    }

    private Map<String, IDLValue> dictExpr () {
        expect (TokType.tEndline);
        TreeMap<String, IDLValue> result = new TreeMap<String, IDLValue> ();
        
        while (peekType() != TokType.tEnd) {
            Token nameTok = expect (TokType.tName);
            String key = nameTok.value;
            expect (TokType.tColon);
            IDLValue val = value ();
            expect (TokType.tEndline);
            
            result.put(key, val);
        }
        expect(TokType.tEnd);
        return result;

    }
    
    private IDLValue value () {
        IDLValue result = null;
        switch (peekType()) {
        case tInteger:
            result = IDLValue.createInteger(Integer.parseInt(consume().value));
            break;
        case tFloat:
            result = IDLValue.createFloat(Float.parseFloat(consume().value));
            break;
        case tString:
            String val = consume().value;
            while (peekType() == TokType.tString)
                val += consume().value;
            
            result = IDLValue.createString(val);
            break;
        case tName:
            result = IDLValue.createClassName(className ());
            break;
        case tEndline:
            result = IDLValue.createDict(dictExpr ());
            break;
        case tTrueFalse:
            if ("true".equals(consume().value))
                result = IDLValue.createBoolean(true);
            else
                result = IDLValue.createBoolean(false);
            break;
        case tNull:
            consume();
            result = IDLValue.createNull();
            break;
        default:
            expect(TokType.tInteger, 
                   TokType.tFloat,
                   TokType.tString, 
                   TokType.tName,
                   TokType.tEndline,
                   TokType.tTrueFalse,
                   TokType.tNull);
            break;
        }
        return result;
    }
}