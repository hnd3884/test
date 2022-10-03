package org.apache.taglibs.standard.lang.jstl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

public class ArraySuffix extends ValueSuffix
{
    static Object[] sNoArgs;
    Expression mIndex;
    
    public Expression getIndex() {
        return this.mIndex;
    }
    
    public void setIndex(final Expression pIndex) {
        this.mIndex = pIndex;
    }
    
    public ArraySuffix(final Expression pIndex) {
        this.mIndex = pIndex;
    }
    
    Object evaluateIndex(final Object pContext, final VariableResolver pResolver, final Map functions, final String defaultPrefix, final Logger pLogger) throws ELException {
        return this.mIndex.evaluate(pContext, pResolver, functions, defaultPrefix, pLogger);
    }
    
    String getOperatorSymbol() {
        return "[]";
    }
    
    @Override
    public String getExpressionString() {
        return "[" + this.mIndex.getExpressionString() + "]";
    }
    
    @Override
    public Object evaluate(final Object pValue, final Object pContext, final VariableResolver pResolver, final Map functions, final String defaultPrefix, final Logger pLogger) throws ELException {
        if (pValue == null) {
            if (pLogger.isLoggingWarning()) {
                pLogger.logWarning(Constants.CANT_GET_INDEXED_VALUE_OF_NULL, this.getOperatorSymbol());
            }
            return null;
        }
        final Object indexVal;
        if ((indexVal = this.evaluateIndex(pContext, pResolver, functions, defaultPrefix, pLogger)) == null) {
            if (pLogger.isLoggingWarning()) {
                pLogger.logWarning(Constants.CANT_GET_NULL_INDEX, this.getOperatorSymbol());
            }
            return null;
        }
        if (pValue instanceof Map) {
            final Map val = (Map)pValue;
            return val.get(indexVal);
        }
        if (pValue instanceof List || pValue.getClass().isArray()) {
            final Integer indexObj = Coercions.coerceToInteger(indexVal, pLogger);
            if (indexObj == null) {
                if (pLogger.isLoggingError()) {
                    pLogger.logError(Constants.BAD_INDEX_VALUE, this.getOperatorSymbol(), indexVal.getClass().getName());
                }
                return null;
            }
            if (pValue instanceof List) {
                try {
                    return ((List)pValue).get(indexObj);
                }
                catch (final ArrayIndexOutOfBoundsException exc) {
                    if (pLogger.isLoggingWarning()) {
                        pLogger.logWarning(Constants.EXCEPTION_ACCESSING_LIST, exc, indexObj);
                    }
                    return null;
                }
                catch (final IndexOutOfBoundsException exc2) {
                    if (pLogger.isLoggingWarning()) {
                        pLogger.logWarning(Constants.EXCEPTION_ACCESSING_LIST, exc2, indexObj);
                    }
                    return null;
                }
                catch (final Exception exc3) {
                    if (pLogger.isLoggingError()) {
                        pLogger.logError(Constants.EXCEPTION_ACCESSING_LIST, exc3, indexObj);
                    }
                    return null;
                }
            }
            try {
                return Array.get(pValue, indexObj);
            }
            catch (final ArrayIndexOutOfBoundsException exc) {
                if (pLogger.isLoggingWarning()) {
                    pLogger.logWarning(Constants.EXCEPTION_ACCESSING_ARRAY, exc, indexObj);
                }
                return null;
            }
            catch (final IndexOutOfBoundsException exc2) {
                if (pLogger.isLoggingWarning()) {
                    pLogger.logWarning(Constants.EXCEPTION_ACCESSING_ARRAY, exc2, indexObj);
                }
                return null;
            }
            catch (final Exception exc3) {
                if (pLogger.isLoggingError()) {
                    pLogger.logError(Constants.EXCEPTION_ACCESSING_ARRAY, exc3, indexObj);
                }
                return null;
            }
        }
        final String indexStr;
        if ((indexStr = Coercions.coerceToString(indexVal, pLogger)) == null) {
            return null;
        }
        final BeanInfoProperty property;
        if ((property = BeanInfoManager.getBeanInfoProperty(pValue.getClass(), indexStr, pLogger)) != null && property.getReadMethod() != null) {
            try {
                return property.getReadMethod().invoke(pValue, ArraySuffix.sNoArgs);
            }
            catch (final InvocationTargetException exc4) {
                if (pLogger.isLoggingError()) {
                    pLogger.logError(Constants.ERROR_GETTING_PROPERTY, exc4.getTargetException(), indexStr, pValue.getClass().getName());
                }
                return null;
            }
            catch (final Exception exc5) {
                if (pLogger.isLoggingError()) {
                    pLogger.logError(Constants.ERROR_GETTING_PROPERTY, exc5, indexStr, pValue.getClass().getName());
                }
                return null;
            }
        }
        if (pLogger.isLoggingError()) {
            pLogger.logError(Constants.CANT_FIND_INDEX, indexVal, pValue.getClass().getName(), this.getOperatorSymbol());
        }
        return null;
    }
    
    static {
        ArraySuffix.sNoArgs = new Object[0];
    }
}
