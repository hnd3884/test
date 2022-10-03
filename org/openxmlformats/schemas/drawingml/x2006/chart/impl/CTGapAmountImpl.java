package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.STGapAmount;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTGapAmount;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTGapAmountImpl extends XmlComplexContentImpl implements CTGapAmount
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTGapAmountImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGapAmountImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTGapAmountImpl.VAL$0);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STGapAmount xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STGapAmount stGapAmount = (STGapAmount)this.get_store().find_attribute_user(CTGapAmountImpl.VAL$0);
            if (stGapAmount == null) {
                stGapAmount = (STGapAmount)this.get_default_attribute_value(CTGapAmountImpl.VAL$0);
            }
            return stGapAmount;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGapAmountImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGapAmountImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGapAmountImpl.VAL$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetVal(final STGapAmount stGapAmount) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STGapAmount stGapAmount2 = (STGapAmount)this.get_store().find_attribute_user(CTGapAmountImpl.VAL$0);
            if (stGapAmount2 == null) {
                stGapAmount2 = (STGapAmount)this.get_store().add_attribute_user(CTGapAmountImpl.VAL$0);
            }
            stGapAmount2.set((XmlObject)stGapAmount);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGapAmountImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
