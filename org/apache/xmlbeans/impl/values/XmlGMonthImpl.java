package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlGMonth;

public class XmlGMonthImpl extends JavaGDateHolderEx implements XmlGMonth
{
    public XmlGMonthImpl() {
        super(XmlGMonth.type, false);
    }
    
    public XmlGMonthImpl(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
