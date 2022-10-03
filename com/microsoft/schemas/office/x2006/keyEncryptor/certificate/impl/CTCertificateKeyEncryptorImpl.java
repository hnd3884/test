package com.microsoft.schemas.office.x2006.keyEncryptor.certificate.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.x2006.keyEncryptor.certificate.CTCertificateKeyEncryptor;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCertificateKeyEncryptorImpl extends XmlComplexContentImpl implements CTCertificateKeyEncryptor
{
    private static final long serialVersionUID = 1L;
    private static final QName ENCRYPTEDKEYVALUE$0;
    private static final QName X509CERTIFICATE$2;
    private static final QName CERTVERIFIER$4;
    
    public CTCertificateKeyEncryptorImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public byte[] getEncryptedKeyValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCertificateKeyEncryptorImpl.ENCRYPTEDKEYVALUE$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetEncryptedKeyValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_attribute_user(CTCertificateKeyEncryptorImpl.ENCRYPTEDKEYVALUE$0);
        }
    }
    
    public void setEncryptedKeyValue(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCertificateKeyEncryptorImpl.ENCRYPTEDKEYVALUE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCertificateKeyEncryptorImpl.ENCRYPTEDKEYVALUE$0);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetEncryptedKeyValue(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_attribute_user(CTCertificateKeyEncryptorImpl.ENCRYPTEDKEYVALUE$0);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_attribute_user(CTCertificateKeyEncryptorImpl.ENCRYPTEDKEYVALUE$0);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    public byte[] getX509Certificate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCertificateKeyEncryptorImpl.X509CERTIFICATE$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetX509Certificate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_attribute_user(CTCertificateKeyEncryptorImpl.X509CERTIFICATE$2);
        }
    }
    
    public void setX509Certificate(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCertificateKeyEncryptorImpl.X509CERTIFICATE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCertificateKeyEncryptorImpl.X509CERTIFICATE$2);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetX509Certificate(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_attribute_user(CTCertificateKeyEncryptorImpl.X509CERTIFICATE$2);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_attribute_user(CTCertificateKeyEncryptorImpl.X509CERTIFICATE$2);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    public byte[] getCertVerifier() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCertificateKeyEncryptorImpl.CERTVERIFIER$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetCertVerifier() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_attribute_user(CTCertificateKeyEncryptorImpl.CERTVERIFIER$4);
        }
    }
    
    public void setCertVerifier(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCertificateKeyEncryptorImpl.CERTVERIFIER$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCertificateKeyEncryptorImpl.CERTVERIFIER$4);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetCertVerifier(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_attribute_user(CTCertificateKeyEncryptorImpl.CERTVERIFIER$4);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_attribute_user(CTCertificateKeyEncryptorImpl.CERTVERIFIER$4);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    static {
        ENCRYPTEDKEYVALUE$0 = new QName("", "encryptedKeyValue");
        X509CERTIFICATE$2 = new QName("", "X509Certificate");
        CERTVERIFIER$4 = new QName("", "certVerifier");
    }
}
