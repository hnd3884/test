package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

public class BooleanDV extends TypeValidator
{
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        if ("false".equals(s) || "0".equals(s)) {
            return Boolean.FALSE;
        }
        if ("true".equals(s) || "1".equals(s)) {
            return Boolean.TRUE;
        }
        throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "boolean" });
    }
}
