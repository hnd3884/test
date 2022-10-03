package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTOnOffImpl extends XmlComplexContentImpl implements CTOnOff
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTOnOffImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STOnOff.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOnOffImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTOnOffImpl.VAL$0);
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOnOffImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOnOffImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOnOffImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTOnOffImpl.VAL$0);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTOnOffImpl.VAL$0);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOnOffImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
    }
}
