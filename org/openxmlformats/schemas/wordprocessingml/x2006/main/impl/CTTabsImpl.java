package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabStop;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabs;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTabsImpl extends XmlComplexContentImpl implements CTTabs
{
    private static final long serialVersionUID = 1L;
    private static final QName TAB$0;
    
    public CTTabsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTTabStop> getTabList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TabList extends AbstractList<CTTabStop>
            {
                @Override
                public CTTabStop get(final int n) {
                    return CTTabsImpl.this.getTabArray(n);
                }
                
                @Override
                public CTTabStop set(final int n, final CTTabStop ctTabStop) {
                    final CTTabStop tabArray = CTTabsImpl.this.getTabArray(n);
                    CTTabsImpl.this.setTabArray(n, ctTabStop);
                    return tabArray;
                }
                
                @Override
                public void add(final int n, final CTTabStop ctTabStop) {
                    CTTabsImpl.this.insertNewTab(n).set((XmlObject)ctTabStop);
                }
                
                @Override
                public CTTabStop remove(final int n) {
                    final CTTabStop tabArray = CTTabsImpl.this.getTabArray(n);
                    CTTabsImpl.this.removeTab(n);
                    return tabArray;
                }
                
                @Override
                public int size() {
                    return CTTabsImpl.this.sizeOfTabArray();
                }
            }
            return new TabList();
        }
    }
    
    @Deprecated
    public CTTabStop[] getTabArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTabsImpl.TAB$0, (List)list);
            final CTTabStop[] array = new CTTabStop[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTabStop getTabArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTabStop ctTabStop = (CTTabStop)this.get_store().find_element_user(CTTabsImpl.TAB$0, n);
            if (ctTabStop == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTabStop;
        }
    }
    
    public int sizeOfTabArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTabsImpl.TAB$0);
        }
    }
    
    public void setTabArray(final CTTabStop[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTabsImpl.TAB$0);
    }
    
    public void setTabArray(final int n, final CTTabStop ctTabStop) {
        this.generatedSetterHelperImpl((XmlObject)ctTabStop, CTTabsImpl.TAB$0, n, (short)2);
    }
    
    public CTTabStop insertNewTab(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTabStop)this.get_store().insert_element_user(CTTabsImpl.TAB$0, n);
        }
    }
    
    public CTTabStop addNewTab() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTabStop)this.get_store().add_element_user(CTTabsImpl.TAB$0);
        }
    }
    
    public void removeTab(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTabsImpl.TAB$0, n);
        }
    }
    
    static {
        TAB$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tab");
    }
}
