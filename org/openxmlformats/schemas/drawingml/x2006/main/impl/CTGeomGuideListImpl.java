package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuide;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuideList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTGeomGuideListImpl extends XmlComplexContentImpl implements CTGeomGuideList
{
    private static final long serialVersionUID = 1L;
    private static final QName GD$0;
    
    public CTGeomGuideListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTGeomGuide> getGdList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GdList extends AbstractList<CTGeomGuide>
            {
                @Override
                public CTGeomGuide get(final int n) {
                    return CTGeomGuideListImpl.this.getGdArray(n);
                }
                
                @Override
                public CTGeomGuide set(final int n, final CTGeomGuide ctGeomGuide) {
                    final CTGeomGuide gdArray = CTGeomGuideListImpl.this.getGdArray(n);
                    CTGeomGuideListImpl.this.setGdArray(n, ctGeomGuide);
                    return gdArray;
                }
                
                @Override
                public void add(final int n, final CTGeomGuide ctGeomGuide) {
                    CTGeomGuideListImpl.this.insertNewGd(n).set((XmlObject)ctGeomGuide);
                }
                
                @Override
                public CTGeomGuide remove(final int n) {
                    final CTGeomGuide gdArray = CTGeomGuideListImpl.this.getGdArray(n);
                    CTGeomGuideListImpl.this.removeGd(n);
                    return gdArray;
                }
                
                @Override
                public int size() {
                    return CTGeomGuideListImpl.this.sizeOfGdArray();
                }
            }
            return new GdList();
        }
    }
    
    @Deprecated
    public CTGeomGuide[] getGdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGeomGuideListImpl.GD$0, (List)list);
            final CTGeomGuide[] array = new CTGeomGuide[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTGeomGuide getGdArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGeomGuide ctGeomGuide = (CTGeomGuide)this.get_store().find_element_user(CTGeomGuideListImpl.GD$0, n);
            if (ctGeomGuide == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctGeomGuide;
        }
    }
    
    public int sizeOfGdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGeomGuideListImpl.GD$0);
        }
    }
    
    public void setGdArray(final CTGeomGuide[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGeomGuideListImpl.GD$0);
    }
    
    public void setGdArray(final int n, final CTGeomGuide ctGeomGuide) {
        this.generatedSetterHelperImpl((XmlObject)ctGeomGuide, CTGeomGuideListImpl.GD$0, n, (short)2);
    }
    
    public CTGeomGuide insertNewGd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGeomGuide)this.get_store().insert_element_user(CTGeomGuideListImpl.GD$0, n);
        }
    }
    
    public CTGeomGuide addNewGd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGeomGuide)this.get_store().add_element_user(CTGeomGuideListImpl.GD$0);
        }
    }
    
    public void removeGd(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGeomGuideListImpl.GD$0, n);
        }
    }
    
    static {
        GD$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gd");
    }
}
