package org.w3.x2000.x09.xmldsig.impl;

import org.apache.xmlbeans.XmlObject;
import org.w3.x2000.x09.xmldsig.SignatureType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.w3.x2000.x09.xmldsig.SignatureDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SignatureDocumentImpl extends XmlComplexContentImpl implements SignatureDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName SIGNATURE$0;
    
    public SignatureDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public SignatureType getSignature() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SignatureType signatureType = (SignatureType)this.get_store().find_element_user(SignatureDocumentImpl.SIGNATURE$0, 0);
            if (signatureType == null) {
                return null;
            }
            return signatureType;
        }
    }
    
    public void setSignature(final SignatureType signatureType) {
        this.generatedSetterHelperImpl((XmlObject)signatureType, SignatureDocumentImpl.SIGNATURE$0, 0, (short)1);
    }
    
    public SignatureType addNewSignature() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (SignatureType)this.get_store().add_element_user(SignatureDocumentImpl.SIGNATURE$0);
        }
    }
    
    static {
        SIGNATURE$0 = new QName("http://www.w3.org/2000/09/xmldsig#", "Signature");
    }
}
