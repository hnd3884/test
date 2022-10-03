package org.apache.taglibs.standard.lang.jstl;

public class NotOperator extends UnaryOperator
{
    public static final NotOperator SINGLETON;
    
    @Override
    public String getOperatorSymbol() {
        return "not";
    }
    
    @Override
    public Object apply(final Object pValue, final Object pContext, final Logger pLogger) throws ELException {
        final boolean val = Coercions.coerceToBoolean(pValue, pLogger);
        return PrimitiveObjects.getBoolean(!val);
    }
    
    static {
        SINGLETON = new NotOperator();
    }
}
