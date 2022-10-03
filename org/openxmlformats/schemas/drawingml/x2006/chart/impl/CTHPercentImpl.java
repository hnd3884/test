package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.STHPercent;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTHPercent;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTHPercentImpl extends XmlComplexContentImpl implements CTHPercent
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTHPercentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHPercentImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTHPercentImpl.VAL$0);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STHPercent xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHPercent sthPercent = (STHPercent)this.get_store().find_attribute_user(CTHPercentImpl.VAL$0);
            if (sthPercent == null) {
                sthPercent = (STHPercent)this.get_default_attribute_value(CTHPercentImpl.VAL$0);
            }
            return sthPercent;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHPercentImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHPercentImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHPercentImpl.VAL$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetVal(final STHPercent sthPercent) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHPercent sthPercent2 = (STHPercent)this.get_store().find_attribute_user(CTHPercentImpl.VAL$0);
            if (sthPercent2 == null) {
                sthPercent2 = (STHPercent)this.get_store().add_attribute_user(CTHPercentImpl.VAL$0);
            }
            sthPercent2.set((XmlObject)sthPercent);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHPercentImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
