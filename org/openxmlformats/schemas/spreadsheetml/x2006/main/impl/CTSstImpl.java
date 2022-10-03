package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSst;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSstImpl extends XmlComplexContentImpl implements CTSst
{
    private static final long serialVersionUID = 1L;
    private static final QName SI$0;
    private static final QName EXTLST$2;
    private static final QName COUNT$4;
    private static final QName UNIQUECOUNT$6;
    
    public CTSstImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTRst> getSiList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SiList extends AbstractList<CTRst>
            {
                @Override
                public CTRst get(final int n) {
                    return CTSstImpl.this.getSiArray(n);
                }
                
                @Override
                public CTRst set(final int n, final CTRst ctRst) {
                    final CTRst siArray = CTSstImpl.this.getSiArray(n);
                    CTSstImpl.this.setSiArray(n, ctRst);
                    return siArray;
                }
                
                @Override
                public void add(final int n, final CTRst ctRst) {
                    CTSstImpl.this.insertNewSi(n).set((XmlObject)ctRst);
                }
                
                @Override
                public CTRst remove(final int n) {
                    final CTRst siArray = CTSstImpl.this.getSiArray(n);
                    CTSstImpl.this.removeSi(n);
                    return siArray;
                }
                
                @Override
                public int size() {
                    return CTSstImpl.this.sizeOfSiArray();
                }
            }
            return new SiList();
        }
    }
    
    @Deprecated
    public CTRst[] getSiArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSstImpl.SI$0, (List)list);
            final CTRst[] array = new CTRst[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRst getSiArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRst ctRst = (CTRst)this.get_store().find_element_user(CTSstImpl.SI$0, n);
            if (ctRst == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRst;
        }
    }
    
    public int sizeOfSiArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSstImpl.SI$0);
        }
    }
    
    public void setSiArray(final CTRst[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSstImpl.SI$0);
    }
    
    public void setSiArray(final int n, final CTRst ctRst) {
        this.generatedSetterHelperImpl((XmlObject)ctRst, CTSstImpl.SI$0, n, (short)2);
    }
    
    public CTRst insertNewSi(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRst)this.get_store().insert_element_user(CTSstImpl.SI$0, n);
        }
    }
    
    public CTRst addNewSi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRst)this.get_store().add_element_user(CTSstImpl.SI$0);
        }
    }
    
    public void removeSi(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSstImpl.SI$0, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTSstImpl.EXTLST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSstImpl.EXTLST$2) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTSstImpl.EXTLST$2, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTSstImpl.EXTLST$2);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSstImpl.EXTLST$2, 0);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSstImpl.COUNT$4);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTSstImpl.COUNT$4);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSstImpl.COUNT$4) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSstImpl.COUNT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSstImpl.COUNT$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSstImpl.COUNT$4);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTSstImpl.COUNT$4);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSstImpl.COUNT$4);
        }
    }
    
    public long getUniqueCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSstImpl.UNIQUECOUNT$6);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetUniqueCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTSstImpl.UNIQUECOUNT$6);
        }
    }
    
    public boolean isSetUniqueCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSstImpl.UNIQUECOUNT$6) != null;
        }
    }
    
    public void setUniqueCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSstImpl.UNIQUECOUNT$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSstImpl.UNIQUECOUNT$6);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetUniqueCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSstImpl.UNIQUECOUNT$6);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTSstImpl.UNIQUECOUNT$6);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetUniqueCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSstImpl.UNIQUECOUNT$6);
        }
    }
    
    static {
        SI$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "si");
        EXTLST$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        COUNT$4 = new QName("", "count");
        UNIQUECOUNT$6 = new QName("", "uniqueCount");
    }
}
