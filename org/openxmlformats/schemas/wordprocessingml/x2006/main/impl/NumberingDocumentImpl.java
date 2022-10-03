package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumbering;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.NumberingDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class NumberingDocumentImpl extends XmlComplexContentImpl implements NumberingDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName NUMBERING$0;
    
    public NumberingDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTNumbering getNumbering() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumbering ctNumbering = (CTNumbering)this.get_store().find_element_user(NumberingDocumentImpl.NUMBERING$0, 0);
            if (ctNumbering == null) {
                return null;
            }
            return ctNumbering;
        }
    }
    
    public void setNumbering(final CTNumbering ctNumbering) {
        this.generatedSetterHelperImpl((XmlObject)ctNumbering, NumberingDocumentImpl.NUMBERING$0, 0, (short)1);
    }
    
    public CTNumbering addNewNumbering() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumbering)this.get_store().add_element_user(NumberingDocumentImpl.NUMBERING$0);
        }
    }
    
    static {
        NUMBERING$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "numbering");
    }
}
