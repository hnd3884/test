package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class EntityDV extends TypeValidator
{
    @Override
    public short getAllowedFacets() {
        return 2079;
    }
    
    @Override
    public Object getActualValue(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        if (!XMLChar.isValidNCName(content)) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { content, "NCName" });
        }
        return content;
    }
    
    @Override
    public void checkExtraRules(final Object value, final ValidationContext context) throws InvalidDatatypeValueException {
        if (!context.isEntityUnparsed((String)value)) {
            throw new InvalidDatatypeValueException("UndeclaredEntity", new Object[] { value });
        }
    }
}
