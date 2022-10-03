package com.microsoft.schemas.vml.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import com.microsoft.schemas.vml.CTH;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.vml.CTHandles;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTHandlesImpl extends XmlComplexContentImpl implements CTHandles
{
    private static final long serialVersionUID = 1L;
    private static final QName H$0;
    
    public CTHandlesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTH> getHList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HList extends AbstractList<CTH>
            {
                @Override
                public CTH get(final int n) {
                    return CTHandlesImpl.this.getHArray(n);
                }
                
                @Override
                public CTH set(final int n, final CTH cth) {
                    final CTH hArray = CTHandlesImpl.this.getHArray(n);
                    CTHandlesImpl.this.setHArray(n, cth);
                    return hArray;
                }
                
                @Override
                public void add(final int n, final CTH cth) {
                    CTHandlesImpl.this.insertNewH(n).set((XmlObject)cth);
                }
                
                @Override
                public CTH remove(final int n) {
                    final CTH hArray = CTHandlesImpl.this.getHArray(n);
                    CTHandlesImpl.this.removeH(n);
                    return hArray;
                }
                
                @Override
                public int size() {
                    return CTHandlesImpl.this.sizeOfHArray();
                }
            }
            return new HList();
        }
    }
    
    @Deprecated
    public CTH[] getHArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTHandlesImpl.H$0, (List)list);
            final CTH[] array = new CTH[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTH getHArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTH cth = (CTH)this.get_store().find_element_user(CTHandlesImpl.H$0, n);
            if (cth == null) {
                throw new IndexOutOfBoundsException();
            }
            return cth;
        }
    }
    
    public int sizeOfHArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHandlesImpl.H$0);
        }
    }
    
    public void setHArray(final CTH[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTHandlesImpl.H$0);
    }
    
    public void setHArray(final int n, final CTH cth) {
        this.generatedSetterHelperImpl((XmlObject)cth, CTHandlesImpl.H$0, n, (short)2);
    }
    
    public CTH insertNewH(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTH)this.get_store().insert_element_user(CTHandlesImpl.H$0, n);
        }
    }
    
    public CTH addNewH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTH)this.get_store().add_element_user(CTHandlesImpl.H$0);
        }
    }
    
    public void removeH(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHandlesImpl.H$0, n);
        }
    }
    
    static {
        H$0 = new QName("urn:schemas-microsoft-com:vml", "h");
    }
}
