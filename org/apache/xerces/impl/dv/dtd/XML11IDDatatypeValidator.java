package org.apache.xerces.impl.dv.dtd;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.util.XML11Char;
import org.apache.xerces.impl.dv.ValidationContext;

public class XML11IDDatatypeValidator extends IDDatatypeValidator
{
    public void validate(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        if (validationContext.useNamespaces()) {
            if (!XML11Char.isXML11ValidNCName(s)) {
                throw new InvalidDatatypeValueException("IDInvalidWithNamespaces", new Object[] { s });
            }
        }
        else if (!XML11Char.isXML11ValidName(s)) {
            throw new InvalidDatatypeValueException("IDInvalid", new Object[] { s });
        }
        if (validationContext.isIdDeclared(s)) {
            throw new InvalidDatatypeValueException("IDNotUnique", new Object[] { s });
        }
        validationContext.addId(s);
    }
}
