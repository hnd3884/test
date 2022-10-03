package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.STGuid;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyle;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyleList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTableStyleListImpl extends XmlComplexContentImpl implements CTTableStyleList
{
    private static final long serialVersionUID = 1L;
    private static final QName TBLSTYLE$0;
    private static final QName DEF$2;
    
    public CTTableStyleListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTTableStyle> getTblStyleList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TblStyleList extends AbstractList<CTTableStyle>
            {
                @Override
                public CTTableStyle get(final int n) {
                    return CTTableStyleListImpl.this.getTblStyleArray(n);
                }
                
                @Override
                public CTTableStyle set(final int n, final CTTableStyle ctTableStyle) {
                    final CTTableStyle tblStyleArray = CTTableStyleListImpl.this.getTblStyleArray(n);
                    CTTableStyleListImpl.this.setTblStyleArray(n, ctTableStyle);
                    return tblStyleArray;
                }
                
                @Override
                public void add(final int n, final CTTableStyle ctTableStyle) {
                    CTTableStyleListImpl.this.insertNewTblStyle(n).set((XmlObject)ctTableStyle);
                }
                
                @Override
                public CTTableStyle remove(final int n) {
                    final CTTableStyle tblStyleArray = CTTableStyleListImpl.this.getTblStyleArray(n);
                    CTTableStyleListImpl.this.removeTblStyle(n);
                    return tblStyleArray;
                }
                
                @Override
                public int size() {
                    return CTTableStyleListImpl.this.sizeOfTblStyleArray();
                }
            }
            return new TblStyleList();
        }
    }
    
    @Deprecated
    public CTTableStyle[] getTblStyleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTableStyleListImpl.TBLSTYLE$0, (List)list);
            final CTTableStyle[] array = new CTTableStyle[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTableStyle getTblStyleArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableStyle ctTableStyle = (CTTableStyle)this.get_store().find_element_user(CTTableStyleListImpl.TBLSTYLE$0, n);
            if (ctTableStyle == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTableStyle;
        }
    }
    
    public int sizeOfTblStyleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleListImpl.TBLSTYLE$0);
        }
    }
    
    public void setTblStyleArray(final CTTableStyle[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTableStyleListImpl.TBLSTYLE$0);
    }
    
    public void setTblStyleArray(final int n, final CTTableStyle ctTableStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTableStyle, CTTableStyleListImpl.TBLSTYLE$0, n, (short)2);
    }
    
    public CTTableStyle insertNewTblStyle(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableStyle)this.get_store().insert_element_user(CTTableStyleListImpl.TBLSTYLE$0, n);
        }
    }
    
    public CTTableStyle addNewTblStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableStyle)this.get_store().add_element_user(CTTableStyleListImpl.TBLSTYLE$0);
        }
    }
    
    public void removeTblStyle(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleListImpl.TBLSTYLE$0, n);
        }
    }
    
    public String getDef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleListImpl.DEF$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STGuid xgetDef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STGuid)this.get_store().find_attribute_user(CTTableStyleListImpl.DEF$2);
        }
    }
    
    public void setDef(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableStyleListImpl.DEF$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableStyleListImpl.DEF$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetDef(final STGuid stGuid) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STGuid stGuid2 = (STGuid)this.get_store().find_attribute_user(CTTableStyleListImpl.DEF$2);
            if (stGuid2 == null) {
                stGuid2 = (STGuid)this.get_store().add_attribute_user(CTTableStyleListImpl.DEF$2);
            }
            stGuid2.set((XmlObject)stGuid);
        }
    }
    
    static {
        TBLSTYLE$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tblStyle");
        DEF$2 = new QName("", "def");
    }
}
