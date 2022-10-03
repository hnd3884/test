package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

public abstract class NumberType extends Type
{
    @Override
    public boolean isNumber() {
        return true;
    }
    
    @Override
    public boolean isSimple() {
        return true;
    }
}
