package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlide;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.SldDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SldDocumentImpl extends XmlComplexContentImpl implements SldDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName SLD$0;
    
    public SldDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTSlide getSld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSlide ctSlide = (CTSlide)this.get_store().find_element_user(SldDocumentImpl.SLD$0, 0);
            if (ctSlide == null) {
                return null;
            }
            return ctSlide;
        }
    }
    
    public void setSld(final CTSlide ctSlide) {
        this.generatedSetterHelperImpl((XmlObject)ctSlide, SldDocumentImpl.SLD$0, 0, (short)1);
    }
    
    public CTSlide addNewSld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSlide)this.get_store().add_element_user(SldDocumentImpl.SLD$0);
        }
    }
    
    static {
        SLD$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "sld");
    }
}
