package org.apache.xerces.impl.dv.dtd;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.util.XML11Char;
import org.apache.xerces.impl.dv.ValidationContext;

public class XML11NMTOKENDatatypeValidator extends NMTOKENDatatypeValidator
{
    public void validate(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        if (!XML11Char.isXML11ValidNmtoken(s)) {
            throw new InvalidDatatypeValueException("NMTOKENInvalid", new Object[] { s });
        }
    }
}
