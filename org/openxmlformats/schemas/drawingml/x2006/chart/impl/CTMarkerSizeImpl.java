package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.STMarkerSize;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarkerSize;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTMarkerSizeImpl extends XmlComplexContentImpl implements CTMarkerSize
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTMarkerSizeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public short getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMarkerSizeImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTMarkerSizeImpl.VAL$0);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getShortValue();
        }
    }
    
    public STMarkerSize xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STMarkerSize stMarkerSize = (STMarkerSize)this.get_store().find_attribute_user(CTMarkerSizeImpl.VAL$0);
            if (stMarkerSize == null) {
                stMarkerSize = (STMarkerSize)this.get_default_attribute_value(CTMarkerSizeImpl.VAL$0);
            }
            return stMarkerSize;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTMarkerSizeImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMarkerSizeImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTMarkerSizeImpl.VAL$0);
            }
            simpleValue.setShortValue(shortValue);
        }
    }
    
    public void xsetVal(final STMarkerSize stMarkerSize) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STMarkerSize stMarkerSize2 = (STMarkerSize)this.get_store().find_attribute_user(CTMarkerSizeImpl.VAL$0);
            if (stMarkerSize2 == null) {
                stMarkerSize2 = (STMarkerSize)this.get_store().add_attribute_user(CTMarkerSizeImpl.VAL$0);
            }
            stMarkerSize2.set((XmlObject)stMarkerSize);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTMarkerSizeImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
