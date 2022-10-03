package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class XML11NMTOKENDatatypeValidator extends NMTOKENDatatypeValidator
{
    @Override
    public void validate(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        if (!XML11Char.isXML11ValidNmtoken(content)) {
            throw new InvalidDatatypeValueException("NMTOKENInvalid", new Object[] { content });
        }
    }
}
