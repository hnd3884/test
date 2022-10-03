package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComment;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCommentList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCommentListImpl extends XmlComplexContentImpl implements CTCommentList
{
    private static final long serialVersionUID = 1L;
    private static final QName COMMENT$0;
    
    public CTCommentListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTComment> getCommentList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CommentList extends AbstractList<CTComment>
            {
                @Override
                public CTComment get(final int n) {
                    return CTCommentListImpl.this.getCommentArray(n);
                }
                
                @Override
                public CTComment set(final int n, final CTComment ctComment) {
                    final CTComment commentArray = CTCommentListImpl.this.getCommentArray(n);
                    CTCommentListImpl.this.setCommentArray(n, ctComment);
                    return commentArray;
                }
                
                @Override
                public void add(final int n, final CTComment ctComment) {
                    CTCommentListImpl.this.insertNewComment(n).set((XmlObject)ctComment);
                }
                
                @Override
                public CTComment remove(final int n) {
                    final CTComment commentArray = CTCommentListImpl.this.getCommentArray(n);
                    CTCommentListImpl.this.removeComment(n);
                    return commentArray;
                }
                
                @Override
                public int size() {
                    return CTCommentListImpl.this.sizeOfCommentArray();
                }
            }
            return new CommentList();
        }
    }
    
    @Deprecated
    public CTComment[] getCommentArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTCommentListImpl.COMMENT$0, (List)list);
            final CTComment[] array = new CTComment[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTComment getCommentArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTComment ctComment = (CTComment)this.get_store().find_element_user(CTCommentListImpl.COMMENT$0, n);
            if (ctComment == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctComment;
        }
    }
    
    public int sizeOfCommentArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCommentListImpl.COMMENT$0);
        }
    }
    
    public void setCommentArray(final CTComment[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTCommentListImpl.COMMENT$0);
    }
    
    public void setCommentArray(final int n, final CTComment ctComment) {
        this.generatedSetterHelperImpl((XmlObject)ctComment, CTCommentListImpl.COMMENT$0, n, (short)2);
    }
    
    public CTComment insertNewComment(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTComment)this.get_store().insert_element_user(CTCommentListImpl.COMMENT$0, n);
        }
    }
    
    public CTComment addNewComment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTComment)this.get_store().add_element_user(CTCommentListImpl.COMMENT$0);
        }
    }
    
    public void removeComment(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCommentListImpl.COMMENT$0, n);
        }
    }
    
    static {
        COMMENT$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "comment");
    }
}
