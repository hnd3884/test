package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRecord;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCacheRecords;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPivotCacheRecordsImpl extends XmlComplexContentImpl implements CTPivotCacheRecords
{
    private static final long serialVersionUID = 1L;
    private static final QName R$0;
    private static final QName EXTLST$2;
    private static final QName COUNT$4;
    
    public CTPivotCacheRecordsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTRecord> getRList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RList extends AbstractList<CTRecord>
            {
                @Override
                public CTRecord get(final int n) {
                    return CTPivotCacheRecordsImpl.this.getRArray(n);
                }
                
                @Override
                public CTRecord set(final int n, final CTRecord ctRecord) {
                    final CTRecord rArray = CTPivotCacheRecordsImpl.this.getRArray(n);
                    CTPivotCacheRecordsImpl.this.setRArray(n, ctRecord);
                    return rArray;
                }
                
                @Override
                public void add(final int n, final CTRecord ctRecord) {
                    CTPivotCacheRecordsImpl.this.insertNewR(n).set((XmlObject)ctRecord);
                }
                
                @Override
                public CTRecord remove(final int n) {
                    final CTRecord rArray = CTPivotCacheRecordsImpl.this.getRArray(n);
                    CTPivotCacheRecordsImpl.this.removeR(n);
                    return rArray;
                }
                
                @Override
                public int size() {
                    return CTPivotCacheRecordsImpl.this.sizeOfRArray();
                }
            }
            return new RList();
        }
    }
    
    @Deprecated
    public CTRecord[] getRArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPivotCacheRecordsImpl.R$0, (List)list);
            final CTRecord[] array = new CTRecord[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRecord getRArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRecord ctRecord = (CTRecord)this.get_store().find_element_user(CTPivotCacheRecordsImpl.R$0, n);
            if (ctRecord == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRecord;
        }
    }
    
    public int sizeOfRArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotCacheRecordsImpl.R$0);
        }
    }
    
    public void setRArray(final CTRecord[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPivotCacheRecordsImpl.R$0);
    }
    
    public void setRArray(final int n, final CTRecord ctRecord) {
        this.generatedSetterHelperImpl((XmlObject)ctRecord, CTPivotCacheRecordsImpl.R$0, n, (short)2);
    }
    
    public CTRecord insertNewR(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRecord)this.get_store().insert_element_user(CTPivotCacheRecordsImpl.R$0, n);
        }
    }
    
    public CTRecord addNewR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRecord)this.get_store().add_element_user(CTPivotCacheRecordsImpl.R$0);
        }
    }
    
    public void removeR(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotCacheRecordsImpl.R$0, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTPivotCacheRecordsImpl.EXTLST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotCacheRecordsImpl.EXTLST$2) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTPivotCacheRecordsImpl.EXTLST$2, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTPivotCacheRecordsImpl.EXTLST$2);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotCacheRecordsImpl.EXTLST$2, 0);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheRecordsImpl.COUNT$4);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotCacheRecordsImpl.COUNT$4);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheRecordsImpl.COUNT$4) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheRecordsImpl.COUNT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheRecordsImpl.COUNT$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotCacheRecordsImpl.COUNT$4);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPivotCacheRecordsImpl.COUNT$4);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheRecordsImpl.COUNT$4);
        }
    }
    
    static {
        R$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "r");
        EXTLST$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        COUNT$4 = new QName("", "count");
    }
}
