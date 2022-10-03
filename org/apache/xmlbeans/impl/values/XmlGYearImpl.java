package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlGYear;

public class XmlGYearImpl extends JavaGDateHolderEx implements XmlGYear
{
    public XmlGYearImpl() {
        super(XmlGYear.type, false);
    }
    
    public XmlGYearImpl(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
