package com.microsoft.schemas.office.x2006.encryption.impl;

import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.x2006.encryption.CTEncryption;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.x2006.encryption.EncryptionDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class EncryptionDocumentImpl extends XmlComplexContentImpl implements EncryptionDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName ENCRYPTION$0;
    
    public EncryptionDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTEncryption getEncryption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEncryption ctEncryption = (CTEncryption)this.get_store().find_element_user(EncryptionDocumentImpl.ENCRYPTION$0, 0);
            if (ctEncryption == null) {
                return null;
            }
            return ctEncryption;
        }
    }
    
    public void setEncryption(final CTEncryption ctEncryption) {
        this.generatedSetterHelperImpl((XmlObject)ctEncryption, EncryptionDocumentImpl.ENCRYPTION$0, 0, (short)1);
    }
    
    public CTEncryption addNewEncryption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEncryption)this.get_store().add_element_user(EncryptionDocumentImpl.ENCRYPTION$0);
        }
    }
    
    static {
        ENCRYPTION$0 = new QName("http://schemas.microsoft.com/office/2006/encryption", "encryption");
    }
}
