package com.microsoft.schemas.office.x2006.encryption.impl;

import com.microsoft.schemas.office.x2006.encryption.CTKeyEncryptors;
import com.microsoft.schemas.office.x2006.encryption.CTDataIntegrity;
import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.x2006.encryption.CTKeyData;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.x2006.encryption.CTEncryption;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTEncryptionImpl extends XmlComplexContentImpl implements CTEncryption
{
    private static final long serialVersionUID = 1L;
    private static final QName KEYDATA$0;
    private static final QName DATAINTEGRITY$2;
    private static final QName KEYENCRYPTORS$4;
    
    public CTEncryptionImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTKeyData getKeyData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTKeyData ctKeyData = (CTKeyData)this.get_store().find_element_user(CTEncryptionImpl.KEYDATA$0, 0);
            if (ctKeyData == null) {
                return null;
            }
            return ctKeyData;
        }
    }
    
    public void setKeyData(final CTKeyData ctKeyData) {
        this.generatedSetterHelperImpl((XmlObject)ctKeyData, CTEncryptionImpl.KEYDATA$0, 0, (short)1);
    }
    
    public CTKeyData addNewKeyData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTKeyData)this.get_store().add_element_user(CTEncryptionImpl.KEYDATA$0);
        }
    }
    
    public CTDataIntegrity getDataIntegrity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDataIntegrity ctDataIntegrity = (CTDataIntegrity)this.get_store().find_element_user(CTEncryptionImpl.DATAINTEGRITY$2, 0);
            if (ctDataIntegrity == null) {
                return null;
            }
            return ctDataIntegrity;
        }
    }
    
    public void setDataIntegrity(final CTDataIntegrity ctDataIntegrity) {
        this.generatedSetterHelperImpl((XmlObject)ctDataIntegrity, CTEncryptionImpl.DATAINTEGRITY$2, 0, (short)1);
    }
    
    public CTDataIntegrity addNewDataIntegrity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDataIntegrity)this.get_store().add_element_user(CTEncryptionImpl.DATAINTEGRITY$2);
        }
    }
    
    public CTKeyEncryptors getKeyEncryptors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTKeyEncryptors ctKeyEncryptors = (CTKeyEncryptors)this.get_store().find_element_user(CTEncryptionImpl.KEYENCRYPTORS$4, 0);
            if (ctKeyEncryptors == null) {
                return null;
            }
            return ctKeyEncryptors;
        }
    }
    
    public void setKeyEncryptors(final CTKeyEncryptors ctKeyEncryptors) {
        this.generatedSetterHelperImpl((XmlObject)ctKeyEncryptors, CTEncryptionImpl.KEYENCRYPTORS$4, 0, (short)1);
    }
    
    public CTKeyEncryptors addNewKeyEncryptors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTKeyEncryptors)this.get_store().add_element_user(CTEncryptionImpl.KEYENCRYPTORS$4);
        }
    }
    
    static {
        KEYDATA$0 = new QName("http://schemas.microsoft.com/office/2006/encryption", "keyData");
        DATAINTEGRITY$2 = new QName("http://schemas.microsoft.com/office/2006/encryption", "dataIntegrity");
        KEYENCRYPTORS$4 = new QName("http://schemas.microsoft.com/office/2006/encryption", "keyEncryptors");
    }
}
