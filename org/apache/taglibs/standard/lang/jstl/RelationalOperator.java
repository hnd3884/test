package org.apache.taglibs.standard.lang.jstl;

public abstract class RelationalOperator extends BinaryOperator
{
    @Override
    public Object apply(final Object pLeft, final Object pRight, final Object pContext, final Logger pLogger) throws ELException {
        return Coercions.applyRelationalOperator(pLeft, pRight, this, pLogger);
    }
    
    public abstract boolean apply(final double p0, final double p1, final Logger p2);
    
    public abstract boolean apply(final long p0, final long p1, final Logger p2);
    
    public abstract boolean apply(final String p0, final String p1, final Logger p2);
}
