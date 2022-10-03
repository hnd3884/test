package com.sun.org.apache.xalan.internal.xsltc.compiler;

abstract class RelativeLocationPath extends Expression
{
    public abstract int getAxis();
    
    public abstract void setAxis(final int p0);
}
