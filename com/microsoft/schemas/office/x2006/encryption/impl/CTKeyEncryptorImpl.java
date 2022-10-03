package com.microsoft.schemas.office.x2006.encryption.impl;

import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import com.microsoft.schemas.office.x2006.keyEncryptor.certificate.CTCertificateKeyEncryptor;
import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.x2006.keyEncryptor.password.CTPasswordKeyEncryptor;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.x2006.encryption.CTKeyEncryptor;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTKeyEncryptorImpl extends XmlComplexContentImpl implements CTKeyEncryptor
{
    private static final long serialVersionUID = 1L;
    private static final QName ENCRYPTEDPASSWORDKEY$0;
    private static final QName ENCRYPTEDCERTIFICATEKEY$2;
    private static final QName URI$4;
    
    public CTKeyEncryptorImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTPasswordKeyEncryptor getEncryptedPasswordKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPasswordKeyEncryptor ctPasswordKeyEncryptor = (CTPasswordKeyEncryptor)this.get_store().find_element_user(CTKeyEncryptorImpl.ENCRYPTEDPASSWORDKEY$0, 0);
            if (ctPasswordKeyEncryptor == null) {
                return null;
            }
            return ctPasswordKeyEncryptor;
        }
    }
    
    public boolean isSetEncryptedPasswordKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTKeyEncryptorImpl.ENCRYPTEDPASSWORDKEY$0) != 0;
        }
    }
    
    public void setEncryptedPasswordKey(final CTPasswordKeyEncryptor ctPasswordKeyEncryptor) {
        this.generatedSetterHelperImpl((XmlObject)ctPasswordKeyEncryptor, CTKeyEncryptorImpl.ENCRYPTEDPASSWORDKEY$0, 0, (short)1);
    }
    
    public CTPasswordKeyEncryptor addNewEncryptedPasswordKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPasswordKeyEncryptor)this.get_store().add_element_user(CTKeyEncryptorImpl.ENCRYPTEDPASSWORDKEY$0);
        }
    }
    
    public void unsetEncryptedPasswordKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTKeyEncryptorImpl.ENCRYPTEDPASSWORDKEY$0, 0);
        }
    }
    
    public CTCertificateKeyEncryptor getEncryptedCertificateKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCertificateKeyEncryptor ctCertificateKeyEncryptor = (CTCertificateKeyEncryptor)this.get_store().find_element_user(CTKeyEncryptorImpl.ENCRYPTEDCERTIFICATEKEY$2, 0);
            if (ctCertificateKeyEncryptor == null) {
                return null;
            }
            return ctCertificateKeyEncryptor;
        }
    }
    
    public boolean isSetEncryptedCertificateKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTKeyEncryptorImpl.ENCRYPTEDCERTIFICATEKEY$2) != 0;
        }
    }
    
    public void setEncryptedCertificateKey(final CTCertificateKeyEncryptor ctCertificateKeyEncryptor) {
        this.generatedSetterHelperImpl((XmlObject)ctCertificateKeyEncryptor, CTKeyEncryptorImpl.ENCRYPTEDCERTIFICATEKEY$2, 0, (short)1);
    }
    
    public CTCertificateKeyEncryptor addNewEncryptedCertificateKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCertificateKeyEncryptor)this.get_store().add_element_user(CTKeyEncryptorImpl.ENCRYPTEDCERTIFICATEKEY$2);
        }
    }
    
    public void unsetEncryptedCertificateKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTKeyEncryptorImpl.ENCRYPTEDCERTIFICATEKEY$2, 0);
        }
    }
    
    public Uri.Enum getUri() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTKeyEncryptorImpl.URI$4);
            if (simpleValue == null) {
                return null;
            }
            return (Uri.Enum)simpleValue.getEnumValue();
        }
    }
    
    public Uri xgetUri() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (Uri)this.get_store().find_attribute_user(CTKeyEncryptorImpl.URI$4);
        }
    }
    
    public boolean isSetUri() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTKeyEncryptorImpl.URI$4) != null;
        }
    }
    
    public void setUri(final Uri.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTKeyEncryptorImpl.URI$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTKeyEncryptorImpl.URI$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetUri(final Uri uri) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Uri uri2 = (Uri)this.get_store().find_attribute_user(CTKeyEncryptorImpl.URI$4);
            if (uri2 == null) {
                uri2 = (Uri)this.get_store().add_attribute_user(CTKeyEncryptorImpl.URI$4);
            }
            uri2.set((XmlObject)uri);
        }
    }
    
    public void unsetUri() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTKeyEncryptorImpl.URI$4);
        }
    }
    
    static {
        ENCRYPTEDPASSWORDKEY$0 = new QName("http://schemas.microsoft.com/office/2006/keyEncryptor/password", "encryptedKey");
        ENCRYPTEDCERTIFICATEKEY$2 = new QName("http://schemas.microsoft.com/office/2006/keyEncryptor/certificate", "encryptedKey");
        URI$4 = new QName("", "uri");
    }
    
    public static class UriImpl extends JavaStringEnumerationHolderEx implements Uri
    {
        private static final long serialVersionUID = 1L;
        
        public UriImpl(final SchemaType schemaType) {
            super(schemaType, false);
        }
        
        protected UriImpl(final SchemaType schemaType, final boolean b) {
            super(schemaType, b);
        }
    }
}
