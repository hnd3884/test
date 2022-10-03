package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.STFirstSliceAng;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTFirstSliceAng;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFirstSliceAngImpl extends XmlComplexContentImpl implements CTFirstSliceAng
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTFirstSliceAngImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFirstSliceAngImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTFirstSliceAngImpl.VAL$0);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STFirstSliceAng xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFirstSliceAng stFirstSliceAng = (STFirstSliceAng)this.get_store().find_attribute_user(CTFirstSliceAngImpl.VAL$0);
            if (stFirstSliceAng == null) {
                stFirstSliceAng = (STFirstSliceAng)this.get_default_attribute_value(CTFirstSliceAngImpl.VAL$0);
            }
            return stFirstSliceAng;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFirstSliceAngImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFirstSliceAngImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFirstSliceAngImpl.VAL$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetVal(final STFirstSliceAng stFirstSliceAng) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFirstSliceAng stFirstSliceAng2 = (STFirstSliceAng)this.get_store().find_attribute_user(CTFirstSliceAngImpl.VAL$0);
            if (stFirstSliceAng2 == null) {
                stFirstSliceAng2 = (STFirstSliceAng)this.get_store().add_attribute_user(CTFirstSliceAngImpl.VAL$0);
            }
            stFirstSliceAng2.set((XmlObject)stFirstSliceAng);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFirstSliceAngImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
