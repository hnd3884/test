package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPresentation;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.PresentationDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class PresentationDocumentImpl extends XmlComplexContentImpl implements PresentationDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName PRESENTATION$0;
    
    public PresentationDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTPresentation getPresentation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPresentation ctPresentation = (CTPresentation)this.get_store().find_element_user(PresentationDocumentImpl.PRESENTATION$0, 0);
            if (ctPresentation == null) {
                return null;
            }
            return ctPresentation;
        }
    }
    
    public void setPresentation(final CTPresentation ctPresentation) {
        this.generatedSetterHelperImpl((XmlObject)ctPresentation, PresentationDocumentImpl.PRESENTATION$0, 0, (short)1);
    }
    
    public CTPresentation addNewPresentation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPresentation)this.get_store().add_element_user(PresentationDocumentImpl.PRESENTATION$0);
        }
    }
    
    static {
        PRESENTATION$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "presentation");
    }
}
