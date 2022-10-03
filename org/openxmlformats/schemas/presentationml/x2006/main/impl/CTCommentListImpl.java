package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTComment;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCommentListImpl extends XmlComplexContentImpl implements CTCommentList
{
    private static final long serialVersionUID = 1L;
    private static final QName CM$0;
    
    public CTCommentListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTComment> getCmList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CmList extends AbstractList<CTComment>
            {
                @Override
                public CTComment get(final int n) {
                    return CTCommentListImpl.this.getCmArray(n);
                }
                
                @Override
                public CTComment set(final int n, final CTComment ctComment) {
                    final CTComment cmArray = CTCommentListImpl.this.getCmArray(n);
                    CTCommentListImpl.this.setCmArray(n, ctComment);
                    return cmArray;
                }
                
                @Override
                public void add(final int n, final CTComment ctComment) {
                    CTCommentListImpl.this.insertNewCm(n).set((XmlObject)ctComment);
                }
                
                @Override
                public CTComment remove(final int n) {
                    final CTComment cmArray = CTCommentListImpl.this.getCmArray(n);
                    CTCommentListImpl.this.removeCm(n);
                    return cmArray;
                }
                
                @Override
                public int size() {
                    return CTCommentListImpl.this.sizeOfCmArray();
                }
            }
            return new CmList();
        }
    }
    
    @Deprecated
    public CTComment[] getCmArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTCommentListImpl.CM$0, (List)list);
            final CTComment[] array = new CTComment[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTComment getCmArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTComment ctComment = (CTComment)this.get_store().find_element_user(CTCommentListImpl.CM$0, n);
            if (ctComment == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctComment;
        }
    }
    
    public int sizeOfCmArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCommentListImpl.CM$0);
        }
    }
    
    public void setCmArray(final CTComment[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTCommentListImpl.CM$0);
    }
    
    public void setCmArray(final int n, final CTComment ctComment) {
        this.generatedSetterHelperImpl((XmlObject)ctComment, CTCommentListImpl.CM$0, n, (short)2);
    }
    
    public CTComment insertNewCm(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTComment)this.get_store().insert_element_user(CTCommentListImpl.CM$0, n);
        }
    }
    
    public CTComment addNewCm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTComment)this.get_store().add_element_user(CTCommentListImpl.CM$0);
        }
    }
    
    public void removeCm(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCommentListImpl.CM$0, n);
        }
    }
    
    static {
        CM$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cm");
    }
}
