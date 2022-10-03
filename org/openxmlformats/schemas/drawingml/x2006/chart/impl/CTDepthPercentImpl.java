package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.STDepthPercent;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDepthPercent;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDepthPercentImpl extends XmlComplexContentImpl implements CTDepthPercent
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTDepthPercentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDepthPercentImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDepthPercentImpl.VAL$0);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STDepthPercent xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDepthPercent stDepthPercent = (STDepthPercent)this.get_store().find_attribute_user(CTDepthPercentImpl.VAL$0);
            if (stDepthPercent == null) {
                stDepthPercent = (STDepthPercent)this.get_default_attribute_value(CTDepthPercentImpl.VAL$0);
            }
            return stDepthPercent;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDepthPercentImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDepthPercentImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDepthPercentImpl.VAL$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetVal(final STDepthPercent stDepthPercent) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDepthPercent stDepthPercent2 = (STDepthPercent)this.get_store().find_attribute_user(CTDepthPercentImpl.VAL$0);
            if (stDepthPercent2 == null) {
                stDepthPercent2 = (STDepthPercent)this.get_store().add_attribute_user(CTDepthPercentImpl.VAL$0);
            }
            stDepthPercent2.set((XmlObject)stDepthPercent);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDepthPercentImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
