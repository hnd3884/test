package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.STMarkerStyle;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarkerStyle;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTMarkerStyleImpl extends XmlComplexContentImpl implements CTMarkerStyle
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTMarkerStyleImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STMarkerStyle.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMarkerStyleImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STMarkerStyle.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STMarkerStyle xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STMarkerStyle)this.get_store().find_attribute_user(CTMarkerStyleImpl.VAL$0);
        }
    }
    
    public void setVal(final STMarkerStyle.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMarkerStyleImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTMarkerStyleImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STMarkerStyle stMarkerStyle) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STMarkerStyle stMarkerStyle2 = (STMarkerStyle)this.get_store().find_attribute_user(CTMarkerStyleImpl.VAL$0);
            if (stMarkerStyle2 == null) {
                stMarkerStyle2 = (STMarkerStyle)this.get_store().add_attribute_user(CTMarkerStyleImpl.VAL$0);
            }
            stMarkerStyle2.set((XmlObject)stMarkerStyle);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
