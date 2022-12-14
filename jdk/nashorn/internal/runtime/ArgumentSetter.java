package jdk.nashorn.internal.runtime;

import jdk.nashorn.internal.codegen.CompilerConstants;

public final class ArgumentSetter
{
    public static final CompilerConstants.Call SET_ARGUMENT;
    public static final CompilerConstants.Call SET_ARRAY_ELEMENT;
    
    private ArgumentSetter() {
    }
    
    public static void setArgument(final Object value, final ScriptObject arguments, final int key) {
        arguments.setArgument(key, value);
    }
    
    public static void setArrayElement(final Object value, final Object[] arguments, final int key) {
        arguments[key] = value;
    }
    
    static {
        SET_ARGUMENT = CompilerConstants.staticCallNoLookup(ArgumentSetter.class, "setArgument", Void.TYPE, Object.class, ScriptObject.class, Integer.TYPE);
        SET_ARRAY_ELEMENT = CompilerConstants.staticCallNoLookup(ArgumentSetter.class, "setArrayElement", Void.TYPE, Object.class, Object[].class, Integer.TYPE);
    }
}
