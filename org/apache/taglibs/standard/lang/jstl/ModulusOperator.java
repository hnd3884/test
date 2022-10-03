package org.apache.taglibs.standard.lang.jstl;

public class ModulusOperator extends BinaryOperator
{
    public static final ModulusOperator SINGLETON;
    
    @Override
    public String getOperatorSymbol() {
        return "%";
    }
    
    @Override
    public Object apply(final Object pLeft, final Object pRight, final Object pContext, final Logger pLogger) throws ELException {
        if (pLeft == null && pRight == null) {
            if (pLogger.isLoggingWarning()) {
                pLogger.logWarning(Constants.ARITH_OP_NULL, this.getOperatorSymbol());
            }
            return PrimitiveObjects.getInteger(0);
        }
        if ((pLeft != null && (Coercions.isFloatingPointType(pLeft) || Coercions.isFloatingPointString(pLeft))) || (pRight != null && (Coercions.isFloatingPointType(pRight) || Coercions.isFloatingPointString(pRight)))) {
            final double left = Coercions.coerceToPrimitiveNumber(pLeft, Double.class, pLogger).doubleValue();
            final double right = Coercions.coerceToPrimitiveNumber(pRight, Double.class, pLogger).doubleValue();
            try {
                return PrimitiveObjects.getDouble(left % right);
            }
            catch (final Exception exc) {
                if (pLogger.isLoggingError()) {
                    pLogger.logError(Constants.ARITH_ERROR, this.getOperatorSymbol(), "" + left, "" + right);
                }
                return PrimitiveObjects.getInteger(0);
            }
        }
        final long left2 = Coercions.coerceToPrimitiveNumber(pLeft, Long.class, pLogger).longValue();
        final long right2 = Coercions.coerceToPrimitiveNumber(pRight, Long.class, pLogger).longValue();
        try {
            return PrimitiveObjects.getLong(left2 % right2);
        }
        catch (final Exception exc) {
            if (pLogger.isLoggingError()) {
                pLogger.logError(Constants.ARITH_ERROR, this.getOperatorSymbol(), "" + left2, "" + right2);
            }
            return PrimitiveObjects.getInteger(0);
        }
    }
    
    static {
        SINGLETON = new ModulusOperator();
    }
}
