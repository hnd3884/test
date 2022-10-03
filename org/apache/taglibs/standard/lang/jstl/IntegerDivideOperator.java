package org.apache.taglibs.standard.lang.jstl;

public class IntegerDivideOperator extends BinaryOperator
{
    public static final IntegerDivideOperator SINGLETON;
    
    @Override
    public String getOperatorSymbol() {
        return "idiv";
    }
    
    @Override
    public Object apply(final Object pLeft, final Object pRight, final Object pContext, final Logger pLogger) throws ELException {
        if (pLeft == null && pRight == null) {
            if (pLogger.isLoggingWarning()) {
                pLogger.logWarning(Constants.ARITH_OP_NULL, this.getOperatorSymbol());
            }
            return PrimitiveObjects.getInteger(0);
        }
        final long left = Coercions.coerceToPrimitiveNumber(pLeft, Long.class, pLogger).longValue();
        final long right = Coercions.coerceToPrimitiveNumber(pRight, Long.class, pLogger).longValue();
        try {
            return PrimitiveObjects.getLong(left / right);
        }
        catch (final Exception exc) {
            if (pLogger.isLoggingError()) {
                pLogger.logError(Constants.ARITH_ERROR, this.getOperatorSymbol(), "" + left, "" + right);
            }
            return PrimitiveObjects.getInteger(0);
        }
    }
    
    static {
        SINGLETON = new IntegerDivideOperator();
    }
}
