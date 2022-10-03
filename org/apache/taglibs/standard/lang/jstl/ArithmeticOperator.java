package org.apache.taglibs.standard.lang.jstl;

public abstract class ArithmeticOperator extends BinaryOperator
{
    @Override
    public Object apply(final Object pLeft, final Object pRight, final Object pContext, final Logger pLogger) throws ELException {
        return Coercions.applyArithmeticOperator(pLeft, pRight, this, pLogger);
    }
    
    public abstract double apply(final double p0, final double p1, final Logger p2);
    
    public abstract long apply(final long p0, final long p1, final Logger p2);
}
