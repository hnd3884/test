package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlGMonthDay;

public class XmlGMonthDayImpl extends JavaGDateHolderEx implements XmlGMonthDay
{
    public XmlGMonthDayImpl() {
        super(XmlGMonthDay.type, false);
    }
    
    public XmlGMonthDayImpl(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
