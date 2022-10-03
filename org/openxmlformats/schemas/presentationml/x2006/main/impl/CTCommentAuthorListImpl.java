package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthor;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthorList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCommentAuthorListImpl extends XmlComplexContentImpl implements CTCommentAuthorList
{
    private static final long serialVersionUID = 1L;
    private static final QName CMAUTHOR$0;
    
    public CTCommentAuthorListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTCommentAuthor> getCmAuthorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CmAuthorList extends AbstractList<CTCommentAuthor>
            {
                @Override
                public CTCommentAuthor get(final int n) {
                    return CTCommentAuthorListImpl.this.getCmAuthorArray(n);
                }
                
                @Override
                public CTCommentAuthor set(final int n, final CTCommentAuthor ctCommentAuthor) {
                    final CTCommentAuthor cmAuthorArray = CTCommentAuthorListImpl.this.getCmAuthorArray(n);
                    CTCommentAuthorListImpl.this.setCmAuthorArray(n, ctCommentAuthor);
                    return cmAuthorArray;
                }
                
                @Override
                public void add(final int n, final CTCommentAuthor ctCommentAuthor) {
                    CTCommentAuthorListImpl.this.insertNewCmAuthor(n).set((XmlObject)ctCommentAuthor);
                }
                
                @Override
                public CTCommentAuthor remove(final int n) {
                    final CTCommentAuthor cmAuthorArray = CTCommentAuthorListImpl.this.getCmAuthorArray(n);
                    CTCommentAuthorListImpl.this.removeCmAuthor(n);
                    return cmAuthorArray;
                }
                
                @Override
                public int size() {
                    return CTCommentAuthorListImpl.this.sizeOfCmAuthorArray();
                }
            }
            return new CmAuthorList();
        }
    }
    
    @Deprecated
    public CTCommentAuthor[] getCmAuthorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTCommentAuthorListImpl.CMAUTHOR$0, (List)list);
            final CTCommentAuthor[] array = new CTCommentAuthor[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCommentAuthor getCmAuthorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCommentAuthor ctCommentAuthor = (CTCommentAuthor)this.get_store().find_element_user(CTCommentAuthorListImpl.CMAUTHOR$0, n);
            if (ctCommentAuthor == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCommentAuthor;
        }
    }
    
    public int sizeOfCmAuthorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCommentAuthorListImpl.CMAUTHOR$0);
        }
    }
    
    public void setCmAuthorArray(final CTCommentAuthor[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTCommentAuthorListImpl.CMAUTHOR$0);
    }
    
    public void setCmAuthorArray(final int n, final CTCommentAuthor ctCommentAuthor) {
        this.generatedSetterHelperImpl((XmlObject)ctCommentAuthor, CTCommentAuthorListImpl.CMAUTHOR$0, n, (short)2);
    }
    
    public CTCommentAuthor insertNewCmAuthor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCommentAuthor)this.get_store().insert_element_user(CTCommentAuthorListImpl.CMAUTHOR$0, n);
        }
    }
    
    public CTCommentAuthor addNewCmAuthor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCommentAuthor)this.get_store().add_element_user(CTCommentAuthorListImpl.CMAUTHOR$0);
        }
    }
    
    public void removeCmAuthor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCommentAuthorListImpl.CMAUTHOR$0, n);
        }
    }
    
    static {
        CMAUTHOR$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cmAuthor");
    }
}
