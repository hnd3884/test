package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

public class ErrorDV extends TypeValidator
{
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "error" });
    }
}
