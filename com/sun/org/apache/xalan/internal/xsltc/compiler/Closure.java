package com.sun.org.apache.xalan.internal.xsltc.compiler;

public interface Closure
{
    boolean inInnerClass();
    
    Closure getParentClosure();
    
    String getInnerClassName();
    
    void addVariable(final VariableRefBase p0);
}
