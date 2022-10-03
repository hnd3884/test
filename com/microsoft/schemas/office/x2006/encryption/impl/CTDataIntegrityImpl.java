package com.microsoft.schemas.office.x2006.encryption.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.x2006.encryption.CTDataIntegrity;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDataIntegrityImpl extends XmlComplexContentImpl implements CTDataIntegrity
{
    private static final long serialVersionUID = 1L;
    private static final QName ENCRYPTEDHMACKEY$0;
    private static final QName ENCRYPTEDHMACVALUE$2;
    
    public CTDataIntegrityImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public byte[] getEncryptedHmacKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataIntegrityImpl.ENCRYPTEDHMACKEY$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetEncryptedHmacKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_attribute_user(CTDataIntegrityImpl.ENCRYPTEDHMACKEY$0);
        }
    }
    
    public void setEncryptedHmacKey(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataIntegrityImpl.ENCRYPTEDHMACKEY$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataIntegrityImpl.ENCRYPTEDHMACKEY$0);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetEncryptedHmacKey(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_attribute_user(CTDataIntegrityImpl.ENCRYPTEDHMACKEY$0);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_attribute_user(CTDataIntegrityImpl.ENCRYPTEDHMACKEY$0);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    public byte[] getEncryptedHmacValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataIntegrityImpl.ENCRYPTEDHMACVALUE$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetEncryptedHmacValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_attribute_user(CTDataIntegrityImpl.ENCRYPTEDHMACVALUE$2);
        }
    }
    
    public void setEncryptedHmacValue(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataIntegrityImpl.ENCRYPTEDHMACVALUE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataIntegrityImpl.ENCRYPTEDHMACVALUE$2);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetEncryptedHmacValue(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_attribute_user(CTDataIntegrityImpl.ENCRYPTEDHMACVALUE$2);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_attribute_user(CTDataIntegrityImpl.ENCRYPTEDHMACVALUE$2);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    static {
        ENCRYPTEDHMACKEY$0 = new QName("", "encryptedHmacKey");
        ENCRYPTEDHMACVALUE$2 = new QName("", "encryptedHmacValue");
    }
}
