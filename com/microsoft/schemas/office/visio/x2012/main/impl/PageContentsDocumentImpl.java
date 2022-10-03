package com.microsoft.schemas.office.visio.x2012.main.impl;

import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.visio.x2012.main.PageContentsType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.PageContentsDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class PageContentsDocumentImpl extends XmlComplexContentImpl implements PageContentsDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName PAGECONTENTS$0;
    
    public PageContentsDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public PageContentsType getPageContents() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final PageContentsType pageContentsType = (PageContentsType)this.get_store().find_element_user(PageContentsDocumentImpl.PAGECONTENTS$0, 0);
            if (pageContentsType == null) {
                return null;
            }
            return pageContentsType;
        }
    }
    
    public void setPageContents(final PageContentsType pageContentsType) {
        this.generatedSetterHelperImpl((XmlObject)pageContentsType, PageContentsDocumentImpl.PAGECONTENTS$0, 0, (short)1);
    }
    
    public PageContentsType addNewPageContents() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (PageContentsType)this.get_store().add_element_user(PageContentsDocumentImpl.PAGECONTENTS$0);
        }
    }
    
    static {
        PAGECONTENTS$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "PageContents");
    }
}
