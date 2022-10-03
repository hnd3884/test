package org.apache.xerces.impl.dv.dtd;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.util.XML11Char;
import org.apache.xerces.impl.dv.ValidationContext;

public class XML11IDREFDatatypeValidator extends IDREFDatatypeValidator
{
    public void validate(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        if (validationContext.useNamespaces()) {
            if (!XML11Char.isXML11ValidNCName(s)) {
                throw new InvalidDatatypeValueException("IDREFInvalidWithNamespaces", new Object[] { s });
            }
        }
        else if (!XML11Char.isXML11ValidName(s)) {
            throw new InvalidDatatypeValueException("IDREFInvalid", new Object[] { s });
        }
        validationContext.addIdRef(s);
    }
}
