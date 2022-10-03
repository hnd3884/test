package org.apache.taglibs.standard.lang.jstl;

public class EqualsOperator extends EqualityOperator
{
    public static final EqualsOperator SINGLETON;
    
    @Override
    public String getOperatorSymbol() {
        return "==";
    }
    
    @Override
    public boolean apply(final boolean pAreEqual, final Logger pLogger) {
        return pAreEqual;
    }
    
    static {
        SINGLETON = new EqualsOperator();
    }
}
