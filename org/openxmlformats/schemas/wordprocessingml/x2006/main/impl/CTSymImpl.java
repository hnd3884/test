package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShortHexNumber;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSym;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSymImpl extends XmlComplexContentImpl implements CTSym
{
    private static final long serialVersionUID = 1L;
    private static final QName FONT$0;
    private static final QName CHAR$2;
    
    public CTSymImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSymImpl.FONT$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTSymImpl.FONT$0);
        }
    }
    
    public boolean isSetFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSymImpl.FONT$0) != null;
        }
    }
    
    public void setFont(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSymImpl.FONT$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSymImpl.FONT$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFont(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTSymImpl.FONT$0);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTSymImpl.FONT$0);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    public void unsetFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSymImpl.FONT$0);
        }
    }
    
    public byte[] getChar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSymImpl.CHAR$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STShortHexNumber xgetChar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STShortHexNumber)this.get_store().find_attribute_user(CTSymImpl.CHAR$2);
        }
    }
    
    public boolean isSetChar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSymImpl.CHAR$2) != null;
        }
    }
    
    public void setChar(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSymImpl.CHAR$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSymImpl.CHAR$2);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetChar(final STShortHexNumber stShortHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STShortHexNumber stShortHexNumber2 = (STShortHexNumber)this.get_store().find_attribute_user(CTSymImpl.CHAR$2);
            if (stShortHexNumber2 == null) {
                stShortHexNumber2 = (STShortHexNumber)this.get_store().add_attribute_user(CTSymImpl.CHAR$2);
            }
            stShortHexNumber2.set((XmlObject)stShortHexNumber);
        }
    }
    
    public void unsetChar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSymImpl.CHAR$2);
        }
    }
    
    static {
        FONT$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "font");
        CHAR$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "char");
    }
}
