package org.openxmlformats.schemas.xpackage.x2006.digitalSignature.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.CTSignatureTime;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.SignatureTimeDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SignatureTimeDocumentImpl extends XmlComplexContentImpl implements SignatureTimeDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName SIGNATURETIME$0;
    
    public SignatureTimeDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTSignatureTime getSignatureTime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSignatureTime ctSignatureTime = (CTSignatureTime)this.get_store().find_element_user(SignatureTimeDocumentImpl.SIGNATURETIME$0, 0);
            if (ctSignatureTime == null) {
                return null;
            }
            return ctSignatureTime;
        }
    }
    
    public void setSignatureTime(final CTSignatureTime ctSignatureTime) {
        this.generatedSetterHelperImpl((XmlObject)ctSignatureTime, SignatureTimeDocumentImpl.SIGNATURETIME$0, 0, (short)1);
    }
    
    public CTSignatureTime addNewSignatureTime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSignatureTime)this.get_store().add_element_user(SignatureTimeDocumentImpl.SIGNATURETIME$0);
        }
    }
    
    static {
        SIGNATURETIME$0 = new QName("http://schemas.openxmlformats.org/package/2006/digital-signature", "SignatureTime");
    }
}
