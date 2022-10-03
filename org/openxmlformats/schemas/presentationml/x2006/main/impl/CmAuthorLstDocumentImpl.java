package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthorList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CmAuthorLstDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CmAuthorLstDocumentImpl extends XmlComplexContentImpl implements CmAuthorLstDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName CMAUTHORLST$0;
    
    public CmAuthorLstDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTCommentAuthorList getCmAuthorLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCommentAuthorList list = (CTCommentAuthorList)this.get_store().find_element_user(CmAuthorLstDocumentImpl.CMAUTHORLST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public void setCmAuthorLst(final CTCommentAuthorList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CmAuthorLstDocumentImpl.CMAUTHORLST$0, 0, (short)1);
    }
    
    public CTCommentAuthorList addNewCmAuthorLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCommentAuthorList)this.get_store().add_element_user(CmAuthorLstDocumentImpl.CMAUTHORLST$0);
        }
    }
    
    static {
        CMAUTHORLST$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cmAuthorLst");
    }
}
