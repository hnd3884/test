package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientStop;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientStopList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTGradientStopListImpl extends XmlComplexContentImpl implements CTGradientStopList
{
    private static final long serialVersionUID = 1L;
    private static final QName GS$0;
    
    public CTGradientStopListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTGradientStop> getGsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GsList extends AbstractList<CTGradientStop>
            {
                @Override
                public CTGradientStop get(final int n) {
                    return CTGradientStopListImpl.this.getGsArray(n);
                }
                
                @Override
                public CTGradientStop set(final int n, final CTGradientStop ctGradientStop) {
                    final CTGradientStop gsArray = CTGradientStopListImpl.this.getGsArray(n);
                    CTGradientStopListImpl.this.setGsArray(n, ctGradientStop);
                    return gsArray;
                }
                
                @Override
                public void add(final int n, final CTGradientStop ctGradientStop) {
                    CTGradientStopListImpl.this.insertNewGs(n).set((XmlObject)ctGradientStop);
                }
                
                @Override
                public CTGradientStop remove(final int n) {
                    final CTGradientStop gsArray = CTGradientStopListImpl.this.getGsArray(n);
                    CTGradientStopListImpl.this.removeGs(n);
                    return gsArray;
                }
                
                @Override
                public int size() {
                    return CTGradientStopListImpl.this.sizeOfGsArray();
                }
            }
            return new GsList();
        }
    }
    
    @Deprecated
    public CTGradientStop[] getGsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGradientStopListImpl.GS$0, (List)list);
            final CTGradientStop[] array = new CTGradientStop[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTGradientStop getGsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGradientStop ctGradientStop = (CTGradientStop)this.get_store().find_element_user(CTGradientStopListImpl.GS$0, n);
            if (ctGradientStop == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctGradientStop;
        }
    }
    
    public int sizeOfGsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGradientStopListImpl.GS$0);
        }
    }
    
    public void setGsArray(final CTGradientStop[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGradientStopListImpl.GS$0);
    }
    
    public void setGsArray(final int n, final CTGradientStop ctGradientStop) {
        this.generatedSetterHelperImpl((XmlObject)ctGradientStop, CTGradientStopListImpl.GS$0, n, (short)2);
    }
    
    public CTGradientStop insertNewGs(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGradientStop)this.get_store().insert_element_user(CTGradientStopListImpl.GS$0, n);
        }
    }
    
    public CTGradientStop addNewGs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGradientStop)this.get_store().add_element_user(CTGradientStopListImpl.GS$0);
        }
    }
    
    public void removeGs(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGradientStopListImpl.GS$0, n);
        }
    }
    
    static {
        GS$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gs");
    }
}
