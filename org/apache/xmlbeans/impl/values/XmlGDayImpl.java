package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlGDay;

public class XmlGDayImpl extends JavaGDateHolderEx implements XmlGDay
{
    public XmlGDayImpl() {
        super(XmlGDay.type, false);
    }
    
    public XmlGDayImpl(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
