package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumVal;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.STXstring;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumData;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNumDataImpl extends XmlComplexContentImpl implements CTNumData
{
    private static final long serialVersionUID = 1L;
    private static final QName FORMATCODE$0;
    private static final QName PTCOUNT$2;
    private static final QName PT$4;
    private static final QName EXTLST$6;
    
    public CTNumDataImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getFormatCode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTNumDataImpl.FORMATCODE$0, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetFormatCode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_element_user(CTNumDataImpl.FORMATCODE$0, 0);
        }
    }
    
    public boolean isSetFormatCode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumDataImpl.FORMATCODE$0) != 0;
        }
    }
    
    public void setFormatCode(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTNumDataImpl.FORMATCODE$0, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTNumDataImpl.FORMATCODE$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFormatCode(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_element_user(CTNumDataImpl.FORMATCODE$0, 0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_element_user(CTNumDataImpl.FORMATCODE$0);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetFormatCode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumDataImpl.FORMATCODE$0, 0);
        }
    }
    
    public CTUnsignedInt getPtCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTNumDataImpl.PTCOUNT$2, 0);
            if (ctUnsignedInt == null) {
                return null;
            }
            return ctUnsignedInt;
        }
    }
    
    public boolean isSetPtCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumDataImpl.PTCOUNT$2) != 0;
        }
    }
    
    public void setPtCount(final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTNumDataImpl.PTCOUNT$2, 0, (short)1);
    }
    
    public CTUnsignedInt addNewPtCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTNumDataImpl.PTCOUNT$2);
        }
    }
    
    public void unsetPtCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumDataImpl.PTCOUNT$2, 0);
        }
    }
    
    public List<CTNumVal> getPtList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PtList extends AbstractList<CTNumVal>
            {
                @Override
                public CTNumVal get(final int n) {
                    return CTNumDataImpl.this.getPtArray(n);
                }
                
                @Override
                public CTNumVal set(final int n, final CTNumVal ctNumVal) {
                    final CTNumVal ptArray = CTNumDataImpl.this.getPtArray(n);
                    CTNumDataImpl.this.setPtArray(n, ctNumVal);
                    return ptArray;
                }
                
                @Override
                public void add(final int n, final CTNumVal ctNumVal) {
                    CTNumDataImpl.this.insertNewPt(n).set((XmlObject)ctNumVal);
                }
                
                @Override
                public CTNumVal remove(final int n) {
                    final CTNumVal ptArray = CTNumDataImpl.this.getPtArray(n);
                    CTNumDataImpl.this.removePt(n);
                    return ptArray;
                }
                
                @Override
                public int size() {
                    return CTNumDataImpl.this.sizeOfPtArray();
                }
            }
            return new PtList();
        }
    }
    
    @Deprecated
    public CTNumVal[] getPtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTNumDataImpl.PT$4, (List)list);
            final CTNumVal[] array = new CTNumVal[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTNumVal getPtArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumVal ctNumVal = (CTNumVal)this.get_store().find_element_user(CTNumDataImpl.PT$4, n);
            if (ctNumVal == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctNumVal;
        }
    }
    
    public int sizeOfPtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumDataImpl.PT$4);
        }
    }
    
    public void setPtArray(final CTNumVal[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTNumDataImpl.PT$4);
    }
    
    public void setPtArray(final int n, final CTNumVal ctNumVal) {
        this.generatedSetterHelperImpl((XmlObject)ctNumVal, CTNumDataImpl.PT$4, n, (short)2);
    }
    
    public CTNumVal insertNewPt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumVal)this.get_store().insert_element_user(CTNumDataImpl.PT$4, n);
        }
    }
    
    public CTNumVal addNewPt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumVal)this.get_store().add_element_user(CTNumDataImpl.PT$4);
        }
    }
    
    public void removePt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumDataImpl.PT$4, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTNumDataImpl.EXTLST$6, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumDataImpl.EXTLST$6) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTNumDataImpl.EXTLST$6, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTNumDataImpl.EXTLST$6);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumDataImpl.EXTLST$6, 0);
        }
    }
    
    static {
        FORMATCODE$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "formatCode");
        PTCOUNT$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "ptCount");
        PT$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "pt");
        EXTLST$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
