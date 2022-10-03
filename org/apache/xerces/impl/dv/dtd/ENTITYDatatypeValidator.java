package org.apache.xerces.impl.dv.dtd;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.DatatypeValidator;

public class ENTITYDatatypeValidator implements DatatypeValidator
{
    public void validate(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        if (!validationContext.isEntityUnparsed(s)) {
            throw new InvalidDatatypeValueException("ENTITYNotUnparsed", new Object[] { s });
        }
    }
}
