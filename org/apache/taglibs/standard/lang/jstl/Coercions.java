package org.apache.taglibs.standard.lang.jstl;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

public class Coercions
{
    public static Object coerce(final Object pValue, final Class pClass, final Logger pLogger) throws ELException {
        if (pClass == String.class) {
            return coerceToString(pValue, pLogger);
        }
        if (isPrimitiveNumberClass(pClass)) {
            return coerceToPrimitiveNumber(pValue, pClass, pLogger);
        }
        if (pClass == Character.class || pClass == Character.TYPE) {
            return coerceToCharacter(pValue, pLogger);
        }
        if (pClass == Boolean.class || pClass == Boolean.TYPE) {
            return coerceToBoolean(pValue, pLogger);
        }
        return coerceToObject(pValue, pClass, pLogger);
    }
    
    static boolean isPrimitiveNumberClass(final Class pClass) {
        return pClass == Byte.class || pClass == Byte.TYPE || pClass == Short.class || pClass == Short.TYPE || pClass == Integer.class || pClass == Integer.TYPE || pClass == Long.class || pClass == Long.TYPE || pClass == Float.class || pClass == Float.TYPE || pClass == Double.class || pClass == Double.TYPE;
    }
    
    public static String coerceToString(final Object pValue, final Logger pLogger) throws ELException {
        if (pValue == null) {
            return "";
        }
        if (pValue instanceof String) {
            return (String)pValue;
        }
        try {
            return pValue.toString();
        }
        catch (final Exception exc) {
            if (pLogger.isLoggingError()) {
                pLogger.logError(Constants.TOSTRING_EXCEPTION, exc, pValue.getClass().getName());
            }
            return "";
        }
    }
    
    public static Number coerceToPrimitiveNumber(final Object pValue, final Class pClass, final Logger pLogger) throws ELException {
        if (pValue == null || "".equals(pValue)) {
            return coerceToPrimitiveNumber(0L, pClass);
        }
        if (pValue instanceof Character) {
            final char val = (char)pValue;
            return coerceToPrimitiveNumber((short)val, pClass);
        }
        if (pValue instanceof Boolean) {
            if (pLogger.isLoggingError()) {
                pLogger.logError(Constants.BOOLEAN_TO_NUMBER, pValue, pClass.getName());
            }
            return coerceToPrimitiveNumber(0L, pClass);
        }
        if (pValue.getClass() == pClass) {
            return (Number)pValue;
        }
        if (pValue instanceof Number) {
            return coerceToPrimitiveNumber((Number)pValue, pClass);
        }
        if (pValue instanceof String) {
            try {
                return coerceToPrimitiveNumber((String)pValue, pClass);
            }
            catch (final Exception exc) {
                if (pLogger.isLoggingError()) {
                    pLogger.logError(Constants.STRING_TO_NUMBER_EXCEPTION, pValue, pClass.getName());
                }
                return coerceToPrimitiveNumber(0L, pClass);
            }
        }
        if (pLogger.isLoggingError()) {
            pLogger.logError(Constants.COERCE_TO_NUMBER, pValue.getClass().getName(), pClass.getName());
        }
        return coerceToPrimitiveNumber(0L, pClass);
    }
    
    public static Integer coerceToInteger(final Object pValue, final Logger pLogger) throws ELException {
        if (pValue == null) {
            return null;
        }
        if (pValue instanceof Character) {
            return PrimitiveObjects.getInteger((char)pValue);
        }
        if (pValue instanceof Boolean) {
            if (pLogger.isLoggingWarning()) {
                pLogger.logWarning(Constants.BOOLEAN_TO_NUMBER, pValue, Integer.class.getName());
            }
            return PrimitiveObjects.getInteger(((boolean)pValue) ? 1 : 0);
        }
        if (pValue instanceof Integer) {
            return (Integer)pValue;
        }
        if (pValue instanceof Number) {
            return PrimitiveObjects.getInteger(((Number)pValue).intValue());
        }
        if (pValue instanceof String) {
            try {
                return Integer.valueOf((String)pValue);
            }
            catch (final Exception exc) {
                if (pLogger.isLoggingWarning()) {
                    pLogger.logWarning(Constants.STRING_TO_NUMBER_EXCEPTION, pValue, Integer.class.getName());
                }
                return null;
            }
        }
        if (pLogger.isLoggingWarning()) {
            pLogger.logWarning(Constants.COERCE_TO_NUMBER, pValue.getClass().getName(), Integer.class.getName());
        }
        return null;
    }
    
