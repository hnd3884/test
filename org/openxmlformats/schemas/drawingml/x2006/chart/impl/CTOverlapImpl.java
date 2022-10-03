package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.STOverlap;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTOverlap;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTOverlapImpl extends XmlComplexContentImpl implements CTOverlap
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTOverlapImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public byte getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOverlapImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOverlapImpl.VAL$0);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getByteValue();
        }
    }
    
    public STOverlap xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOverlap stOverlap = (STOverlap)this.get_store().find_attribute_user(CTOverlapImpl.VAL$0);
            if (stOverlap == null) {
                stOverlap = (STOverlap)this.get_default_attribute_value(CTOverlapImpl.VAL$0);
            }
            return stOverlap;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOverlapImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final byte byteValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOverlapImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOverlapImpl.VAL$0);
            }
            simpleValue.setByteValue(byteValue);
        }
    }
    
    public void xsetVal(final STOverlap stOverlap) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOverlap stOverlap2 = (STOverlap)this.get_store().find_attribute_user(CTOverlapImpl.VAL$0);
            if (stOverlap2 == null) {
                stOverlap2 = (STOverlap)this.get_store().add_attribute_user(CTOverlapImpl.VAL$0);
            }
            stOverlap2.set((XmlObject)stOverlap);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOverlapImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
