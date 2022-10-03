package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyle;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyles;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTableStylesImpl extends XmlComplexContentImpl implements CTTableStyles
{
    private static final long serialVersionUID = 1L;
    private static final QName TABLESTYLE$0;
    private static final QName COUNT$2;
    private static final QName DEFAULTTABLESTYLE$4;
    private static final QName DEFAULTPIVOTSTYLE$6;
    
    public CTTableStylesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTTableStyle> getTableStyleList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TableStyleList extends AbstractList<CTTableStyle>
            {
                @Override
                public CTTableStyle get(final int n) {
                    return CTTableStylesImpl.this.getTableStyleArray(n);
                }
                
                @Override
                public CTTableStyle set(final int n, final CTTableStyle ctTableStyle) {
                    final CTTableStyle tableStyleArray = CTTableStylesImpl.this.getTableStyleArray(n);
                    CTTableStylesImpl.this.setTableStyleArray(n, ctTableStyle);
                    return tableStyleArray;
                }
                
                @Override
                public void add(final int n, final CTTableStyle ctTableStyle) {
                    CTTableStylesImpl.this.insertNewTableStyle(n).set((XmlObject)ctTableStyle);
                }
                
                @Override
                public CTTableStyle remove(final int n) {
                    final CTTableStyle tableStyleArray = CTTableStylesImpl.this.getTableStyleArray(n);
                    CTTableStylesImpl.this.removeTableStyle(n);
                    return tableStyleArray;
                }
                
                @Override
                public int size() {
                    return CTTableStylesImpl.this.sizeOfTableStyleArray();
                }
            }
            return new TableStyleList();
        }
    }
    
    @Deprecated
    public CTTableStyle[] getTableStyleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTableStylesImpl.TABLESTYLE$0, (List)list);
            final CTTableStyle[] array = new CTTableStyle[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTableStyle getTableStyleArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableStyle ctTableStyle = (CTTableStyle)this.get_store().find_element_user(CTTableStylesImpl.TABLESTYLE$0, n);
            if (ctTableStyle == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTableStyle;
        }
    }
    
    public int sizeOfTableStyleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStylesImpl.TABLESTYLE$0);
        }
    }
    
    public void setTableStyleArray(final CTTableStyle[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTableStylesImpl.TABLESTYLE$0);
    }
    
    public void setTableStyleArray(final int n, final CTTableStyle ctTableStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTableStyle, CTTableStylesImpl.TABLESTYLE$0, n, (short)2);
    }
    
    public CTTableStyle insertNewTableStyle(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableStyle)this.get_store().insert_element_user(CTTableStylesImpl.TABLESTYLE$0, n);
        }
    }
    
    public CTTableStyle addNewTableStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableStyle)this.get_store().add_element_user(CTTableStylesImpl.TABLESTYLE$0);
        }
    }
    
    public void removeTableStyle(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStylesImpl.TABLESTYLE$0, n);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStylesImpl.COUNT$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableStylesImpl.COUNT$2);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableStylesImpl.COUNT$2) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStylesImpl.COUNT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStylesImpl.COUNT$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableStylesImpl.COUNT$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTTableStylesImpl.COUNT$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableStylesImpl.COUNT$2);
        }
    }
    
    public String getDefaultTableStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStylesImpl.DEFAULTTABLESTYLE$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetDefaultTableStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTTableStylesImpl.DEFAULTTABLESTYLE$4);
        }
    }
    
    public boolean isSetDefaultTableStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableStylesImpl.DEFAULTTABLESTYLE$4) != null;
        }
    }
    
    public void setDefaultTableStyle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStylesImpl.DEFAULTTABLESTYLE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStylesImpl.DEFAULTTABLESTYLE$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetDefaultTableStyle(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTTableStylesImpl.DEFAULTTABLESTYLE$4);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTTableStylesImpl.DEFAULTTABLESTYLE$4);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetDefaultTableStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableStylesImpl.DEFAULTTABLESTYLE$4);
        }
    }
    
    public String getDefaultPivotStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStylesImpl.DEFAULTPIVOTSTYLE$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetDefaultPivotStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTTableStylesImpl.DEFAULTPIVOTSTYLE$6);
        }
    }
    
    public boolean isSetDefaultPivotStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableStylesImpl.DEFAULTPIVOTSTYLE$6) != null;
        }
    }
    
    public void setDefaultPivotStyle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStylesImpl.DEFAULTPIVOTSTYLE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStylesImpl.DEFAULTPIVOTSTYLE$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetDefaultPivotStyle(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTTableStylesImpl.DEFAULTPIVOTSTYLE$6);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTTableStylesImpl.DEFAULTPIVOTSTYLE$6);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetDefaultPivotStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableStylesImpl.DEFAULTPIVOTSTYLE$6);
        }
    }
    
    static {
        TABLESTYLE$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "tableStyle");
        COUNT$2 = new QName("", "count");
        DEFAULTTABLESTYLE$4 = new QName("", "defaultTableStyle");
        DEFAULTPIVOTSTYLE$6 = new QName("", "defaultPivotStyle");
    }
}
