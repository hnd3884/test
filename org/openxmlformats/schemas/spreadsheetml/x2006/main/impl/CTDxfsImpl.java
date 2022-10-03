package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxf;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxfs;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDxfsImpl extends XmlComplexContentImpl implements CTDxfs
{
    private static final long serialVersionUID = 1L;
    private static final QName DXF$0;
    private static final QName COUNT$2;
    
    public CTDxfsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTDxf> getDxfList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DxfList extends AbstractList<CTDxf>
            {
                @Override
                public CTDxf get(final int n) {
                    return CTDxfsImpl.this.getDxfArray(n);
                }
                
                @Override
                public CTDxf set(final int n, final CTDxf ctDxf) {
                    final CTDxf dxfArray = CTDxfsImpl.this.getDxfArray(n);
                    CTDxfsImpl.this.setDxfArray(n, ctDxf);
                    return dxfArray;
                }
                
                @Override
                public void add(final int n, final CTDxf ctDxf) {
                    CTDxfsImpl.this.insertNewDxf(n).set((XmlObject)ctDxf);
                }
                
                @Override
                public CTDxf remove(final int n) {
                    final CTDxf dxfArray = CTDxfsImpl.this.getDxfArray(n);
                    CTDxfsImpl.this.removeDxf(n);
                    return dxfArray;
                }
                
                @Override
                public int size() {
                    return CTDxfsImpl.this.sizeOfDxfArray();
                }
            }
            return new DxfList();
        }
    }
    
    @Deprecated
    public CTDxf[] getDxfArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTDxfsImpl.DXF$0, (List)list);
            final CTDxf[] array = new CTDxf[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDxf getDxfArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDxf ctDxf = (CTDxf)this.get_store().find_element_user(CTDxfsImpl.DXF$0, n);
            if (ctDxf == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctDxf;
        }
    }
    
    public int sizeOfDxfArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDxfsImpl.DXF$0);
        }
    }
    
    public void setDxfArray(final CTDxf[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTDxfsImpl.DXF$0);
    }
    
    public void setDxfArray(final int n, final CTDxf ctDxf) {
        this.generatedSetterHelperImpl((XmlObject)ctDxf, CTDxfsImpl.DXF$0, n, (short)2);
    }
    
    public CTDxf insertNewDxf(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDxf)this.get_store().insert_element_user(CTDxfsImpl.DXF$0, n);
        }
    }
    
    public CTDxf addNewDxf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDxf)this.get_store().add_element_user(CTDxfsImpl.DXF$0);
        }
    }
    
    public void removeDxf(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDxfsImpl.DXF$0, n);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDxfsImpl.COUNT$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTDxfsImpl.COUNT$2);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDxfsImpl.COUNT$2) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDxfsImpl.COUNT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDxfsImpl.COUNT$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTDxfsImpl.COUNT$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTDxfsImpl.COUNT$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDxfsImpl.COUNT$2);
        }
    }
    
    static {
        DXF$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "dxf");
        COUNT$2 = new QName("", "count");
    }
}
