package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLayoutMode;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLayoutMode;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLayoutModeImpl extends XmlComplexContentImpl implements CTLayoutMode
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTLayoutModeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STLayoutMode.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLayoutModeImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTLayoutModeImpl.VAL$0);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STLayoutMode.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STLayoutMode xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLayoutMode stLayoutMode = (STLayoutMode)this.get_store().find_attribute_user(CTLayoutModeImpl.VAL$0);
            if (stLayoutMode == null) {
                stLayoutMode = (STLayoutMode)this.get_default_attribute_value(CTLayoutModeImpl.VAL$0);
            }
            return stLayoutMode;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLayoutModeImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STLayoutMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLayoutModeImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLayoutModeImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STLayoutMode stLayoutMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLayoutMode stLayoutMode2 = (STLayoutMode)this.get_store().find_attribute_user(CTLayoutModeImpl.VAL$0);
            if (stLayoutMode2 == null) {
                stLayoutMode2 = (STLayoutMode)this.get_store().add_attribute_user(CTLayoutModeImpl.VAL$0);
            }
            stLayoutMode2.set((XmlObject)stLayoutMode);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLayoutModeImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
