package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetView;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetViews;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSheetViewsImpl extends XmlComplexContentImpl implements CTSheetViews
{
    private static final long serialVersionUID = 1L;
    private static final QName SHEETVIEW$0;
    private static final QName EXTLST$2;
    
    public CTSheetViewsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTSheetView> getSheetViewList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SheetViewList extends AbstractList<CTSheetView>
            {
                @Override
                public CTSheetView get(final int n) {
                    return CTSheetViewsImpl.this.getSheetViewArray(n);
                }
                
                @Override
                public CTSheetView set(final int n, final CTSheetView ctSheetView) {
                    final CTSheetView sheetViewArray = CTSheetViewsImpl.this.getSheetViewArray(n);
                    CTSheetViewsImpl.this.setSheetViewArray(n, ctSheetView);
                    return sheetViewArray;
                }
                
                @Override
                public void add(final int n, final CTSheetView ctSheetView) {
                    CTSheetViewsImpl.this.insertNewSheetView(n).set((XmlObject)ctSheetView);
                }
                
                @Override
                public CTSheetView remove(final int n) {
                    final CTSheetView sheetViewArray = CTSheetViewsImpl.this.getSheetViewArray(n);
                    CTSheetViewsImpl.this.removeSheetView(n);
                    return sheetViewArray;
                }
                
                @Override
                public int size() {
                    return CTSheetViewsImpl.this.sizeOfSheetViewArray();
                }
            }
            return new SheetViewList();
        }
    }
    
    @Deprecated
    public CTSheetView[] getSheetViewArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSheetViewsImpl.SHEETVIEW$0, (List)list);
            final CTSheetView[] array = new CTSheetView[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSheetView getSheetViewArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSheetView ctSheetView = (CTSheetView)this.get_store().find_element_user(CTSheetViewsImpl.SHEETVIEW$0, n);
            if (ctSheetView == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSheetView;
        }
    }
    
    public int sizeOfSheetViewArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSheetViewsImpl.SHEETVIEW$0);
        }
    }
    
    public void setSheetViewArray(final CTSheetView[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSheetViewsImpl.SHEETVIEW$0);
    }
    
    public void setSheetViewArray(final int n, final CTSheetView ctSheetView) {
        this.generatedSetterHelperImpl((XmlObject)ctSheetView, CTSheetViewsImpl.SHEETVIEW$0, n, (short)2);
    }
    
    public CTSheetView insertNewSheetView(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSheetView)this.get_store().insert_element_user(CTSheetViewsImpl.SHEETVIEW$0, n);
        }
    }
    
    public CTSheetView addNewSheetView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSheetView)this.get_store().add_element_user(CTSheetViewsImpl.SHEETVIEW$0);
        }
    }
    
    public void removeSheetView(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSheetViewsImpl.SHEETVIEW$0, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTSheetViewsImpl.EXTLST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSheetViewsImpl.EXTLST$2) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTSheetViewsImpl.EXTLST$2, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTSheetViewsImpl.EXTLST$2);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSheetViewsImpl.EXTLST$2, 0);
        }
    }
    
    static {
        SHEETVIEW$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sheetView");
        EXTLST$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
    }
}
