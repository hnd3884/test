package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidation;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidations;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDataValidationsImpl extends XmlComplexContentImpl implements CTDataValidations
{
    private static final long serialVersionUID = 1L;
    private static final QName DATAVALIDATION$0;
    private static final QName DISABLEPROMPTS$2;
    private static final QName XWINDOW$4;
    private static final QName YWINDOW$6;
    private static final QName COUNT$8;
    
    public CTDataValidationsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTDataValidation> getDataValidationList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DataValidationList extends AbstractList<CTDataValidation>
            {
                @Override
                public CTDataValidation get(final int n) {
                    return CTDataValidationsImpl.this.getDataValidationArray(n);
                }
                
                @Override
                public CTDataValidation set(final int n, final CTDataValidation ctDataValidation) {
                    final CTDataValidation dataValidationArray = CTDataValidationsImpl.this.getDataValidationArray(n);
                    CTDataValidationsImpl.this.setDataValidationArray(n, ctDataValidation);
                    return dataValidationArray;
                }
                
                @Override
                public void add(final int n, final CTDataValidation ctDataValidation) {
                    CTDataValidationsImpl.this.insertNewDataValidation(n).set((XmlObject)ctDataValidation);
                }
                
                @Override
                public CTDataValidation remove(final int n) {
                    final CTDataValidation dataValidationArray = CTDataValidationsImpl.this.getDataValidationArray(n);
                    CTDataValidationsImpl.this.removeDataValidation(n);
                    return dataValidationArray;
                }
                
                @Override
                public int size() {
                    return CTDataValidationsImpl.this.sizeOfDataValidationArray();
                }
            }
            return new DataValidationList();
        }
    }
    
    @Deprecated
    public CTDataValidation[] getDataValidationArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTDataValidationsImpl.DATAVALIDATION$0, (List)list);
            final CTDataValidation[] array = new CTDataValidation[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDataValidation getDataValidationArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDataValidation ctDataValidation = (CTDataValidation)this.get_store().find_element_user(CTDataValidationsImpl.DATAVALIDATION$0, n);
            if (ctDataValidation == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctDataValidation;
        }
    }
    
    public int sizeOfDataValidationArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDataValidationsImpl.DATAVALIDATION$0);
        }
    }
    
    public void setDataValidationArray(final CTDataValidation[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTDataValidationsImpl.DATAVALIDATION$0);
    }
    
    public void setDataValidationArray(final int n, final CTDataValidation ctDataValidation) {
        this.generatedSetterHelperImpl((XmlObject)ctDataValidation, CTDataValidationsImpl.DATAVALIDATION$0, n, (short)2);
    }
    
    public CTDataValidation insertNewDataValidation(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDataValidation)this.get_store().insert_element_user(CTDataValidationsImpl.DATAVALIDATION$0, n);
        }
    }
    
    public CTDataValidation addNewDataValidation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDataValidation)this.get_store().add_element_user(CTDataValidationsImpl.DATAVALIDATION$0);
        }
    }
    
    public void removeDataValidation(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDataValidationsImpl.DATAVALIDATION$0, n);
        }
    }
    
    public boolean getDisablePrompts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationsImpl.DISABLEPROMPTS$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDataValidationsImpl.DISABLEPROMPTS$2);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDisablePrompts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTDataValidationsImpl.DISABLEPROMPTS$2);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTDataValidationsImpl.DISABLEPROMPTS$2);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDisablePrompts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataValidationsImpl.DISABLEPROMPTS$2) != null;
        }
    }
    
    public void setDisablePrompts(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationsImpl.DISABLEPROMPTS$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataValidationsImpl.DISABLEPROMPTS$2);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDisablePrompts(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTDataValidationsImpl.DISABLEPROMPTS$2);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTDataValidationsImpl.DISABLEPROMPTS$2);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDisablePrompts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataValidationsImpl.DISABLEPROMPTS$2);
        }
    }
    
    public long getXWindow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationsImpl.XWINDOW$4);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetXWindow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTDataValidationsImpl.XWINDOW$4);
        }
    }
    
    public boolean isSetXWindow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataValidationsImpl.XWINDOW$4) != null;
        }
    }
    
    public void setXWindow(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationsImpl.XWINDOW$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataValidationsImpl.XWINDOW$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetXWindow(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTDataValidationsImpl.XWINDOW$4);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTDataValidationsImpl.XWINDOW$4);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetXWindow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataValidationsImpl.XWINDOW$4);
        }
    }
    
    public long getYWindow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationsImpl.YWINDOW$6);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetYWindow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTDataValidationsImpl.YWINDOW$6);
        }
    }
    
    public boolean isSetYWindow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataValidationsImpl.YWINDOW$6) != null;
        }
    }
    
    public void setYWindow(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationsImpl.YWINDOW$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataValidationsImpl.YWINDOW$6);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetYWindow(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTDataValidationsImpl.YWINDOW$6);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTDataValidationsImpl.YWINDOW$6);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetYWindow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataValidationsImpl.YWINDOW$6);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationsImpl.COUNT$8);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTDataValidationsImpl.COUNT$8);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataValidationsImpl.COUNT$8) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationsImpl.COUNT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataValidationsImpl.COUNT$8);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTDataValidationsImpl.COUNT$8);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTDataValidationsImpl.COUNT$8);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataValidationsImpl.COUNT$8);
        }
    }
    
    static {
        DATAVALIDATION$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "dataValidation");
        DISABLEPROMPTS$2 = new QName("", "disablePrompts");
        XWINDOW$4 = new QName("", "xWindow");
        YWINDOW$6 = new QName("", "yWindow");
        COUNT$8 = new QName("", "count");
    }
}
