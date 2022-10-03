package org.apache.taglibs.standard.lang.jstl;

public class UnaryMinusOperator extends UnaryOperator
{
    public static final UnaryMinusOperator SINGLETON;
    
    @Override
    public String getOperatorSymbol() {
        return "-";
    }
    
    @Override
    public Object apply(final Object pValue, final Object pContext, final Logger pLogger) throws ELException {
        if (pValue == null) {
            return PrimitiveObjects.getInteger(0);
        }
        if (pValue instanceof String) {
            if (Coercions.isFloatingPointString(pValue)) {
                final double dval = Coercions.coerceToPrimitiveNumber(pValue, Double.class, pLogger).doubleValue();
                return PrimitiveObjects.getDouble(-dval);
            }
            final long lval = Coercions.coerceToPrimitiveNumber(pValue, Long.class, pLogger).longValue();
            return PrimitiveObjects.getLong(-lval);
        }
        else {
            if (pValue instanceof Byte) {
                return PrimitiveObjects.getByte((byte)(-(byte)pValue));
            }
            if (pValue instanceof Short) {
                return PrimitiveObjects.getShort((short)(-(short)pValue));
            }
            if (pValue instanceof Integer) {
                return PrimitiveObjects.getInteger(-(int)pValue);
            }
            if (pValue instanceof Long) {
                return PrimitiveObjects.getLong(-(long)pValue);
            }
            if (pValue instanceof Float) {
                return PrimitiveObjects.getFloat(-(float)pValue);
            }
            if (pValue instanceof Double) {
                return PrimitiveObjects.getDouble(-(double)pValue);
            }
            if (pLogger.isLoggingError()) {
                pLogger.logError(Constants.UNARY_OP_BAD_TYPE, this.getOperatorSymbol(), pValue.getClass().getName());
            }
            return PrimitiveObjects.getInteger(0);
        }
    }
    
    static {
        SINGLETON = new UnaryMinusOperator();
    }
}
