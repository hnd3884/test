package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import java.util.StringTokenizer;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;

public class ListDatatypeValidator implements DatatypeValidator
{
    DatatypeValidator fItemValidator;
    
    public ListDatatypeValidator(final DatatypeValidator itemDV) {
        this.fItemValidator = itemDV;
    }
    
    @Override
    public void validate(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        final StringTokenizer parsedList = new StringTokenizer(content, " ");
        final int numberOfTokens = parsedList.countTokens();
        if (numberOfTokens == 0) {
            throw new InvalidDatatypeValueException("EmptyList", (Object[])null);
        }
        while (parsedList.hasMoreTokens()) {
            this.fItemValidator.validate(parsedList.nextToken(), context);
        }
    }
}
