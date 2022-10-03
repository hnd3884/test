package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.STRotX;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRotX;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRotXImpl extends XmlComplexContentImpl implements CTRotX
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTRotXImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public byte getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRotXImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRotXImpl.VAL$0);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getByteValue();
        }
    }
    
    public STRotX xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRotX stRotX = (STRotX)this.get_store().find_attribute_user(CTRotXImpl.VAL$0);
            if (stRotX == null) {
                stRotX = (STRotX)this.get_default_attribute_value(CTRotXImpl.VAL$0);
            }
            return stRotX;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRotXImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final byte byteValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRotXImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRotXImpl.VAL$0);
            }
            simpleValue.setByteValue(byteValue);
        }
    }
    
    public void xsetVal(final STRotX stRotX) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRotX stRotX2 = (STRotX)this.get_store().find_attribute_user(CTRotXImpl.VAL$0);
            if (stRotX2 == null) {
                stRotX2 = (STRotX)this.get_store().add_attribute_user(CTRotXImpl.VAL$0);
            }
            stRotX2.set((XmlObject)stRotX);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRotXImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
