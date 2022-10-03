package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTDashStop;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTDashStopList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDashStopListImpl extends XmlComplexContentImpl implements CTDashStopList
{
    private static final long serialVersionUID = 1L;
    private static final QName DS$0;
    
    public CTDashStopListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTDashStop> getDsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DsList extends AbstractList<CTDashStop>
            {
                @Override
                public CTDashStop get(final int n) {
                    return CTDashStopListImpl.this.getDsArray(n);
                }
                
                @Override
                public CTDashStop set(final int n, final CTDashStop ctDashStop) {
                    final CTDashStop dsArray = CTDashStopListImpl.this.getDsArray(n);
                    CTDashStopListImpl.this.setDsArray(n, ctDashStop);
                    return dsArray;
                }
                
                @Override
                public void add(final int n, final CTDashStop ctDashStop) {
                    CTDashStopListImpl.this.insertNewDs(n).set((XmlObject)ctDashStop);
                }
                
                @Override
                public CTDashStop remove(final int n) {
                    final CTDashStop dsArray = CTDashStopListImpl.this.getDsArray(n);
                    CTDashStopListImpl.this.removeDs(n);
                    return dsArray;
                }
                
                @Override
                public int size() {
                    return CTDashStopListImpl.this.sizeOfDsArray();
                }
            }
            return new DsList();
        }
    }
    
    @Deprecated
    public CTDashStop[] getDsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTDashStopListImpl.DS$0, (List)list);
            final CTDashStop[] array = new CTDashStop[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDashStop getDsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDashStop ctDashStop = (CTDashStop)this.get_store().find_element_user(CTDashStopListImpl.DS$0, n);
            if (ctDashStop == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctDashStop;
        }
    }
    
    public int sizeOfDsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDashStopListImpl.DS$0);
        }
    }
    
    public void setDsArray(final CTDashStop[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTDashStopListImpl.DS$0);
    }
    
    public void setDsArray(final int n, final CTDashStop ctDashStop) {
        this.generatedSetterHelperImpl((XmlObject)ctDashStop, CTDashStopListImpl.DS$0, n, (short)2);
    }
    
    public CTDashStop insertNewDs(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDashStop)this.get_store().insert_element_user(CTDashStopListImpl.DS$0, n);
        }
    }
    
    public CTDashStop addNewDs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDashStop)this.get_store().add_element_user(CTDashStopListImpl.DS$0);
        }
    }
    
    public void removeDs(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDashStopListImpl.DS$0, n);
        }
    }
    
    static {
        DS$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "ds");
    }
}
