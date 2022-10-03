package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;

public class ENTITYDatatypeValidator implements DatatypeValidator
{
    @Override
    public void validate(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        if (!context.isEntityUnparsed(content)) {
            throw new InvalidDatatypeValueException("ENTITYNotUnparsed", new Object[] { content });
        }
    }
}
