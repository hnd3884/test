package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLogBase;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLogBase;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLogBaseImpl extends XmlComplexContentImpl implements CTLogBase
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTLogBaseImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public double getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLogBaseImpl.VAL$0);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public STLogBase xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLogBase)this.get_store().find_attribute_user(CTLogBaseImpl.VAL$0);
        }
    }
    
    public void setVal(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLogBaseImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLogBaseImpl.VAL$0);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetVal(final STLogBase stLogBase) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLogBase stLogBase2 = (STLogBase)this.get_store().find_attribute_user(CTLogBaseImpl.VAL$0);
            if (stLogBase2 == null) {
                stLogBase2 = (STLogBase)this.get_store().add_attribute_user(CTLogBaseImpl.VAL$0);
            }
            stLogBase2.set((XmlObject)stLogBase);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
