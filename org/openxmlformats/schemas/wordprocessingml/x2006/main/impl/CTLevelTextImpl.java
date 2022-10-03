package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLevelText;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLevelTextImpl extends XmlComplexContentImpl implements CTLevelText
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    private static final QName NULL$2;
    
    public CTLevelTextImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLevelTextImpl.VAL$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTLevelTextImpl.VAL$0);
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLevelTextImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLevelTextImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLevelTextImpl.VAL$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetVal(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTLevelTextImpl.VAL$0);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTLevelTextImpl.VAL$0);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLevelTextImpl.VAL$0);
        }
    }
    
    public STOnOff.Enum getNull() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLevelTextImpl.NULL$2);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetNull() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTLevelTextImpl.NULL$2);
        }
    }
    
    public boolean isSetNull() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLevelTextImpl.NULL$2) != null;
        }
    }
    
    public void setNull(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLevelTextImpl.NULL$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLevelTextImpl.NULL$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetNull(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTLevelTextImpl.NULL$2);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTLevelTextImpl.NULL$2);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetNull() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLevelTextImpl.NULL$2);
        }
    }
    
    static {
        VAL$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "val");
        NULL$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "null");
    }
}
