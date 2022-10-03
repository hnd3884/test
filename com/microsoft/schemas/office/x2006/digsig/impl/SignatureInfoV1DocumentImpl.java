package com.microsoft.schemas.office.x2006.digsig.impl;

import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.x2006.digsig.CTSignatureInfoV1;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.x2006.digsig.SignatureInfoV1Document;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SignatureInfoV1DocumentImpl extends XmlComplexContentImpl implements SignatureInfoV1Document
{
    private static final long serialVersionUID = 1L;
    private static final QName SIGNATUREINFOV1$0;
    
    public SignatureInfoV1DocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTSignatureInfoV1 getSignatureInfoV1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSignatureInfoV1 ctSignatureInfoV1 = (CTSignatureInfoV1)this.get_store().find_element_user(SignatureInfoV1DocumentImpl.SIGNATUREINFOV1$0, 0);
            if (ctSignatureInfoV1 == null) {
                return null;
            }
            return ctSignatureInfoV1;
        }
    }
    
    public void setSignatureInfoV1(final CTSignatureInfoV1 ctSignatureInfoV1) {
        this.generatedSetterHelperImpl((XmlObject)ctSignatureInfoV1, SignatureInfoV1DocumentImpl.SIGNATUREINFOV1$0, 0, (short)1);
    }
    
    public CTSignatureInfoV1 addNewSignatureInfoV1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSignatureInfoV1)this.get_store().add_element_user(SignatureInfoV1DocumentImpl.SIGNATUREINFOV1$0);
        }
    }
    
    static {
        SIGNATUREINFOV1$0 = new QName("http://schemas.microsoft.com/office/2006/digsig", "SignatureInfoV1");
    }
}
