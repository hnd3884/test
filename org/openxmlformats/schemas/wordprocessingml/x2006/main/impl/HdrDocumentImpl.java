package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtr;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.HdrDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class HdrDocumentImpl extends XmlComplexContentImpl implements HdrDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName HDR$0;
    
    public HdrDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTHdrFtr getHdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHdrFtr ctHdrFtr = (CTHdrFtr)this.get_store().find_element_user(HdrDocumentImpl.HDR$0, 0);
            if (ctHdrFtr == null) {
                return null;
            }
            return ctHdrFtr;
        }
    }
    
    public void setHdr(final CTHdrFtr ctHdrFtr) {
        this.generatedSetterHelperImpl((XmlObject)ctHdrFtr, HdrDocumentImpl.HDR$0, 0, (short)1);
    }
    
    public CTHdrFtr addNewHdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHdrFtr)this.get_store().add_element_user(HdrDocumentImpl.HDR$0);
        }
    }
    
    static {
        HDR$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hdr");
    }
}