    static Number coerceToPrimitiveNumber(final long pValue, final Class pClass) throws ELException {
        if (pClass == Byte.class || pClass == Byte.TYPE) {
            return PrimitiveObjects.getByte((byte)pValue);
        }
        if (pClass == Short.class || pClass == Short.TYPE) {
            return PrimitiveObjects.getShort((short)pValue);
        }
        if (pClass == Integer.class || pClass == Integer.TYPE) {
            return PrimitiveObjects.getInteger((int)pValue);
        }
        if (pClass == Long.class || pClass == Long.TYPE) {
            return PrimitiveObjects.getLong(pValue);
        }
        if (pClass == Float.class || pClass == Float.TYPE) {
            return PrimitiveObjects.getFloat((float)pValue);
        }
        if (pClass == Double.class || pClass == Double.TYPE) {
            return PrimitiveObjects.getDouble((double)pValue);
        }
        return PrimitiveObjects.getInteger(0);
    }
    
    static Number coerceToPrimitiveNumber(final double pValue, final Class pClass) throws ELException {
        if (pClass == Byte.class || pClass == Byte.TYPE) {
            return PrimitiveObjects.getByte((byte)pValue);
        }
        if (pClass == Short.class || pClass == Short.TYPE) {
            return PrimitiveObjects.getShort((short)pValue);
        }
        if (pClass == Integer.class || pClass == Integer.TYPE) {
            return PrimitiveObjects.getInteger((int)pValue);
        }
        if (pClass == Long.class || pClass == Long.TYPE) {
            return PrimitiveObjects.getLong((long)pValue);
        }
        if (pClass == Float.class || pClass == Float.TYPE) {
            return PrimitiveObjects.getFloat((float)pValue);
        }
        if (pClass == Double.class || pClass == Double.TYPE) {
            return PrimitiveObjects.getDouble(pValue);
        }
        return PrimitiveObjects.getInteger(0);
    }
    
    static Number coerceToPrimitiveNumber(final Number pValue, final Class pClass) throws ELException {
        if (pClass == Byte.class || pClass == Byte.TYPE) {
            return PrimitiveObjects.getByte(pValue.byteValue());
        }
        if (pClass == Short.class || pClass == Short.TYPE) {
            return PrimitiveObjects.getShort(pValue.shortValue());
        }
        if (pClass == Integer.class || pClass == Integer.TYPE) {
            return PrimitiveObjects.getInteger(pValue.intValue());
        }
        if (pClass == Long.class || pClass == Long.TYPE) {
            return PrimitiveObjects.getLong(pValue.longValue());
        }
        if (pClass == Float.class || pClass == Float.TYPE) {
            return PrimitiveObjects.getFloat(pValue.floatValue());
        }
        if (pClass == Double.class || pClass == Double.TYPE) {
            return PrimitiveObjects.getDouble(pValue.doubleValue());
        }
        return PrimitiveObjects.getInteger(0);
    }
    
    static Number coerceToPrimitiveNumber(final String pValue, final Class pClass) throws ELException {
        if (pClass == Byte.class || pClass == Byte.TYPE) {
            return Byte.valueOf(pValue);
        }
        if (pClass == Short.class || pClass == Short.TYPE) {
            return Short.valueOf(pValue);
        }
        if (pClass == Integer.class || pClass == Integer.TYPE) {
            return Integer.valueOf(pValue);
        }
        if (pClass == Long.class || pClass == Long.TYPE) {
            return Long.valueOf(pValue);
        }
        if (pClass == Float.class || pClass == Float.TYPE) {
            return Float.valueOf(pValue);
        }
        if (pClass == Double.class || pClass == Double.TYPE) {
            return Double.valueOf(pValue);
        }
        return PrimitiveObjects.getInteger(0);
    }
    
    public static Character coerceToCharacter(final Object pValue, final Logger pLogger) throws ELException {
        if (pValue == null || "".equals(pValue)) {
            return PrimitiveObjects.getCharacter('\0');
        }
        if (pValue instanceof Character) {
            return (Character)pValue;
        }
        if (pValue instanceof Boolean) {
            if (pLogger.isLoggingError()) {
                pLogger.logError(Constants.BOOLEAN_TO_CHARACTER, pValue);
            }
            return PrimitiveObjects.getCharacter('\0');
        }
        if (pValue instanceof Number) {
            return PrimitiveObjects.getCharacter((char)((Number)pValue).shortValue());
        }
        if (pValue instanceof String) {
            final String str = (String)pValue;
            return PrimitiveObjects.getCharacter(str.charAt(0));
        }
        if (pLogger.isLoggingError()) {
            pLogger.logError(Constants.COERCE_TO_CHARACTER, pValue.getClass().getName());
        }
        return PrimitiveObjects.getCharacter('\0');
    }
    
