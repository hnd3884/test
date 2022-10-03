package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesSlide;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.NotesDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class NotesDocumentImpl extends XmlComplexContentImpl implements NotesDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName NOTES$0;
    
    public NotesDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTNotesSlide getNotes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNotesSlide ctNotesSlide = (CTNotesSlide)this.get_store().find_element_user(NotesDocumentImpl.NOTES$0, 0);
            if (ctNotesSlide == null) {
                return null;
            }
            return ctNotesSlide;
        }
    }
    
    public void setNotes(final CTNotesSlide ctNotesSlide) {
        this.generatedSetterHelperImpl((XmlObject)ctNotesSlide, NotesDocumentImpl.NOTES$0, 0, (short)1);
    }
    
    public CTNotesSlide addNewNotes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNotesSlide)this.get_store().add_element_user(NotesDocumentImpl.NOTES$0);
        }
    }
    
    static {
        NOTES$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "notes");
    }
}
