package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STIconSetType;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfvo;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIconSet;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTIconSetImpl extends XmlComplexContentImpl implements CTIconSet
{
    private static final long serialVersionUID = 1L;
    private static final QName CFVO$0;
    private static final QName ICONSET$2;
    private static final QName SHOWVALUE$4;
    private static final QName PERCENT$6;
    private static final QName REVERSE$8;
    
    public CTIconSetImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTCfvo> getCfvoList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CfvoList extends AbstractList<CTCfvo>
            {
                @Override
                public CTCfvo get(final int n) {
                    return CTIconSetImpl.this.getCfvoArray(n);
                }
                
                @Override
                public CTCfvo set(final int n, final CTCfvo ctCfvo) {
                    final CTCfvo cfvoArray = CTIconSetImpl.this.getCfvoArray(n);
                    CTIconSetImpl.this.setCfvoArray(n, ctCfvo);
                    return cfvoArray;
                }
                
                @Override
                public void add(final int n, final CTCfvo ctCfvo) {
                    CTIconSetImpl.this.insertNewCfvo(n).set((XmlObject)ctCfvo);
                }
                
                @Override
                public CTCfvo remove(final int n) {
                    final CTCfvo cfvoArray = CTIconSetImpl.this.getCfvoArray(n);
                    CTIconSetImpl.this.removeCfvo(n);
                    return cfvoArray;
                }
                
                @Override
                public int size() {
                    return CTIconSetImpl.this.sizeOfCfvoArray();
                }
            }
            return new CfvoList();
        }
    }
    
    @Deprecated
    public CTCfvo[] getCfvoArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTIconSetImpl.CFVO$0, (List)list);
            final CTCfvo[] array = new CTCfvo[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCfvo getCfvoArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCfvo ctCfvo = (CTCfvo)this.get_store().find_element_user(CTIconSetImpl.CFVO$0, n);
            if (ctCfvo == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCfvo;
        }
    }
    
    public int sizeOfCfvoArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTIconSetImpl.CFVO$0);
        }
    }
    
    public void setCfvoArray(final CTCfvo[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTIconSetImpl.CFVO$0);
    }
    
    public void setCfvoArray(final int n, final CTCfvo ctCfvo) {
        this.generatedSetterHelperImpl((XmlObject)ctCfvo, CTIconSetImpl.CFVO$0, n, (short)2);
    }
    
    public CTCfvo insertNewCfvo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCfvo)this.get_store().insert_element_user(CTIconSetImpl.CFVO$0, n);
        }
    }
    
    public CTCfvo addNewCfvo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCfvo)this.get_store().add_element_user(CTIconSetImpl.CFVO$0);
        }
    }
    
    public void removeCfvo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTIconSetImpl.CFVO$0, n);
        }
    }
    
    public STIconSetType.Enum getIconSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIconSetImpl.ICONSET$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTIconSetImpl.ICONSET$2);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STIconSetType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STIconSetType xgetIconSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STIconSetType stIconSetType = (STIconSetType)this.get_store().find_attribute_user(CTIconSetImpl.ICONSET$2);
            if (stIconSetType == null) {
                stIconSetType = (STIconSetType)this.get_default_attribute_value(CTIconSetImpl.ICONSET$2);
            }
            return stIconSetType;
        }
    }
    
    public boolean isSetIconSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIconSetImpl.ICONSET$2) != null;
        }
    }
    
    public void setIconSet(final STIconSetType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIconSetImpl.ICONSET$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIconSetImpl.ICONSET$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetIconSet(final STIconSetType stIconSetType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STIconSetType stIconSetType2 = (STIconSetType)this.get_store().find_attribute_user(CTIconSetImpl.ICONSET$2);
            if (stIconSetType2 == null) {
                stIconSetType2 = (STIconSetType)this.get_store().add_attribute_user(CTIconSetImpl.ICONSET$2);
            }
            stIconSetType2.set((XmlObject)stIconSetType);
        }
    }
    
    public void unsetIconSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIconSetImpl.ICONSET$2);
        }
    }
    
    public boolean getShowValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIconSetImpl.SHOWVALUE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTIconSetImpl.SHOWVALUE$4);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTIconSetImpl.SHOWVALUE$4);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTIconSetImpl.SHOWVALUE$4);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIconSetImpl.SHOWVALUE$4) != null;
        }
    }
    
    public void setShowValue(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIconSetImpl.SHOWVALUE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIconSetImpl.SHOWVALUE$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowValue(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTIconSetImpl.SHOWVALUE$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTIconSetImpl.SHOWVALUE$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIconSetImpl.SHOWVALUE$4);
        }
    }
    
    public boolean getPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIconSetImpl.PERCENT$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTIconSetImpl.PERCENT$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTIconSetImpl.PERCENT$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTIconSetImpl.PERCENT$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIconSetImpl.PERCENT$6) != null;
        }
    }
    
    public void setPercent(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIconSetImpl.PERCENT$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIconSetImpl.PERCENT$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPercent(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTIconSetImpl.PERCENT$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTIconSetImpl.PERCENT$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIconSetImpl.PERCENT$6);
        }
    }
    
    public boolean getReverse() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIconSetImpl.REVERSE$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTIconSetImpl.REVERSE$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetReverse() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTIconSetImpl.REVERSE$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTIconSetImpl.REVERSE$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetReverse() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIconSetImpl.REVERSE$8) != null;
        }
    }
    
    public void setReverse(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIconSetImpl.REVERSE$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIconSetImpl.REVERSE$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetReverse(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTIconSetImpl.REVERSE$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTIconSetImpl.REVERSE$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetReverse() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIconSetImpl.REVERSE$8);
        }
    }
    
    static {
        CFVO$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "cfvo");
        ICONSET$2 = new QName("", "iconSet");
        SHOWVALUE$4 = new QName("", "showValue");
        PERCENT$6 = new QName("", "percent");
        REVERSE$8 = new QName("", "reverse");
    }
}
