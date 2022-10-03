package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBooleanImpl extends XmlComplexContentImpl implements CTBoolean
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTBooleanImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public boolean getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBooleanImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBooleanImpl.VAL$0);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTBooleanImpl.VAL$0);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTBooleanImpl.VAL$0);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBooleanImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBooleanImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBooleanImpl.VAL$0);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetVal(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTBooleanImpl.VAL$0);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTBooleanImpl.VAL$0);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBooleanImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
