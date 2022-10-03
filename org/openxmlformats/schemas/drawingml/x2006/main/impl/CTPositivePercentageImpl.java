package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositivePercentage;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositivePercentage;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPositivePercentageImpl extends XmlComplexContentImpl implements CTPositivePercentage
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTPositivePercentageImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPositivePercentageImpl.VAL$0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPositivePercentage xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPositivePercentage)this.get_store().find_attribute_user(CTPositivePercentageImpl.VAL$0);
        }
    }
    
    public void setVal(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPositivePercentageImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPositivePercentageImpl.VAL$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetVal(final STPositivePercentage stPositivePercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositivePercentage stPositivePercentage2 = (STPositivePercentage)this.get_store().find_attribute_user(CTPositivePercentageImpl.VAL$0);
            if (stPositivePercentage2 == null) {
                stPositivePercentage2 = (STPositivePercentage)this.get_store().add_attribute_user(CTPositivePercentageImpl.VAL$0);
            }
            stPositivePercentage2.set((XmlObject)stPositivePercentage);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
