package com.microsoft.schemas.vml.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.vml.CTF;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFImpl extends XmlComplexContentImpl implements CTF
{
    private static final long serialVersionUID = 1L;
    private static final QName EQN$0;
    
    public CTFImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getEqn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFImpl.EQN$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetEqn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTFImpl.EQN$0);
        }
    }
    
    public boolean isSetEqn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTFImpl.EQN$0) != null;
        }
    }
    
    public void setEqn(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTFImpl.EQN$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTFImpl.EQN$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetEqn(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTFImpl.EQN$0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTFImpl.EQN$0);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetEqn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTFImpl.EQN$0);
        }
    }
    
    static {
        EQN$0 = new QName("", "eqn");
    }
}
