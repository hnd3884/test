package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorders;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBordersImpl extends XmlComplexContentImpl implements CTBorders
{
    private static final long serialVersionUID = 1L;
    private static final QName BORDER$0;
    private static final QName COUNT$2;
    
    public CTBordersImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTBorder> getBorderList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BorderList extends AbstractList<CTBorder>
            {
                @Override
                public CTBorder get(final int n) {
                    return CTBordersImpl.this.getBorderArray(n);
                }
                
                @Override
                public CTBorder set(final int n, final CTBorder ctBorder) {
                    final CTBorder borderArray = CTBordersImpl.this.getBorderArray(n);
                    CTBordersImpl.this.setBorderArray(n, ctBorder);
                    return borderArray;
                }
                
                @Override
                public void add(final int n, final CTBorder ctBorder) {
                    CTBordersImpl.this.insertNewBorder(n).set((XmlObject)ctBorder);
                }
                
                @Override
                public CTBorder remove(final int n) {
                    final CTBorder borderArray = CTBordersImpl.this.getBorderArray(n);
                    CTBordersImpl.this.removeBorder(n);
                    return borderArray;
                }
                
                @Override
                public int size() {
                    return CTBordersImpl.this.sizeOfBorderArray();
                }
            }
            return new BorderList();
        }
    }
    
    @Deprecated
    public CTBorder[] getBorderArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBordersImpl.BORDER$0, (List)list);
            final CTBorder[] array = new CTBorder[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBorder getBorderArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTBordersImpl.BORDER$0, n);
            if (ctBorder == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBorder;
        }
    }
    
    public int sizeOfBorderArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBordersImpl.BORDER$0);
        }
    }
    
    public void setBorderArray(final CTBorder[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBordersImpl.BORDER$0);
    }
    
    public void setBorderArray(final int n, final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTBordersImpl.BORDER$0, n, (short)2);
    }
    
    public CTBorder insertNewBorder(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().insert_element_user(CTBordersImpl.BORDER$0, n);
        }
    }
    
    public CTBorder addNewBorder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTBordersImpl.BORDER$0);
        }
    }
    
    public void removeBorder(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBordersImpl.BORDER$0, n);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBordersImpl.COUNT$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTBordersImpl.COUNT$2);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBordersImpl.COUNT$2) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBordersImpl.COUNT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBordersImpl.COUNT$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTBordersImpl.COUNT$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTBordersImpl.COUNT$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBordersImpl.COUNT$2);
        }
    }
    
    static {
        BORDER$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "border");
        COUNT$2 = new QName("", "count");
    }
}
