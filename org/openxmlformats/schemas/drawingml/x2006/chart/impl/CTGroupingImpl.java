package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.STGrouping;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTGrouping;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTGroupingImpl extends XmlComplexContentImpl implements CTGrouping
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTGroupingImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STGrouping.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupingImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTGroupingImpl.VAL$0);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STGrouping.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STGrouping xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STGrouping stGrouping = (STGrouping)this.get_store().find_attribute_user(CTGroupingImpl.VAL$0);
            if (stGrouping == null) {
                stGrouping = (STGrouping)this.get_default_attribute_value(CTGroupingImpl.VAL$0);
            }
            return stGrouping;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupingImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STGrouping.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupingImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupingImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STGrouping stGrouping) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STGrouping stGrouping2 = (STGrouping)this.get_store().find_attribute_user(CTGroupingImpl.VAL$0);
            if (stGrouping2 == null) {
                stGrouping2 = (STGrouping)this.get_store().add_attribute_user(CTGroupingImpl.VAL$0);
            }
            stGrouping2.set((XmlObject)stGrouping);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupingImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
