package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCommentList;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTAuthors;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComments;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCommentsImpl extends XmlComplexContentImpl implements CTComments
{
    private static final long serialVersionUID = 1L;
    private static final QName AUTHORS$0;
    private static final QName COMMENTLIST$2;
    private static final QName EXTLST$4;
    
    public CTCommentsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTAuthors getAuthors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAuthors ctAuthors = (CTAuthors)this.get_store().find_element_user(CTCommentsImpl.AUTHORS$0, 0);
            if (ctAuthors == null) {
                return null;
            }
            return ctAuthors;
        }
    }
    
    public void setAuthors(final CTAuthors ctAuthors) {
        this.generatedSetterHelperImpl((XmlObject)ctAuthors, CTCommentsImpl.AUTHORS$0, 0, (short)1);
    }
    
    public CTAuthors addNewAuthors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAuthors)this.get_store().add_element_user(CTCommentsImpl.AUTHORS$0);
        }
    }
    
    public CTCommentList getCommentList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCommentList list = (CTCommentList)this.get_store().find_element_user(CTCommentsImpl.COMMENTLIST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public void setCommentList(final CTCommentList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTCommentsImpl.COMMENTLIST$2, 0, (short)1);
    }
    
    public CTCommentList addNewCommentList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCommentList)this.get_store().add_element_user(CTCommentsImpl.COMMENTLIST$2);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTCommentsImpl.EXTLST$4, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCommentsImpl.EXTLST$4) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTCommentsImpl.EXTLST$4, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTCommentsImpl.EXTLST$4);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCommentsImpl.EXTLST$4, 0);
        }
    }
    
    static {
        AUTHORS$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "authors");
        COMMENTLIST$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "commentList");
        EXTLST$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
    }
}
