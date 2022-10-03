package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

public class DateTimeStampDV extends DateTimeDV
{
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return this.parse(s, validationContext.getTypeValidatorHelper().isXMLSchema11());
        }
        catch (final Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "dateTimeStamp" });
        }
    }
    
    protected DateTimeData parse(final String s, final boolean b) throws SchemaDateTimeException {
        final DateTimeData parse = super.parse(s, b);
        if (parse.utc == 0) {
            throw new RuntimeException("dateTimeStamp must have timezone");
        }
        return parse;
    }
}
