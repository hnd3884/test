package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStop;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStopList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextTabStopListImpl extends XmlComplexContentImpl implements CTTextTabStopList
{
    private static final long serialVersionUID = 1L;
    private static final QName TAB$0;
    
    public CTTextTabStopListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTTextTabStop> getTabList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TabList extends AbstractList<CTTextTabStop>
            {
                @Override
                public CTTextTabStop get(final int n) {
                    return CTTextTabStopListImpl.this.getTabArray(n);
                }
                
                @Override
                public CTTextTabStop set(final int n, final CTTextTabStop ctTextTabStop) {
                    final CTTextTabStop tabArray = CTTextTabStopListImpl.this.getTabArray(n);
                    CTTextTabStopListImpl.this.setTabArray(n, ctTextTabStop);
                    return tabArray;
                }
                
                @Override
                public void add(final int n, final CTTextTabStop ctTextTabStop) {
                    CTTextTabStopListImpl.this.insertNewTab(n).set((XmlObject)ctTextTabStop);
                }
                
                @Override
                public CTTextTabStop remove(final int n) {
                    final CTTextTabStop tabArray = CTTextTabStopListImpl.this.getTabArray(n);
                    CTTextTabStopListImpl.this.removeTab(n);
                    return tabArray;
                }
                
                @Override
                public int size() {
                    return CTTextTabStopListImpl.this.sizeOfTabArray();
                }
            }
            return new TabList();
        }
    }
    
    @Deprecated
    public CTTextTabStop[] getTabArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTextTabStopListImpl.TAB$0, (List)list);
            final CTTextTabStop[] array = new CTTextTabStop[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTextTabStop getTabArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextTabStop ctTextTabStop = (CTTextTabStop)this.get_store().find_element_user(CTTextTabStopListImpl.TAB$0, n);
            if (ctTextTabStop == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTextTabStop;
        }
    }
    
    public int sizeOfTabArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextTabStopListImpl.TAB$0);
        }
    }
    
    public void setTabArray(final CTTextTabStop[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTextTabStopListImpl.TAB$0);
    }
    
    public void setTabArray(final int n, final CTTextTabStop ctTextTabStop) {
        this.generatedSetterHelperImpl((XmlObject)ctTextTabStop, CTTextTabStopListImpl.TAB$0, n, (short)2);
    }
    
    public CTTextTabStop insertNewTab(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextTabStop)this.get_store().insert_element_user(CTTextTabStopListImpl.TAB$0, n);
        }
    }
    
    public CTTextTabStop addNewTab() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextTabStop)this.get_store().add_element_user(CTTextTabStopListImpl.TAB$0);
        }
    }
    
    public void removeTab(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextTabStopListImpl.TAB$0, n);
        }
    }
    
    static {
        TAB$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tab");
    }
}
