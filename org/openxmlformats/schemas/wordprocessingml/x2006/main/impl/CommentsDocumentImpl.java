package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTComments;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CommentsDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CommentsDocumentImpl extends XmlComplexContentImpl implements CommentsDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName COMMENTS$0;
    
    public CommentsDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTComments getComments() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTComments ctComments = (CTComments)this.get_store().find_element_user(CommentsDocumentImpl.COMMENTS$0, 0);
            if (ctComments == null) {
                return null;
            }
            return ctComments;
        }
    }
    
    public void setComments(final CTComments ctComments) {
        this.generatedSetterHelperImpl((XmlObject)ctComments, CommentsDocumentImpl.COMMENTS$0, 0, (short)1);
    }
    
    public CTComments addNewComments() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTComments)this.get_store().add_element_user(CommentsDocumentImpl.COMMENTS$0);
        }
    }
    
    static {
        COMMENTS$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "comments");
    }
}
