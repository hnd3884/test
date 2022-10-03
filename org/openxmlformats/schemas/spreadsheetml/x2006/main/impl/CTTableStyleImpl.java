package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleElement;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyle;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTableStyleImpl extends XmlComplexContentImpl implements CTTableStyle
{
    private static final long serialVersionUID = 1L;
    private static final QName TABLESTYLEELEMENT$0;
    private static final QName NAME$2;
    private static final QName PIVOT$4;
    private static final QName TABLE$6;
    private static final QName COUNT$8;
    
    public CTTableStyleImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTTableStyleElement> getTableStyleElementList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TableStyleElementList extends AbstractList<CTTableStyleElement>
            {
                @Override
                public CTTableStyleElement get(final int n) {
                    return CTTableStyleImpl.this.getTableStyleElementArray(n);
                }
                
                @Override
                public CTTableStyleElement set(final int n, final CTTableStyleElement ctTableStyleElement) {
                    final CTTableStyleElement tableStyleElementArray = CTTableStyleImpl.this.getTableStyleElementArray(n);
                    CTTableStyleImpl.this.setTableStyleElementArray(n, ctTableStyleElement);
                    return tableStyleElementArray;
                }
                
                @Override
                public void add(final int n, final CTTableStyleElement ctTableStyleElement) {
                    CTTableStyleImpl.this.insertNewTableStyleElement(n).set((XmlObject)ctTableStyleElement);
                }
                
                @Override
                public CTTableStyleElement remove(final int n) {
                    final CTTableStyleElement tableStyleElementArray = CTTableStyleImpl.this.getTableStyleElementArray(n);
                    CTTableStyleImpl.this.removeTableStyleElement(n);
                    return tableStyleElementArray;
                }
                
                @Override
                public int size() {
                    return CTTableStyleImpl.this.sizeOfTableStyleElementArray();
                }
            }
            return new TableStyleElementList();
        }
    }
    
    @Deprecated
    public CTTableStyleElement[] getTableStyleElementArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTableStyleImpl.TABLESTYLEELEMENT$0, (List)list);
            final CTTableStyleElement[] array = new CTTableStyleElement[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTableStyleElement getTableStyleElementArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableStyleElement ctTableStyleElement = (CTTableStyleElement)this.get_store().find_element_user(CTTableStyleImpl.TABLESTYLEELEMENT$0, n);
            if (ctTableStyleElement == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTableStyleElement;
        }
    }
    
    public int sizeOfTableStyleElementArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleImpl.TABLESTYLEELEMENT$0);
        }
    }
    
    public void setTableStyleElementArray(final CTTableStyleElement[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTableStyleImpl.TABLESTYLEELEMENT$0);
    }
    
    public void setTableStyleElementArray(final int n, final CTTableStyleElement ctTableStyleElement) {
        this.generatedSetterHelperImpl((XmlObject)ctTableStyleElement, CTTableStyleImpl.TABLESTYLEELEMENT$0, n, (short)2);
    }
    
    public CTTableStyleElement insertNewTableStyleElement(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableStyleElement)this.get_store().insert_element_user(CTTableStyleImpl.TABLESTYLEELEMENT$0, n);
        }
    }
    
    public CTTableStyleElement addNewTableStyleElement() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableStyleElement)this.get_store().add_element_user(CTTableStyleImpl.TABLESTYLEELEMENT$0);
        }
    }
    
    public void removeTableStyleElement(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleImpl.TABLESTYLEELEMENT$0, n);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleImpl.NAME$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTTableStyleImpl.NAME$2);
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleImpl.NAME$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStyleImpl.NAME$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTTableStyleImpl.NAME$2);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTTableStyleImpl.NAME$2);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public boolean getPivot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleImpl.PIVOT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableStyleImpl.PIVOT$4);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPivot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTableStyleImpl.PIVOT$4);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTableStyleImpl.PIVOT$4);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPivot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableStyleImpl.PIVOT$4) != null;
        }
    }
    
    public void setPivot(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleImpl.PIVOT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStyleImpl.PIVOT$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPivot(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTableStyleImpl.PIVOT$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTableStyleImpl.PIVOT$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPivot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableStyleImpl.PIVOT$4);
        }
    }
    
    public boolean getTable() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleImpl.TABLE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableStyleImpl.TABLE$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetTable() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTableStyleImpl.TABLE$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTableStyleImpl.TABLE$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetTable() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableStyleImpl.TABLE$6) != null;
        }
    }
    
    public void setTable(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleImpl.TABLE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStyleImpl.TABLE$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetTable(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTableStyleImpl.TABLE$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTableStyleImpl.TABLE$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetTable() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableStyleImpl.TABLE$6);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleImpl.COUNT$8);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableStyleImpl.COUNT$8);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableStyleImpl.COUNT$8) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleImpl.COUNT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStyleImpl.COUNT$8);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableStyleImpl.COUNT$8);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTTableStyleImpl.COUNT$8);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableStyleImpl.COUNT$8);
        }
    }
    
    static {
        TABLESTYLEELEMENT$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "tableStyleElement");
        NAME$2 = new QName("", "name");
        PIVOT$4 = new QName("", "pivot");
        TABLE$6 = new QName("", "table");
        COUNT$8 = new QName("", "count");
    }
}
