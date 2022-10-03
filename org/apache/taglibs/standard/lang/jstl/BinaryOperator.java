package org.apache.taglibs.standard.lang.jstl;

public abstract class BinaryOperator
{
    public abstract String getOperatorSymbol();
    
    public abstract Object apply(final Object p0, final Object p1, final Object p2, final Logger p3) throws ELException;
    
    public boolean shouldEvaluate(final Object pLeft) {
        return true;
    }
    
    public boolean shouldCoerceToBoolean() {
        return false;
    }
}