    public static Boolean coerceToBoolean(final Object pValue, final Logger pLogger) throws ELException {
        if (pValue == null || "".equals(pValue)) {
            return Boolean.FALSE;
        }
        if (pValue instanceof Boolean) {
            return (Boolean)pValue;
        }
        if (pValue instanceof String) {
            final String str = (String)pValue;
            try {
                return Boolean.valueOf(str);
            }
            catch (final Exception exc) {
                if (pLogger.isLoggingError()) {
                    pLogger.logError(Constants.STRING_TO_BOOLEAN, exc, pValue);
                }
                return Boolean.FALSE;
            }
        }
        if (pLogger.isLoggingError()) {
            pLogger.logError(Constants.COERCE_TO_BOOLEAN, pValue.getClass().getName());
        }
        return Boolean.TRUE;
    }
    
    public static Object coerceToObject(final Object pValue, final Class pClass, final Logger pLogger) throws ELException {
        if (pValue == null) {
            return null;
        }
        if (pClass.isAssignableFrom(pValue.getClass())) {
            return pValue;
        }
        if (pValue instanceof String) {
            final String str = (String)pValue;
            final PropertyEditor pe = PropertyEditorManager.findEditor(pClass);
            if (pe == null) {
                if ("".equals(str)) {
                    return null;
                }
                if (pLogger.isLoggingError()) {
                    pLogger.logError(Constants.NO_PROPERTY_EDITOR, str, pClass.getName());
                }
                return null;
            }
            else {
                try {
                    pe.setAsText(str);
                    return pe.getValue();
                }
                catch (final IllegalArgumentException exc) {
                    if ("".equals(str)) {
                        return null;
                    }
                    if (pLogger.isLoggingError()) {
                        pLogger.logError(Constants.PROPERTY_EDITOR_ERROR, exc, pValue, pClass.getName());
                    }
                    return null;
                }
            }
        }
        if (pLogger.isLoggingError()) {
            pLogger.logError(Constants.COERCE_TO_OBJECT, pValue.getClass().getName(), pClass.getName());
        }
        return null;
    }
    
    public static Object applyArithmeticOperator(final Object pLeft, final Object pRight, final ArithmeticOperator pOperator, final Logger pLogger) throws ELException {
        if (pLeft == null && pRight == null) {
            if (pLogger.isLoggingWarning()) {
                pLogger.logWarning(Constants.ARITH_OP_NULL, pOperator.getOperatorSymbol());
            }
            return PrimitiveObjects.getInteger(0);
        }
        if (isFloatingPointType(pLeft) || isFloatingPointType(pRight) || isFloatingPointString(pLeft) || isFloatingPointString(pRight)) {
            final double left = coerceToPrimitiveNumber(pLeft, Double.class, pLogger).doubleValue();
            final double right = coerceToPrimitiveNumber(pRight, Double.class, pLogger).doubleValue();
            return PrimitiveObjects.getDouble(pOperator.apply(left, right, pLogger));
        }
        final long left2 = coerceToPrimitiveNumber(pLeft, Long.class, pLogger).longValue();
        final long right2 = coerceToPrimitiveNumber(pRight, Long.class, pLogger).longValue();
        return PrimitiveObjects.getLong(pOperator.apply(left2, right2, pLogger));
    }
    
    public static Object applyRelationalOperator(final Object pLeft, final Object pRight, final RelationalOperator pOperator, final Logger pLogger) throws ELException {
        if (isFloatingPointType(pLeft) || isFloatingPointType(pRight)) {
            final double left = coerceToPrimitiveNumber(pLeft, Double.class, pLogger).doubleValue();
            final double right = coerceToPrimitiveNumber(pRight, Double.class, pLogger).doubleValue();
            return PrimitiveObjects.getBoolean(pOperator.apply(left, right, pLogger));
        }
        if (isIntegerType(pLeft) || isIntegerType(pRight)) {
            final long left2 = coerceToPrimitiveNumber(pLeft, Long.class, pLogger).longValue();
            final long right2 = coerceToPrimitiveNumber(pRight, Long.class, pLogger).longValue();
            return PrimitiveObjects.getBoolean(pOperator.apply(left2, right2, pLogger));
        }
        if (pLeft instanceof String || pRight instanceof String) {
            final String left3 = coerceToString(pLeft, pLogger);
            final String right3 = coerceToString(pRight, pLogger);
            return PrimitiveObjects.getBoolean(pOperator.apply(left3, right3, pLogger));
        }
        if (pLeft instanceof Comparable) {
            try {
                final int result = ((Comparable)pLeft).compareTo(pRight);
                return PrimitiveObjects.getBoolean(pOperator.apply(result, -result, pLogger));
            }
            catch (final Exception exc) {
                if (pLogger.isLoggingError()) {
                    pLogger.logError(Constants.COMPARABLE_ERROR, exc, pLeft.getClass().getName(), (pRight == null) ? "null" : pRight.getClass().getName(), pOperator.getOperatorSymbol());
                }
                return Boolean.FALSE;
            }
        }
        if (pRight instanceof Comparable) {
            try {
                final int result = ((Comparable)pRight).compareTo(pLeft);
                return PrimitiveObjects.getBoolean(pOperator.apply(-result, result, pLogger));
            }
            catch (final Exception exc) {
                if (pLogger.isLoggingError()) {
                    pLogger.logError(Constants.COMPARABLE_ERROR, exc, pRight.getClass().getName(), (pLeft == null) ? "null" : pLeft.getClass().getName(), pOperator.getOperatorSymbol());
                }
                return Boolean.FALSE;
            }
        }
        if (pLogger.isLoggingError()) {
            pLogger.logError(Constants.ARITH_OP_BAD_TYPE, pOperator.getOperatorSymbol(), pLeft.getClass().getName(), pRight.getClass().getName());
        }
        return Boolean.FALSE;
    }
    
