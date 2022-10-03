package org.apache.xerces.impl.dv.dtd;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.DatatypeValidator;

public class NMTOKENDatatypeValidator implements DatatypeValidator
{
    public void validate(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        if (!XMLChar.isValidNmtoken(s)) {
            throw new InvalidDatatypeValueException("NMTOKENInvalid", new Object[] { s });
        }
    }
}
