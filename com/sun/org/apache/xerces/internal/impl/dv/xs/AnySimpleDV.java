package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class AnySimpleDV extends TypeValidator
{
    @Override
    public short getAllowedFacets() {
        return 0;
    }
    
    @Override
    public Object getActualValue(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        return content;
    }
}
