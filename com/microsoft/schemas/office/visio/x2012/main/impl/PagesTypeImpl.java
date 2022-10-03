package com.microsoft.schemas.office.visio.x2012.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import com.microsoft.schemas.office.visio.x2012.main.PageType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.PagesType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class PagesTypeImpl extends XmlComplexContentImpl implements PagesType
{
    private static final long serialVersionUID = 1L;
    private static final QName PAGE$0;
    
    public PagesTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<PageType> getPageList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PageList extends AbstractList<PageType>
            {
                @Override
                public PageType get(final int n) {
                    return PagesTypeImpl.this.getPageArray(n);
                }
                
                @Override
                public PageType set(final int n, final PageType pageType) {
                    final PageType pageArray = PagesTypeImpl.this.getPageArray(n);
                    PagesTypeImpl.this.setPageArray(n, pageType);
                    return pageArray;
                }
                
                @Override
                public void add(final int n, final PageType pageType) {
                    PagesTypeImpl.this.insertNewPage(n).set((XmlObject)pageType);
                }
                
                @Override
                public PageType remove(final int n) {
                    final PageType pageArray = PagesTypeImpl.this.getPageArray(n);
                    PagesTypeImpl.this.removePage(n);
                    return pageArray;
                }
                
                @Override
                public int size() {
                    return PagesTypeImpl.this.sizeOfPageArray();
                }
            }
            return new PageList();
        }
    }
    
    @Deprecated
    public PageType[] getPageArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(PagesTypeImpl.PAGE$0, (List)list);
            final PageType[] array = new PageType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public PageType getPageArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final PageType pageType = (PageType)this.get_store().find_element_user(PagesTypeImpl.PAGE$0, n);
            if (pageType == null) {
                throw new IndexOutOfBoundsException();
            }
            return pageType;
        }
    }
    
    public int sizeOfPageArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(PagesTypeImpl.PAGE$0);
        }
    }
    
    public void setPageArray(final PageType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, PagesTypeImpl.PAGE$0);
    }
    
    public void setPageArray(final int n, final PageType pageType) {
        this.generatedSetterHelperImpl((XmlObject)pageType, PagesTypeImpl.PAGE$0, n, (short)2);
    }
    
    public PageType insertNewPage(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (PageType)this.get_store().insert_element_user(PagesTypeImpl.PAGE$0, n);
        }
    }
    
    public PageType addNewPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (PageType)this.get_store().add_element_user(PagesTypeImpl.PAGE$0);
        }
    }
    
    public void removePage(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(PagesTypeImpl.PAGE$0, n);
        }
    }
    
    static {
        PAGE$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Page");
    }
}
