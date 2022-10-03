package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextSpacingPercent;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacingPercent;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextSpacingPercentImpl extends XmlComplexContentImpl implements CTTextSpacingPercent
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTTextSpacingPercentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextSpacingPercentImpl.VAL$0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STTextSpacingPercent xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextSpacingPercent)this.get_store().find_attribute_user(CTTextSpacingPercentImpl.VAL$0);
        }
    }
    
    public void setVal(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextSpacingPercentImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextSpacingPercentImpl.VAL$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetVal(final STTextSpacingPercent stTextSpacingPercent) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextSpacingPercent stTextSpacingPercent2 = (STTextSpacingPercent)this.get_store().find_attribute_user(CTTextSpacingPercentImpl.VAL$0);
            if (stTextSpacingPercent2 == null) {
                stTextSpacingPercent2 = (STTextSpacingPercent)this.get_store().add_attribute_user(CTTextSpacingPercentImpl.VAL$0);
            }
            stTextSpacingPercent2.set((XmlObject)stTextSpacingPercent);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
