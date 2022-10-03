package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CmLstDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CmLstDocumentImpl extends XmlComplexContentImpl implements CmLstDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName CMLST$0;
    
    public CmLstDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTCommentList getCmLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCommentList list = (CTCommentList)this.get_store().find_element_user(CmLstDocumentImpl.CMLST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public void setCmLst(final CTCommentList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CmLstDocumentImpl.CMLST$0, 0, (short)1);
    }
    
    public CTCommentList addNewCmLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCommentList)this.get_store().add_element_user(CmLstDocumentImpl.CMLST$0);
        }
    }
    
    static {
        CMLST$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cmLst");
    }
}
