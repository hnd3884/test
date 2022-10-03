package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveFixedPercentage;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveFixedPercentage;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPositiveFixedPercentageImpl extends XmlComplexContentImpl implements CTPositiveFixedPercentage
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTPositiveFixedPercentageImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPositiveFixedPercentageImpl.VAL$0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPositiveFixedPercentage xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPositiveFixedPercentage)this.get_store().find_attribute_user(CTPositiveFixedPercentageImpl.VAL$0);
        }
    }
    
    public void setVal(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPositiveFixedPercentageImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPositiveFixedPercentageImpl.VAL$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetVal(final STPositiveFixedPercentage stPositiveFixedPercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveFixedPercentage stPositiveFixedPercentage2 = (STPositiveFixedPercentage)this.get_store().find_attribute_user(CTPositiveFixedPercentageImpl.VAL$0);
            if (stPositiveFixedPercentage2 == null) {
                stPositiveFixedPercentage2 = (STPositiveFixedPercentage)this.get_store().add_attribute_user(CTPositiveFixedPercentageImpl.VAL$0);
            }
            stPositiveFixedPercentage2.set((XmlObject)stPositiveFixedPercentage);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
