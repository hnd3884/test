package com.microsoft.schemas.office.visio.x2012.main.impl;

import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.visio.x2012.main.PagesType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.PagesDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class PagesDocumentImpl extends XmlComplexContentImpl implements PagesDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName PAGES$0;
    
    public PagesDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public PagesType getPages() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final PagesType pagesType = (PagesType)this.get_store().find_element_user(PagesDocumentImpl.PAGES$0, 0);
            if (pagesType == null) {
                return null;
            }
            return pagesType;
        }
    }
    
    public void setPages(final PagesType pagesType) {
        this.generatedSetterHelperImpl((XmlObject)pagesType, PagesDocumentImpl.PAGES$0, 0, (short)1);
    }
    
    public PagesType addNewPages() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (PagesType)this.get_store().add_element_user(PagesDocumentImpl.PAGES$0);
        }
    }
    
    static {
        PAGES$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Pages");
    }
}
