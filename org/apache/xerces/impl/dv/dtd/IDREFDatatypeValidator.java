package org.apache.xerces.impl.dv.dtd;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.DatatypeValidator;

public class IDREFDatatypeValidator implements DatatypeValidator
{
    public void validate(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        if (validationContext.useNamespaces()) {
            if (!XMLChar.isValidNCName(s)) {
                throw new InvalidDatatypeValueException("IDREFInvalidWithNamespaces", new Object[] { s });
            }
        }
        else if (!XMLChar.isValidName(s)) {
            throw new InvalidDatatypeValueException("IDREFInvalid", new Object[] { s });
        }
        validationContext.addIdRef(s);
    }
}
