package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjPoint2D;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DCubicBezierTo;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPath2DCubicBezierToImpl extends XmlComplexContentImpl implements CTPath2DCubicBezierTo
{
    private static final long serialVersionUID = 1L;
    private static final QName PT$0;
    
    public CTPath2DCubicBezierToImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTAdjPoint2D> getPtList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PtList extends AbstractList<CTAdjPoint2D>
            {
                @Override
                public CTAdjPoint2D get(final int n) {
                    return CTPath2DCubicBezierToImpl.this.getPtArray(n);
                }
                
                @Override
                public CTAdjPoint2D set(final int n, final CTAdjPoint2D ctAdjPoint2D) {
                    final CTAdjPoint2D ptArray = CTPath2DCubicBezierToImpl.this.getPtArray(n);
                    CTPath2DCubicBezierToImpl.this.setPtArray(n, ctAdjPoint2D);
                    return ptArray;
                }
                
                @Override
                public void add(final int n, final CTAdjPoint2D ctAdjPoint2D) {
                    CTPath2DCubicBezierToImpl.this.insertNewPt(n).set((XmlObject)ctAdjPoint2D);
                }
                
                @Override
                public CTAdjPoint2D remove(final int n) {
                    final CTAdjPoint2D ptArray = CTPath2DCubicBezierToImpl.this.getPtArray(n);
                    CTPath2DCubicBezierToImpl.this.removePt(n);
                    return ptArray;
                }
                
                @Override
                public int size() {
                    return CTPath2DCubicBezierToImpl.this.sizeOfPtArray();
                }
            }
            return new PtList();
        }
    }
    
    @Deprecated
    public CTAdjPoint2D[] getPtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPath2DCubicBezierToImpl.PT$0, (List)list);
            final CTAdjPoint2D[] array = new CTAdjPoint2D[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAdjPoint2D getPtArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAdjPoint2D ctAdjPoint2D = (CTAdjPoint2D)this.get_store().find_element_user(CTPath2DCubicBezierToImpl.PT$0, n);
            if (ctAdjPoint2D == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAdjPoint2D;
        }
    }
    
    public int sizeOfPtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPath2DCubicBezierToImpl.PT$0);
        }
    }
    
    public void setPtArray(final CTAdjPoint2D[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPath2DCubicBezierToImpl.PT$0);
    }
    
    public void setPtArray(final int n, final CTAdjPoint2D ctAdjPoint2D) {
        this.generatedSetterHelperImpl((XmlObject)ctAdjPoint2D, CTPath2DCubicBezierToImpl.PT$0, n, (short)2);
    }
    
    public CTAdjPoint2D insertNewPt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAdjPoint2D)this.get_store().insert_element_user(CTPath2DCubicBezierToImpl.PT$0, n);
        }
    }
    
    public CTAdjPoint2D addNewPt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAdjPoint2D)this.get_store().add_element_user(CTPath2DCubicBezierToImpl.PT$0);
        }
    }
    
    public void removePt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPath2DCubicBezierToImpl.PT$0, n);
        }
    }
    
    static {
        PT$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pt");
    }
}
