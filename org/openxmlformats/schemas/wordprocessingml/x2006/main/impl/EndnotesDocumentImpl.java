package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEndnotes;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.EndnotesDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class EndnotesDocumentImpl extends XmlComplexContentImpl implements EndnotesDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName ENDNOTES$0;
    
    public EndnotesDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTEndnotes getEndnotes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEndnotes ctEndnotes = (CTEndnotes)this.get_store().find_element_user(EndnotesDocumentImpl.ENDNOTES$0, 0);
            if (ctEndnotes == null) {
                return null;
            }
            return ctEndnotes;
        }
    }
    
    public void setEndnotes(final CTEndnotes ctEndnotes) {
        this.generatedSetterHelperImpl((XmlObject)ctEndnotes, EndnotesDocumentImpl.ENDNOTES$0, 0, (short)1);
    }
    
    public CTEndnotes addNewEndnotes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEndnotes)this.get_store().add_element_user(EndnotesDocumentImpl.ENDNOTES$0);
        }
    }
    
    static {
        ENDNOTES$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "endnotes");
    }
}
