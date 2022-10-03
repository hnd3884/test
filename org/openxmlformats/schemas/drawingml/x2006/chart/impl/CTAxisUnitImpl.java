package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.STAxisUnit;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxisUnit;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTAxisUnitImpl extends XmlComplexContentImpl implements CTAxisUnit
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTAxisUnitImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public double getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAxisUnitImpl.VAL$0);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public STAxisUnit xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAxisUnit)this.get_store().find_attribute_user(CTAxisUnitImpl.VAL$0);
        }
    }
    
    public void setVal(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAxisUnitImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAxisUnitImpl.VAL$0);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetVal(final STAxisUnit stAxisUnit) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAxisUnit stAxisUnit2 = (STAxisUnit)this.get_store().find_attribute_user(CTAxisUnitImpl.VAL$0);
            if (stAxisUnit2 == null) {
                stAxisUnit2 = (STAxisUnit)this.get_store().add_attribute_user(CTAxisUnitImpl.VAL$0);
            }
            stAxisUnit2.set((XmlObject)stAxisUnit);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
