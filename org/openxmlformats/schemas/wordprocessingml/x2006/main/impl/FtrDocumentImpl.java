package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtr;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.FtrDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class FtrDocumentImpl extends XmlComplexContentImpl implements FtrDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName FTR$0;
    
    public FtrDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTHdrFtr getFtr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHdrFtr ctHdrFtr = (CTHdrFtr)this.get_store().find_element_user(FtrDocumentImpl.FTR$0, 0);
            if (ctHdrFtr == null) {
                return null;
            }
            return ctHdrFtr;
        }
    }
    
    public void setFtr(final CTHdrFtr ctHdrFtr) {
        this.generatedSetterHelperImpl((XmlObject)ctHdrFtr, FtrDocumentImpl.FTR$0, 0, (short)1);
    }
    
    public CTHdrFtr addNewFtr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHdrFtr)this.get_store().add_element_user(FtrDocumentImpl.FTR$0);
        }
    }
    
    static {
        FTR$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ftr");
    }
}
