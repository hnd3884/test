package com.microsoft.schemas.office.visio.x2012.main.impl;

import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.visio.x2012.main.MastersType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.MastersDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class MastersDocumentImpl extends XmlComplexContentImpl implements MastersDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName MASTERS$0;
    
    public MastersDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public MastersType getMasters() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final MastersType mastersType = (MastersType)this.get_store().find_element_user(MastersDocumentImpl.MASTERS$0, 0);
            if (mastersType == null) {
                return null;
            }
            return mastersType;
        }
    }
    
    public void setMasters(final MastersType mastersType) {
        this.generatedSetterHelperImpl((XmlObject)mastersType, MastersDocumentImpl.MASTERS$0, 0, (short)1);
    }
    
    public MastersType addNewMasters() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (MastersType)this.get_store().add_element_user(MastersDocumentImpl.MASTERS$0);
        }
    }
    
    static {
        MASTERS$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Masters");
    }
}
