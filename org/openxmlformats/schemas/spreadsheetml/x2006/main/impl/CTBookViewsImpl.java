package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookView;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBookViews;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBookViewsImpl extends XmlComplexContentImpl implements CTBookViews
{
    private static final long serialVersionUID = 1L;
    private static final QName WORKBOOKVIEW$0;
    
    public CTBookViewsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTBookView> getWorkbookViewList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class WorkbookViewList extends AbstractList<CTBookView>
            {
                @Override
                public CTBookView get(final int n) {
                    return CTBookViewsImpl.this.getWorkbookViewArray(n);
                }
                
                @Override
                public CTBookView set(final int n, final CTBookView ctBookView) {
                    final CTBookView workbookViewArray = CTBookViewsImpl.this.getWorkbookViewArray(n);
                    CTBookViewsImpl.this.setWorkbookViewArray(n, ctBookView);
                    return workbookViewArray;
                }
                
                @Override
                public void add(final int n, final CTBookView ctBookView) {
                    CTBookViewsImpl.this.insertNewWorkbookView(n).set((XmlObject)ctBookView);
                }
                
                @Override
                public CTBookView remove(final int n) {
                    final CTBookView workbookViewArray = CTBookViewsImpl.this.getWorkbookViewArray(n);
                    CTBookViewsImpl.this.removeWorkbookView(n);
                    return workbookViewArray;
                }
                
                @Override
                public int size() {
                    return CTBookViewsImpl.this.sizeOfWorkbookViewArray();
                }
            }
            return new WorkbookViewList();
        }
    }
    
    @Deprecated
    public CTBookView[] getWorkbookViewArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBookViewsImpl.WORKBOOKVIEW$0, (List)list);
            final CTBookView[] array = new CTBookView[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBookView getWorkbookViewArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBookView ctBookView = (CTBookView)this.get_store().find_element_user(CTBookViewsImpl.WORKBOOKVIEW$0, n);
            if (ctBookView == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBookView;
        }
    }
    
    public int sizeOfWorkbookViewArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBookViewsImpl.WORKBOOKVIEW$0);
        }
    }
    
    public void setWorkbookViewArray(final CTBookView[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBookViewsImpl.WORKBOOKVIEW$0);
    }
    
    public void setWorkbookViewArray(final int n, final CTBookView ctBookView) {
        this.generatedSetterHelperImpl((XmlObject)ctBookView, CTBookViewsImpl.WORKBOOKVIEW$0, n, (short)2);
    }
    
    public CTBookView insertNewWorkbookView(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookView)this.get_store().insert_element_user(CTBookViewsImpl.WORKBOOKVIEW$0, n);
        }
    }
    
    public CTBookView addNewWorkbookView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBookView)this.get_store().add_element_user(CTBookViewsImpl.WORKBOOKVIEW$0);
        }
    }
    
    public void removeWorkbookView(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBookViewsImpl.WORKBOOKVIEW$0, n);
        }
    }
    
    static {
        WORKBOOKVIEW$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "workbookView");
    }
}
