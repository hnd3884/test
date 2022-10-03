package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXf;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellStyleXfs;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCellStyleXfsImpl extends XmlComplexContentImpl implements CTCellStyleXfs
{
    private static final long serialVersionUID = 1L;
    private static final QName XF$0;
    private static final QName COUNT$2;
    
    public CTCellStyleXfsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTXf> getXfList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class XfList extends AbstractList<CTXf>
            {
                @Override
                public CTXf get(final int n) {
                    return CTCellStyleXfsImpl.this.getXfArray(n);
                }
                
                @Override
                public CTXf set(final int n, final CTXf ctXf) {
                    final CTXf xfArray = CTCellStyleXfsImpl.this.getXfArray(n);
                    CTCellStyleXfsImpl.this.setXfArray(n, ctXf);
                    return xfArray;
                }
                
                @Override
                public void add(final int n, final CTXf ctXf) {
                    CTCellStyleXfsImpl.this.insertNewXf(n).set((XmlObject)ctXf);
                }
                
                @Override
                public CTXf remove(final int n) {
                    final CTXf xfArray = CTCellStyleXfsImpl.this.getXfArray(n);
                    CTCellStyleXfsImpl.this.removeXf(n);
                    return xfArray;
                }
                
                @Override
                public int size() {
                    return CTCellStyleXfsImpl.this.sizeOfXfArray();
                }
            }
            return new XfList();
        }
    }
    
    @Deprecated
    public CTXf[] getXfArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTCellStyleXfsImpl.XF$0, (List)list);
            final CTXf[] array = new CTXf[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTXf getXfArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTXf ctXf = (CTXf)this.get_store().find_element_user(CTCellStyleXfsImpl.XF$0, n);
            if (ctXf == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctXf;
        }
    }
    
    public int sizeOfXfArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCellStyleXfsImpl.XF$0);
        }
    }
    
    public void setXfArray(final CTXf[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTCellStyleXfsImpl.XF$0);
    }
    
    public void setXfArray(final int n, final CTXf ctXf) {
        this.generatedSetterHelperImpl((XmlObject)ctXf, CTCellStyleXfsImpl.XF$0, n, (short)2);
    }
    
    public CTXf insertNewXf(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTXf)this.get_store().insert_element_user(CTCellStyleXfsImpl.XF$0, n);
        }
    }
    
    public CTXf addNewXf() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTXf)this.get_store().add_element_user(CTCellStyleXfsImpl.XF$0);
        }
    }
    
    public void removeXf(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCellStyleXfsImpl.XF$0, n);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellStyleXfsImpl.COUNT$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTCellStyleXfsImpl.COUNT$2);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellStyleXfsImpl.COUNT$2) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellStyleXfsImpl.COUNT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellStyleXfsImpl.COUNT$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCellStyleXfsImpl.COUNT$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCellStyleXfsImpl.COUNT$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellStyleXfsImpl.COUNT$2);
        }
    }
    
    static {
        XF$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "xf");
        COUNT$2 = new QName("", "count");
    }
}
