package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.util.XML11Char;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.impl.dv.ValidationContext;

public class IDDV extends TypeValidator
{
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        if (!((validationContext.getDatatypeXMLVersion() == 1) ? XMLChar.isValidNCName(s) : XML11Char.isXML11ValidNCName(s))) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "NCName" });
        }
        return s;
    }
    
    public void checkExtraRules(final Object o, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        final String s = (String)o;
        if (validationContext.isIdDeclared(s)) {
            throw new InvalidDatatypeValueException("cvc-id.2", new Object[] { s });
        }
        validationContext.addId(s);
    }
}
