package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STFixedPercentage;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFixedPercentage;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFixedPercentageImpl extends XmlComplexContentImpl implements CTFixedPercentage
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTFixedPercentageImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFixedPercentageImpl.VAL$0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STFixedPercentage xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STFixedPercentage)this.get_store().find_attribute_user(CTFixedPercentageImpl.VAL$0);
        }
    }
    
    public void setVal(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFixedPercentageImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFixedPercentageImpl.VAL$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetVal(final STFixedPercentage stFixedPercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFixedPercentage stFixedPercentage2 = (STFixedPercentage)this.get_store().find_attribute_user(CTFixedPercentageImpl.VAL$0);
            if (stFixedPercentage2 == null) {
                stFixedPercentage2 = (STFixedPercentage)this.get_store().add_attribute_user(CTFixedPercentageImpl.VAL$0);
            }
            stFixedPercentage2.set((XmlObject)stFixedPercentage);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
