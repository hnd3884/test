package org.apache.taglibs.standard.lang.jstl;

public class NotEqualsOperator extends EqualityOperator
{
    public static final NotEqualsOperator SINGLETON;
    
    @Override
    public String getOperatorSymbol() {
        return "!=";
    }
    
    @Override
    public boolean apply(final boolean pAreEqual, final Logger pLogger) {
        return !pAreEqual;
    }
    
    static {
        SINGLETON = new NotEqualsOperator();
    }
}
