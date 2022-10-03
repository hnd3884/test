package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideLayout;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.SldLayoutDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SldLayoutDocumentImpl extends XmlComplexContentImpl implements SldLayoutDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName SLDLAYOUT$0;
    
    public SldLayoutDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTSlideLayout getSldLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSlideLayout ctSlideLayout = (CTSlideLayout)this.get_store().find_element_user(SldLayoutDocumentImpl.SLDLAYOUT$0, 0);
            if (ctSlideLayout == null) {
                return null;
            }
            return ctSlideLayout;
        }
    }
    
    public void setSldLayout(final CTSlideLayout ctSlideLayout) {
        this.generatedSetterHelperImpl((XmlObject)ctSlideLayout, SldLayoutDocumentImpl.SLDLAYOUT$0, 0, (short)1);
    }
    
    public CTSlideLayout addNewSldLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSlideLayout)this.get_store().add_element_user(SldLayoutDocumentImpl.SLDLAYOUT$0);
        }
    }
    
    static {
        SLDLAYOUT$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "sldLayout");
    }
}
