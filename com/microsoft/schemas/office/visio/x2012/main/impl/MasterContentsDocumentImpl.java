package com.microsoft.schemas.office.visio.x2012.main.impl;

import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.visio.x2012.main.PageContentsType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.MasterContentsDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class MasterContentsDocumentImpl extends XmlComplexContentImpl implements MasterContentsDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName MASTERCONTENTS$0;
    
    public MasterContentsDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public PageContentsType getMasterContents() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final PageContentsType pageContentsType = (PageContentsType)this.get_store().find_element_user(MasterContentsDocumentImpl.MASTERCONTENTS$0, 0);
            if (pageContentsType == null) {
                return null;
            }
            return pageContentsType;
        }
    }
    
    public void setMasterContents(final PageContentsType pageContentsType) {
        this.generatedSetterHelperImpl((XmlObject)pageContentsType, MasterContentsDocumentImpl.MASTERCONTENTS$0, 0, (short)1);
    }
    
    public PageContentsType addNewMasterContents() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (PageContentsType)this.get_store().add_element_user(MasterContentsDocumentImpl.MASTERCONTENTS$0);
        }
    }
    
    static {
        MASTERCONTENTS$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "MasterContents");
    }
}
