package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSst;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.SstDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SstDocumentImpl extends XmlComplexContentImpl implements SstDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName SST$0;
    
    public SstDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTSst getSst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSst ctSst = (CTSst)this.get_store().find_element_user(SstDocumentImpl.SST$0, 0);
            if (ctSst == null) {
                return null;
            }
            return ctSst;
        }
    }
    
    public void setSst(final CTSst ctSst) {
        this.generatedSetterHelperImpl((XmlObject)ctSst, SstDocumentImpl.SST$0, 0, (short)1);
    }
    
    public CTSst addNewSst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSst)this.get_store().add_element_user(SstDocumentImpl.SST$0);
        }
    }
    
    static {
        SST$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sst");
    }
}
