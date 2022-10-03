package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

public class UnionDV extends TypeValidator
{
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        return s;
    }
}
