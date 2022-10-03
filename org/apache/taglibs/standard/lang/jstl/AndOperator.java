package org.apache.taglibs.standard.lang.jstl;

public class AndOperator extends BinaryOperator
{
    public static final AndOperator SINGLETON;
    
    @Override
    public String getOperatorSymbol() {
        return "and";
    }
    
    @Override
    public Object apply(final Object pLeft, final Object pRight, final Object pContext, final Logger pLogger) throws ELException {
        final boolean left = Coercions.coerceToBoolean(pLeft, pLogger);
        final boolean right = Coercions.coerceToBoolean(pRight, pLogger);
        return PrimitiveObjects.getBoolean(left && right);
    }
    
    @Override
    public boolean shouldEvaluate(final Object pLeft) {
        return pLeft instanceof Boolean && (boolean)pLeft;
    }
    
    @Override
    public boolean shouldCoerceToBoolean() {
        return true;
    }
    
    static {
        SINGLETON = new AndOperator();
    }
}
