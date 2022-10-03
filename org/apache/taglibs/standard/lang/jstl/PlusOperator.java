package org.apache.taglibs.standard.lang.jstl;

public class PlusOperator extends ArithmeticOperator
{
    public static final PlusOperator SINGLETON;
    
    @Override
    public String getOperatorSymbol() {
        return "+";
    }
    
    @Override
    public double apply(final double pLeft, final double pRight, final Logger pLogger) {
        return pLeft + pRight;
    }
    
    @Override
    public long apply(final long pLeft, final long pRight, final Logger pLogger) {
        return pLeft + pRight;
    }
    
    static {
        SINGLETON = new PlusOperator();
    }
}
