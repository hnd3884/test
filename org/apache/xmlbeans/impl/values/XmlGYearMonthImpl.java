package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlGYearMonth;

public class XmlGYearMonthImpl extends JavaGDateHolderEx implements XmlGYearMonth
{
    public XmlGYearMonthImpl() {
        super(XmlGYearMonth.type, false);
    }
    
    public XmlGYearMonthImpl(final SchemaType type, final boolean complex) {
        super(type, complex);
    }
}
