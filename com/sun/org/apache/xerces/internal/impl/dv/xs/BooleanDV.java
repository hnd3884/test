package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class BooleanDV extends TypeValidator
{
    private static final String[] fValueSpace;
    
    @Override
    public short getAllowedFacets() {
        return 24;
    }
    
    @Override
    public Object getActualValue(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        Boolean ret = null;
        if (content.equals(BooleanDV.fValueSpace[0]) || content.equals(BooleanDV.fValueSpace[2])) {
            ret = Boolean.FALSE;
        }
        else {
            if (!content.equals(BooleanDV.fValueSpace[1]) && !content.equals(BooleanDV.fValueSpace[3])) {
                throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { content, "boolean" });
            }
            ret = Boolean.TRUE;
        }
        return ret;
    }
    
    static {
        fValueSpace = new String[] { "false", "true", "0", "1" };
    }
}
