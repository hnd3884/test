package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;

public class NMTOKENDatatypeValidator implements DatatypeValidator
{
    @Override
    public void validate(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        if (!XMLChar.isValidNmtoken(content)) {
            throw new InvalidDatatypeValueException("NMTOKENInvalid", new Object[] { content });
        }
    }
}
