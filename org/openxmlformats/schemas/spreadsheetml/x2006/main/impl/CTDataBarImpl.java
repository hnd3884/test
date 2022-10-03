package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfvo;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataBar;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDataBarImpl extends XmlComplexContentImpl implements CTDataBar
{
    private static final long serialVersionUID = 1L;
    private static final QName CFVO$0;
    private static final QName COLOR$2;
    private static final QName MINLENGTH$4;
    private static final QName MAXLENGTH$6;
    private static final QName SHOWVALUE$8;
    
    public CTDataBarImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTCfvo> getCfvoList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CfvoList extends AbstractList<CTCfvo>
            {
                @Override
                public CTCfvo get(final int n) {
                    return CTDataBarImpl.this.getCfvoArray(n);
                }
                
                @Override
                public CTCfvo set(final int n, final CTCfvo ctCfvo) {
                    final CTCfvo cfvoArray = CTDataBarImpl.this.getCfvoArray(n);
                    CTDataBarImpl.this.setCfvoArray(n, ctCfvo);
                    return cfvoArray;
                }
                
                @Override
                public void add(final int n, final CTCfvo ctCfvo) {
                    CTDataBarImpl.this.insertNewCfvo(n).set((XmlObject)ctCfvo);
                }
                
                @Override
                public CTCfvo remove(final int n) {
                    final CTCfvo cfvoArray = CTDataBarImpl.this.getCfvoArray(n);
                    CTDataBarImpl.this.removeCfvo(n);
                    return cfvoArray;
                }
                
                @Override
                public int size() {
                    return CTDataBarImpl.this.sizeOfCfvoArray();
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
            this.get_store().find_all_element_users(CTDataBarImpl.CFVO$0, (List)list);
            final CTCfvo[] array = new CTCfvo[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCfvo getCfvoArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCfvo ctCfvo = (CTCfvo)this.get_store().find_element_user(CTDataBarImpl.CFVO$0, n);
            if (ctCfvo == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCfvo;
        }
    }
    
    public int sizeOfCfvoArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDataBarImpl.CFVO$0);
        }
    }
    
    public void setCfvoArray(final CTCfvo[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTDataBarImpl.CFVO$0);
    }
    
    public void setCfvoArray(final int n, final CTCfvo ctCfvo) {
        this.generatedSetterHelperImpl((XmlObject)ctCfvo, CTDataBarImpl.CFVO$0, n, (short)2);
    }
    
    public CTCfvo insertNewCfvo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCfvo)this.get_store().insert_element_user(CTDataBarImpl.CFVO$0, n);
        }
    }
    
    public CTCfvo addNewCfvo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCfvo)this.get_store().add_element_user(CTDataBarImpl.CFVO$0);
        }
    }
    
    public void removeCfvo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDataBarImpl.CFVO$0, n);
        }
    }
    
    public CTColor getColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTDataBarImpl.COLOR$2, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public void setColor(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTDataBarImpl.COLOR$2, 0, (short)1);
    }
    
    public CTColor addNewColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTDataBarImpl.COLOR$2);
        }
    }
    
    public long getMinLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataBarImpl.MINLENGTH$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDataBarImpl.MINLENGTH$4);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetMinLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTDataBarImpl.MINLENGTH$4);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTDataBarImpl.MINLENGTH$4);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetMinLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataBarImpl.MINLENGTH$4) != null;
        }
    }
    
    public void setMinLength(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataBarImpl.MINLENGTH$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataBarImpl.MINLENGTH$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetMinLength(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTDataBarImpl.MINLENGTH$4);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTDataBarImpl.MINLENGTH$4);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetMinLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataBarImpl.MINLENGTH$4);
        }
    }
    
    public long getMaxLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataBarImpl.MAXLENGTH$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDataBarImpl.MAXLENGTH$6);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetMaxLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTDataBarImpl.MAXLENGTH$6);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTDataBarImpl.MAXLENGTH$6);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetMaxLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataBarImpl.MAXLENGTH$6) != null;
        }
    }
    
    public void setMaxLength(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataBarImpl.MAXLENGTH$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataBarImpl.MAXLENGTH$6);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetMaxLength(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTDataBarImpl.MAXLENGTH$6);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTDataBarImpl.MAXLENGTH$6);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetMaxLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataBarImpl.MAXLENGTH$6);
        }
    }
    
    public boolean getShowValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataBarImpl.SHOWVALUE$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDataBarImpl.SHOWVALUE$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTDataBarImpl.SHOWVALUE$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTDataBarImpl.SHOWVALUE$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataBarImpl.SHOWVALUE$8) != null;
        }
    }
    
    public void setShowValue(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataBarImpl.SHOWVALUE$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataBarImpl.SHOWVALUE$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowValue(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTDataBarImpl.SHOWVALUE$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTDataBarImpl.SHOWVALUE$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataBarImpl.SHOWVALUE$8);
        }
    }
    
    static {
        CFVO$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "cfvo");
        COLOR$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "color");
        MINLENGTH$4 = new QName("", "minLength");
        MAXLENGTH$6 = new QName("", "maxLength");
        SHOWVALUE$8 = new QName("", "showValue");
    }
}
