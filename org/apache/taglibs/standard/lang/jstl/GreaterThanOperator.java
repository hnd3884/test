package org.apache.taglibs.standard.lang.jstl;

public class GreaterThanOperator extends RelationalOperator
{
    public static final GreaterThanOperator SINGLETON;
    
    @Override
    public String getOperatorSymbol() {
        return ">";
    }
    
    @Override
    public Object apply(final Object pLeft, final Object pRight, final Object pContext, final Logger pLogger) throws ELException {
        if (pLeft == pRight) {
            return Boolean.FALSE;
        }
        if (pLeft == null || pRight == null) {
            return Boolean.FALSE;
        }
        return super.apply(pLeft, pRight, pContext, pLogger);
    }
    
    @Override
    public boolean apply(final double pLeft, final double pRight, final Logger pLogger) {
        return pLeft > pRight;
    }
    
    @Override
    public boolean apply(final long pLeft, final long pRight, final Logger pLogger) {
        return pLeft > pRight;
    }
    
    @Override
    public boolean apply(final String pLeft, final String pRight, final Logger pLogger) {
        return pLeft.compareTo(pRight) > 0;
    }
    
    static {
        SINGLETON = new GreaterThanOperator();
    }
}
