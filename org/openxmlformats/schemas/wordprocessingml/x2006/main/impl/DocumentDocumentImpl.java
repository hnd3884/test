package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.DocumentDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class DocumentDocumentImpl extends XmlComplexContentImpl implements DocumentDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName DOCUMENT$0;
    
    public DocumentDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTDocument1 getDocument() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDocument1 ctDocument1 = (CTDocument1)this.get_store().find_element_user(DocumentDocumentImpl.DOCUMENT$0, 0);
            if (ctDocument1 == null) {
                return null;
            }
            return ctDocument1;
        }
    }
    
    public void setDocument(final CTDocument1 ctDocument1) {
        this.generatedSetterHelperImpl((XmlObject)ctDocument1, DocumentDocumentImpl.DOCUMENT$0, 0, (short)1);
    }
    
    public CTDocument1 addNewDocument() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDocument1)this.get_store().add_element_user(DocumentDocumentImpl.DOCUMENT$0);
        }
    }
    
    static {
        DOCUMENT$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "document");
    }
}
