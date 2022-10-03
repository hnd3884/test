package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.impl.common.XMLChar;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlNCName;

public class XmlNCNameImpl extends JavaStringHolderEx implements XmlNCName
{
    public XmlNCNameImpl() {
        super(XmlNCName.type, false);
    }
    
    public XmlNCNameImpl(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
    
    public static void validateLexical(final String v, final ValidationContext context) {
        if (!XMLChar.isValidNCName(v)) {
            context.invalid("NCName", new Object[] { v });
        }
    }
}
