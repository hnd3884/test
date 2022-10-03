package org.apache.taglibs.standard.lang.jstl;

public abstract class EqualityOperator extends BinaryOperator
{
    @Override
    public Object apply(final Object pLeft, final Object pRight, final Object pContext, final Logger pLogger) throws ELException {
        return Coercions.applyEqualityOperator(pLeft, pRight, this, pLogger);
    }
    
    public abstract boolean apply(final boolean p0, final Logger p1);
}
