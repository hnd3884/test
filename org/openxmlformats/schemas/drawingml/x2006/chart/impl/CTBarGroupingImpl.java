package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarGrouping;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarGrouping;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBarGroupingImpl extends XmlComplexContentImpl implements CTBarGrouping
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTBarGroupingImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STBarGrouping.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBarGroupingImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBarGroupingImpl.VAL$0);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STBarGrouping.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STBarGrouping xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBarGrouping stBarGrouping = (STBarGrouping)this.get_store().find_attribute_user(CTBarGroupingImpl.VAL$0);
            if (stBarGrouping == null) {
                stBarGrouping = (STBarGrouping)this.get_default_attribute_value(CTBarGroupingImpl.VAL$0);
            }
            return stBarGrouping;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBarGroupingImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STBarGrouping.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBarGroupingImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBarGroupingImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STBarGrouping stBarGrouping) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBarGrouping stBarGrouping2 = (STBarGrouping)this.get_store().find_attribute_user(CTBarGroupingImpl.VAL$0);
            if (stBarGrouping2 == null) {
                stBarGrouping2 = (STBarGrouping)this.get_store().add_attribute_user(CTBarGroupingImpl.VAL$0);
            }
            stBarGrouping2.set((XmlObject)stBarGrouping);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBarGroupingImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
