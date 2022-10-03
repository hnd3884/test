package org.apache.taglibs.standard.lang.jstl;

public abstract class UnaryOperator
{
    public abstract String getOperatorSymbol();
    
    public abstract Object apply(final Object p0, final Object p1, final Logger p2) throws ELException;
}
