package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPercentage;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPercentageImpl extends XmlComplexContentImpl implements CTPercentage
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTPercentageImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPercentageImpl.VAL$0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPercentage xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPercentage)this.get_store().find_attribute_user(CTPercentageImpl.VAL$0);
        }
    }
    
    public void setVal(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPercentageImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPercentageImpl.VAL$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetVal(final STPercentage stPercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage2 = (STPercentage)this.get_store().find_attribute_user(CTPercentageImpl.VAL$0);
            if (stPercentage2 == null) {
                stPercentage2 = (STPercentage)this.get_store().add_attribute_user(CTPercentageImpl.VAL$0);
            }
            stPercentage2.set((XmlObject)stPercentage);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
