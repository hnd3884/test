package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesMaster;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.NotesMasterDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class NotesMasterDocumentImpl extends XmlComplexContentImpl implements NotesMasterDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName NOTESMASTER$0;
    
    public NotesMasterDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTNotesMaster getNotesMaster() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNotesMaster ctNotesMaster = (CTNotesMaster)this.get_store().find_element_user(NotesMasterDocumentImpl.NOTESMASTER$0, 0);
            if (ctNotesMaster == null) {
                return null;
            }
            return ctNotesMaster;
        }
    }
    
    public void setNotesMaster(final CTNotesMaster ctNotesMaster) {
        this.generatedSetterHelperImpl((XmlObject)ctNotesMaster, NotesMasterDocumentImpl.NOTESMASTER$0, 0, (short)1);
    }
    
    public CTNotesMaster addNewNotesMaster() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNotesMaster)this.get_store().add_element_user(NotesMasterDocumentImpl.NOTESMASTER$0);
        }
    }
    
    static {
        NOTESMASTER$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "notesMaster");
    }
}
