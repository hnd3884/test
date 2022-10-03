package org.apache.taglibs.standard.lang.jstl;

public class DivideOperator extends BinaryOperator
{
    public static final DivideOperator SINGLETON;
    
    @Override
    public String getOperatorSymbol() {
        return "/";
    }
    
    @Override
    public Object apply(final Object pLeft, final Object pRight, final Object pContext, final Logger pLogger) throws ELException {
        if (pLeft == null && pRight == null) {
            if (pLogger.isLoggingWarning()) {
                pLogger.logWarning(Constants.ARITH_OP_NULL, this.getOperatorSymbol());
            }
            return PrimitiveObjects.getInteger(0);
        }
        final double left = Coercions.coerceToPrimitiveNumber(pLeft, Double.class, pLogger).doubleValue();
        final double right = Coercions.coerceToPrimitiveNumber(pRight, Double.class, pLogger).doubleValue();
        try {
            return PrimitiveObjects.getDouble(left / right);
        }
        catch (final Exception exc) {
            if (pLogger.isLoggingError()) {
                pLogger.logError(Constants.ARITH_ERROR, this.getOperatorSymbol(), "" + left, "" + right);
            }
            return PrimitiveObjects.getInteger(0);
        }
    }
    
    static {
        SINGLETON = new DivideOperator();
    }
}
