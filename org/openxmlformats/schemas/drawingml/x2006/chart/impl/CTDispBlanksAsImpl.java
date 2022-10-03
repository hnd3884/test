package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.STDispBlanksAs;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDispBlanksAs;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDispBlanksAsImpl extends XmlComplexContentImpl implements CTDispBlanksAs
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTDispBlanksAsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STDispBlanksAs.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDispBlanksAsImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDispBlanksAsImpl.VAL$0);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STDispBlanksAs.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STDispBlanksAs xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDispBlanksAs stDispBlanksAs = (STDispBlanksAs)this.get_store().find_attribute_user(CTDispBlanksAsImpl.VAL$0);
            if (stDispBlanksAs == null) {
                stDispBlanksAs = (STDispBlanksAs)this.get_default_attribute_value(CTDispBlanksAsImpl.VAL$0);
            }
            return stDispBlanksAs;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDispBlanksAsImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STDispBlanksAs.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDispBlanksAsImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDispBlanksAsImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STDispBlanksAs stDispBlanksAs) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDispBlanksAs stDispBlanksAs2 = (STDispBlanksAs)this.get_store().find_attribute_user(CTDispBlanksAsImpl.VAL$0);
            if (stDispBlanksAs2 == null) {
                stDispBlanksAs2 = (STDispBlanksAs)this.get_store().add_attribute_user(CTDispBlanksAsImpl.VAL$0);
            }
            stDispBlanksAs2.set((XmlObject)stDispBlanksAs);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDispBlanksAsImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
