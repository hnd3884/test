package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTPolarAdjustHandle;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTXYAdjustHandle;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjustHandleList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTAdjustHandleListImpl extends XmlComplexContentImpl implements CTAdjustHandleList
{
    private static final long serialVersionUID = 1L;
    private static final QName AHXY$0;
    private static final QName AHPOLAR$2;
    
    public CTAdjustHandleListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTXYAdjustHandle> getAhXYList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AhXYList extends AbstractList<CTXYAdjustHandle>
            {
                @Override
                public CTXYAdjustHandle get(final int n) {
                    return CTAdjustHandleListImpl.this.getAhXYArray(n);
                }
                
                @Override
                public CTXYAdjustHandle set(final int n, final CTXYAdjustHandle ctxyAdjustHandle) {
                    final CTXYAdjustHandle ahXYArray = CTAdjustHandleListImpl.this.getAhXYArray(n);
                    CTAdjustHandleListImpl.this.setAhXYArray(n, ctxyAdjustHandle);
                    return ahXYArray;
                }
                
                @Override
                public void add(final int n, final CTXYAdjustHandle ctxyAdjustHandle) {
                    CTAdjustHandleListImpl.this.insertNewAhXY(n).set((XmlObject)ctxyAdjustHandle);
                }
                
                @Override
                public CTXYAdjustHandle remove(final int n) {
                    final CTXYAdjustHandle ahXYArray = CTAdjustHandleListImpl.this.getAhXYArray(n);
                    CTAdjustHandleListImpl.this.removeAhXY(n);
                    return ahXYArray;
                }
                
                @Override
                public int size() {
                    return CTAdjustHandleListImpl.this.sizeOfAhXYArray();
                }
            }
            return new AhXYList();
        }
    }
    
    @Deprecated
    public CTXYAdjustHandle[] getAhXYArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTAdjustHandleListImpl.AHXY$0, (List)list);
            final CTXYAdjustHandle[] array = new CTXYAdjustHandle[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTXYAdjustHandle getAhXYArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTXYAdjustHandle ctxyAdjustHandle = (CTXYAdjustHandle)this.get_store().find_element_user(CTAdjustHandleListImpl.AHXY$0, n);
            if (ctxyAdjustHandle == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctxyAdjustHandle;
        }
    }
    
    public int sizeOfAhXYArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAdjustHandleListImpl.AHXY$0);
        }
    }
    
    public void setAhXYArray(final CTXYAdjustHandle[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTAdjustHandleListImpl.AHXY$0);
    }
    
    public void setAhXYArray(final int n, final CTXYAdjustHandle ctxyAdjustHandle) {
        this.generatedSetterHelperImpl((XmlObject)ctxyAdjustHandle, CTAdjustHandleListImpl.AHXY$0, n, (short)2);
    }
    
    public CTXYAdjustHandle insertNewAhXY(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTXYAdjustHandle)this.get_store().insert_element_user(CTAdjustHandleListImpl.AHXY$0, n);
        }
    }
    
    public CTXYAdjustHandle addNewAhXY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTXYAdjustHandle)this.get_store().add_element_user(CTAdjustHandleListImpl.AHXY$0);
        }
    }
    
    public void removeAhXY(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAdjustHandleListImpl.AHXY$0, n);
        }
    }
    
    public List<CTPolarAdjustHandle> getAhPolarList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AhPolarList extends AbstractList<CTPolarAdjustHandle>
            {
                @Override
                public CTPolarAdjustHandle get(final int n) {
                    return CTAdjustHandleListImpl.this.getAhPolarArray(n);
                }
                
                @Override
                public CTPolarAdjustHandle set(final int n, final CTPolarAdjustHandle ctPolarAdjustHandle) {
                    final CTPolarAdjustHandle ahPolarArray = CTAdjustHandleListImpl.this.getAhPolarArray(n);
                    CTAdjustHandleListImpl.this.setAhPolarArray(n, ctPolarAdjustHandle);
                    return ahPolarArray;
                }
                
                @Override
                public void add(final int n, final CTPolarAdjustHandle ctPolarAdjustHandle) {
                    CTAdjustHandleListImpl.this.insertNewAhPolar(n).set((XmlObject)ctPolarAdjustHandle);
                }
                
                @Override
                public CTPolarAdjustHandle remove(final int n) {
                    final CTPolarAdjustHandle ahPolarArray = CTAdjustHandleListImpl.this.getAhPolarArray(n);
                    CTAdjustHandleListImpl.this.removeAhPolar(n);
                    return ahPolarArray;
                }
                
                @Override
                public int size() {
                    return CTAdjustHandleListImpl.this.sizeOfAhPolarArray();
                }
            }
            return new AhPolarList();
        }
    }
    
    @Deprecated
    public CTPolarAdjustHandle[] getAhPolarArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTAdjustHandleListImpl.AHPOLAR$2, (List)list);
            final CTPolarAdjustHandle[] array = new CTPolarAdjustHandle[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPolarAdjustHandle getAhPolarArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPolarAdjustHandle ctPolarAdjustHandle = (CTPolarAdjustHandle)this.get_store().find_element_user(CTAdjustHandleListImpl.AHPOLAR$2, n);
            if (ctPolarAdjustHandle == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPolarAdjustHandle;
        }
    }
    
    public int sizeOfAhPolarArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAdjustHandleListImpl.AHPOLAR$2);
        }
    }
    
    public void setAhPolarArray(final CTPolarAdjustHandle[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTAdjustHandleListImpl.AHPOLAR$2);
    }
    
    public void setAhPolarArray(final int n, final CTPolarAdjustHandle ctPolarAdjustHandle) {
        this.generatedSetterHelperImpl((XmlObject)ctPolarAdjustHandle, CTAdjustHandleListImpl.AHPOLAR$2, n, (short)2);
    }
    
    public CTPolarAdjustHandle insertNewAhPolar(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPolarAdjustHandle)this.get_store().insert_element_user(CTAdjustHandleListImpl.AHPOLAR$2, n);
        }
    }
    
    public CTPolarAdjustHandle addNewAhPolar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPolarAdjustHandle)this.get_store().add_element_user(CTAdjustHandleListImpl.AHPOLAR$2);
        }
    }
    
    public void removeAhPolar(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAdjustHandleListImpl.AHPOLAR$2, n);
        }
    }
    
    static {
        AHXY$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "ahXY");
        AHPOLAR$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "ahPolar");
    }
}
