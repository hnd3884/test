package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.impl.common.XMLChar;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNMTOKEN;

public class XmlNmTokenImpl extends JavaStringHolderEx implements XmlNMTOKEN
{
    public XmlNmTokenImpl() {
        super(XmlNMTOKEN.type, false);
    }
    
    public XmlNmTokenImpl(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
    
    public static void validateLexical(final String v, final ValidationContext context) {
        if (!XMLChar.isValidNmtoken(v)) {
            context.invalid("NMTOKEN", new Object[] { v });
        }
    }
}
