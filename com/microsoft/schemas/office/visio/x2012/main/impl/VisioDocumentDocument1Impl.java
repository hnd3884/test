package com.microsoft.schemas.office.visio.x2012.main.impl;

import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.visio.x2012.main.VisioDocumentType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.VisioDocumentDocument1;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class VisioDocumentDocument1Impl extends XmlComplexContentImpl implements VisioDocumentDocument1
{
    private static final long serialVersionUID = 1L;
    private static final QName VISIODOCUMENT$0;
    
    public VisioDocumentDocument1Impl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public VisioDocumentType getVisioDocument() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final VisioDocumentType visioDocumentType = (VisioDocumentType)this.get_store().find_element_user(VisioDocumentDocument1Impl.VISIODOCUMENT$0, 0);
            if (visioDocumentType == null) {
                return null;
            }
            return visioDocumentType;
        }
    }
    
    public void setVisioDocument(final VisioDocumentType visioDocumentType) {
        this.generatedSetterHelperImpl((XmlObject)visioDocumentType, VisioDocumentDocument1Impl.VISIODOCUMENT$0, 0, (short)1);
    }
    
    public VisioDocumentType addNewVisioDocument() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (VisioDocumentType)this.get_store().add_element_user(VisioDocumentDocument1Impl.VISIODOCUMENT$0);
        }
    }
    
    static {
        VISIODOCUMENT$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "VisioDocument");
    }
}
