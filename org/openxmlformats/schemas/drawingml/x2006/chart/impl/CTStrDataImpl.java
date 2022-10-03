package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrVal;
import java.util.List;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrData;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTStrDataImpl extends XmlComplexContentImpl implements CTStrData
{
    private static final long serialVersionUID = 1L;
    private static final QName PTCOUNT$0;
    private static final QName PT$2;
    private static final QName EXTLST$4;
    
    public CTStrDataImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTUnsignedInt getPtCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTStrDataImpl.PTCOUNT$0, 0);
            if (ctUnsignedInt == null) {
                return null;
            }
            return ctUnsignedInt;
        }
    }
    
    public boolean isSetPtCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStrDataImpl.PTCOUNT$0) != 0;
        }
    }
    
    public void setPtCount(final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTStrDataImpl.PTCOUNT$0, 0, (short)1);
    }
    
    public CTUnsignedInt addNewPtCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTStrDataImpl.PTCOUNT$0);
        }
    }
    
    public void unsetPtCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStrDataImpl.PTCOUNT$0, 0);
        }
    }
    
    public List<CTStrVal> getPtList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PtList extends AbstractList<CTStrVal>
            {
                @Override
                public CTStrVal get(final int n) {
                    return CTStrDataImpl.this.getPtArray(n);
                }
                
                @Override
                public CTStrVal set(final int n, final CTStrVal ctStrVal) {
                    final CTStrVal ptArray = CTStrDataImpl.this.getPtArray(n);
                    CTStrDataImpl.this.setPtArray(n, ctStrVal);
                    return ptArray;
                }
                
                @Override
                public void add(final int n, final CTStrVal ctStrVal) {
                    CTStrDataImpl.this.insertNewPt(n).set((XmlObject)ctStrVal);
                }
                
                @Override
                public CTStrVal remove(final int n) {
                    final CTStrVal ptArray = CTStrDataImpl.this.getPtArray(n);
                    CTStrDataImpl.this.removePt(n);
                    return ptArray;
                }
                
                @Override
                public int size() {
                    return CTStrDataImpl.this.sizeOfPtArray();
                }
            }
            return new PtList();
        }
    }
    
    @Deprecated
    public CTStrVal[] getPtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTStrDataImpl.PT$2, (List)list);
            final CTStrVal[] array = new CTStrVal[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTStrVal getPtArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStrVal ctStrVal = (CTStrVal)this.get_store().find_element_user(CTStrDataImpl.PT$2, n);
            if (ctStrVal == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctStrVal;
        }
    }
    
    public int sizeOfPtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStrDataImpl.PT$2);
        }
    }
    
    public void setPtArray(final CTStrVal[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTStrDataImpl.PT$2);
    }
    
    public void setPtArray(final int n, final CTStrVal ctStrVal) {
        this.generatedSetterHelperImpl((XmlObject)ctStrVal, CTStrDataImpl.PT$2, n, (short)2);
    }
    
    public CTStrVal insertNewPt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStrVal)this.get_store().insert_element_user(CTStrDataImpl.PT$2, n);
        }
    }
    
    public CTStrVal addNewPt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStrVal)this.get_store().add_element_user(CTStrDataImpl.PT$2);
        }
    }
    
    public void removePt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStrDataImpl.PT$2, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTStrDataImpl.EXTLST$4, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTStrDataImpl.EXTLST$4) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTStrDataImpl.EXTLST$4, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTStrDataImpl.EXTLST$4);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTStrDataImpl.EXTLST$4, 0);
        }
    }
    
    static {
        PTCOUNT$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "ptCount");
        PT$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "pt");
        EXTLST$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
