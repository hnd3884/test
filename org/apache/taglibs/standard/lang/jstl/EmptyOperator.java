package org.apache.taglibs.standard.lang.jstl;

import java.util.Map;
import java.util.Collection;
import java.lang.reflect.Array;

public class EmptyOperator extends UnaryOperator
{
    public static final EmptyOperator SINGLETON;
    
    @Override
    public String getOperatorSymbol() {
        return "empty";
    }
    
    @Override
    public Object apply(final Object pValue, final Object pContext, final Logger pLogger) throws ELException {
        if (pValue == null) {
            return PrimitiveObjects.getBoolean(true);
        }
        if ("".equals(pValue)) {
            return PrimitiveObjects.getBoolean(true);
        }
        if (pValue.getClass().isArray() && Array.getLength(pValue) == 0) {
            return PrimitiveObjects.getBoolean(true);
        }
        if (pValue instanceof Collection && ((Collection)pValue).isEmpty()) {
            return PrimitiveObjects.getBoolean(true);
        }
        if (pValue instanceof Map && ((Map)pValue).isEmpty()) {
            return PrimitiveObjects.getBoolean(true);
        }
        return PrimitiveObjects.getBoolean(false);
    }
    
    static {
        SINGLETON = new EmptyOperator();
    }
}
