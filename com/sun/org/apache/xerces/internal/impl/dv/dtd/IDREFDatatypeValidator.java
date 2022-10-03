package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;

public class IDREFDatatypeValidator implements DatatypeValidator
{
    @Override
    public void validate(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        if (context.useNamespaces()) {
            if (!XMLChar.isValidNCName(content)) {
                throw new InvalidDatatypeValueException("IDREFInvalidWithNamespaces", new Object[] { content });
            }
        }
        else if (!XMLChar.isValidName(content)) {
            throw new InvalidDatatypeValueException("IDREFInvalid", new Object[] { content });
        }
        context.addIdRef(content);
    }
}
