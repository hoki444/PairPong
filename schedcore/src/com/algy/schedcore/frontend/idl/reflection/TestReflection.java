package com.algy.schedcore.frontend.idl.reflection;

import com.algy.schedcore.frontend.idl.IDLParser;
import com.algy.schedcore.frontend.idl.IDLValue;

class ANG {
    Class<?> clazz;
}

public class TestReflection {
    public static void main (String [] args) {
        String source =
"\n" +
"  clazz: java.util.Map\n" +
"end";
        IDLParser parser = new IDLParser(source);
        IDLValue dict = parser.parseIDLValue();

        ANG v = (ANG) IDLReflector.parse(dict, ANG.class, true);
        System.out.println(v.clazz);
    }
}