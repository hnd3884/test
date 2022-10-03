package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.STRotY;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRotY;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRotYImpl extends XmlComplexContentImpl implements CTRotY
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTRotYImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRotYImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRotYImpl.VAL$0);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STRotY xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRotY stRotY = (STRotY)this.get_store().find_attribute_user(CTRotYImpl.VAL$0);
            if (stRotY == null) {
                stRotY = (STRotY)this.get_default_attribute_value(CTRotYImpl.VAL$0);
            }
            return stRotY;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRotYImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRotYImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRotYImpl.VAL$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetVal(final STRotY stRotY) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRotY stRotY2 = (STRotY)this.get_store().find_attribute_user(CTRotYImpl.VAL$0);
            if (stRotY2 == null) {
                stRotY2 = (STRotY)this.get_store().add_attribute_user(CTRotYImpl.VAL$0);
            }
            stRotY2.set((XmlObject)stRotY);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRotYImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