    public static Object applyEqualityOperator(final Object pLeft, final Object pRight, final EqualityOperator pOperator, final Logger pLogger) throws ELException {
        if (pLeft == pRight) {
            return PrimitiveObjects.getBoolean(pOperator.apply(true, pLogger));
        }
        if (pLeft == null || pRight == null) {
            return PrimitiveObjects.getBoolean(pOperator.apply(false, pLogger));
        }
        if (isFloatingPointType(pLeft) || isFloatingPointType(pRight)) {
            final double left = coerceToPrimitiveNumber(pLeft, Double.class, pLogger).doubleValue();
            final double right = coerceToPrimitiveNumber(pRight, Double.class, pLogger).doubleValue();
            return PrimitiveObjects.getBoolean(pOperator.apply(left == right, pLogger));
        }
        if (isIntegerType(pLeft) || isIntegerType(pRight)) {
            final long left2 = coerceToPrimitiveNumber(pLeft, Long.class, pLogger).longValue();
            final long right2 = coerceToPrimitiveNumber(pRight, Long.class, pLogger).longValue();
            return PrimitiveObjects.getBoolean(pOperator.apply(left2 == right2, pLogger));
        }
        if (pLeft instanceof Boolean || pRight instanceof Boolean) {
            final boolean left3 = coerceToBoolean(pLeft, pLogger);
            final boolean right3 = coerceToBoolean(pRight, pLogger);
            return PrimitiveObjects.getBoolean(pOperator.apply(left3 == right3, pLogger));
        }
        if (pLeft instanceof String || pRight instanceof String) {
            final String left4 = coerceToString(pLeft, pLogger);
            final String right4 = coerceToString(pRight, pLogger);
            return PrimitiveObjects.getBoolean(pOperator.apply(left4.equals(right4), pLogger));
        }
        try {
            return PrimitiveObjects.getBoolean(pOperator.apply(pLeft.equals(pRight), pLogger));
        }
        catch (final Exception exc) {
            if (pLogger.isLoggingError()) {
                pLogger.logError(Constants.ERROR_IN_EQUALS, exc, pLeft.getClass().getName(), pRight.getClass().getName(), pOperator.getOperatorSymbol());
            }
            return Boolean.FALSE;
        }
    }
    
    public static boolean isFloatingPointType(final Object pObject) {
        return pObject != null && isFloatingPointType(pObject.getClass());
    }
    
    public static boolean isFloatingPointType(final Class pClass) {
        return pClass == Float.class || pClass == Float.TYPE || pClass == Double.class || pClass == Double.TYPE;
    }
    
    public static boolean isFloatingPointString(final Object pObject) {
        if (pObject instanceof String) {
            final String str = (String)pObject;
            for (int len = str.length(), i = 0; i < len; ++i) {
                final char ch = str.charAt(i);
                if (ch == '.' || ch == 'e' || ch == 'E') {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    public static boolean isIntegerType(final Object pObject) {
        return pObject != null && isIntegerType(pObject.getClass());
    }
    
    public static boolean isIntegerType(final Class pClass) {
        return pClass == Byte.class || pClass == Byte.TYPE || pClass == Short.class || pClass == Short.TYPE || pClass == Character.class || pClass == Character.TYPE || pClass == Integer.class || pClass == Integer.TYPE || pClass == Long.class || pClass == Long.TYPE;
    }
}
