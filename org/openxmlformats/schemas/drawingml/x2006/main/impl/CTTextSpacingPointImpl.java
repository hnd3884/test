package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextSpacingPoint;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacingPoint;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextSpacingPointImpl extends XmlComplexContentImpl implements CTTextSpacingPoint
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTTextSpacingPointImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextSpacingPointImpl.VAL$0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STTextSpacingPoint xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextSpacingPoint)this.get_store().find_attribute_user(CTTextSpacingPointImpl.VAL$0);
        }
    }
    
    public void setVal(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextSpacingPointImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextSpacingPointImpl.VAL$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetVal(final STTextSpacingPoint stTextSpacingPoint) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextSpacingPoint stTextSpacingPoint2 = (STTextSpacingPoint)this.get_store().find_attribute_user(CTTextSpacingPointImpl.VAL$0);
            if (stTextSpacingPoint2 == null) {
                stTextSpacingPoint2 = (STTextSpacingPoint)this.get_store().add_attribute_user(CTTextSpacingPointImpl.VAL$0);
            }
            stTextSpacingPoint2.set((XmlObject)stTextSpacingPoint);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
