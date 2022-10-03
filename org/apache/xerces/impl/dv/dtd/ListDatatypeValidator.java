package org.apache.xerces.impl.dv.dtd;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import java.util.StringTokenizer;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.DatatypeValidator;

public class ListDatatypeValidator implements DatatypeValidator
{
    final DatatypeValidator fItemValidator;
    
    public ListDatatypeValidator(final DatatypeValidator fItemValidator) {
        this.fItemValidator = fItemValidator;
    }
    
    public void validate(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        final StringTokenizer stringTokenizer = new StringTokenizer(s, " ");
        if (stringTokenizer.countTokens() == 0) {
            throw new InvalidDatatypeValueException("EmptyList", (Object[])null);
        }
        while (stringTokenizer.hasMoreTokens()) {
            this.fItemValidator.validate(stringTokenizer.nextToken(), validationContext);
        }
    }
}
