package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class IDDV extends TypeValidator
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
        final String content = (String)value;
        if (context.isIdDeclared(content)) {
            throw new InvalidDatatypeValueException("cvc-id.2", new Object[] { content });
        }
        context.addId(content);
    }
}
