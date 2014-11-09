package com.algy.schedcore.frontend.idl.reflection;

public class IDLTemplateMixin {
    protected void invokeKeyError (String key, String msg) {
        throw new IDLReflectionError("Error occured in key '" + key + "': " + msg);
    }
    protected void keyNotModifiableError (String key) {
        throw new IDLReflectionError("key '" + key + "' is not modifiable");
    }
    protected void keyRequiredError (String key) {
        throw new IDLReflectionError("key '" + key + "' is required");
    }
    protected void unexpectedValueError (Object [] expectation, Object got) {
        StringBuilder builder = new StringBuilder();
        if (expectation.length > 0) {
            builder.append(expectation[0]);
            if (expectation.length > 1) {
                for (int idx = 1; idx < expectation.length - 1; idx++) {
                    builder.append(", ");
                    builder.append(expectation[idx]);
                }
                    builder.append(" or ");
                builder.append(expectation[expectation.length - 1]);
            }
        }
        throw new IDLReflectionError(builder + " expected, got " + got);
    }
}
