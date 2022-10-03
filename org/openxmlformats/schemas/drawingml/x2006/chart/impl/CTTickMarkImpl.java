package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.STTickMark;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTickMark;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTickMarkImpl extends XmlComplexContentImpl implements CTTickMark
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTTickMarkImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STTickMark.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTickMarkImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTickMarkImpl.VAL$0);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STTickMark.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTickMark xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTickMark stTickMark = (STTickMark)this.get_store().find_attribute_user(CTTickMarkImpl.VAL$0);
            if (stTickMark == null) {
                stTickMark = (STTickMark)this.get_default_attribute_value(CTTickMarkImpl.VAL$0);
            }
            return stTickMark;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTickMarkImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STTickMark.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTickMarkImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTickMarkImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STTickMark stTickMark) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTickMark stTickMark2 = (STTickMark)this.get_store().find_attribute_user(CTTickMarkImpl.VAL$0);
            if (stTickMark2 == null) {
                stTickMark2 = (STTickMark)this.get_store().add_attribute_user(CTTickMarkImpl.VAL$0);
            }
            stTickMark2.set((XmlObject)stTickMark);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTickMarkImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
