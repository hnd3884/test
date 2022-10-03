package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFootnotes;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.FootnotesDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class FootnotesDocumentImpl extends XmlComplexContentImpl implements FootnotesDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName FOOTNOTES$0;
    
    public FootnotesDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTFootnotes getFootnotes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFootnotes ctFootnotes = (CTFootnotes)this.get_store().find_element_user(FootnotesDocumentImpl.FOOTNOTES$0, 0);
            if (ctFootnotes == null) {
                return null;
            }
            return ctFootnotes;
        }
    }
    
    public void setFootnotes(final CTFootnotes ctFootnotes) {
        this.generatedSetterHelperImpl((XmlObject)ctFootnotes, FootnotesDocumentImpl.FOOTNOTES$0, 0, (short)1);
    }
    
    public CTFootnotes addNewFootnotes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFootnotes)this.get_store().add_element_user(FootnotesDocumentImpl.FOOTNOTES$0);
        }
    }
    
    static {
        FOOTNOTES$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "footnotes");
    }
}
