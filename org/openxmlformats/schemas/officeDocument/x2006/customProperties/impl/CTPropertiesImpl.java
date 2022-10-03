package org.openxmlformats.schemas.officeDocument.x2006.customProperties.impl;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPropertiesImpl extends XmlComplexContentImpl implements CTProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName PROPERTY$0;
    
    public CTPropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTProperty> getPropertyList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PropertyList extends AbstractList<CTProperty>
            {
                @Override
                public CTProperty get(final int n) {
                    return CTPropertiesImpl.this.getPropertyArray(n);
                }
                
                @Override
                public CTProperty set(final int n, final CTProperty ctProperty) {
                    final CTProperty propertyArray = CTPropertiesImpl.this.getPropertyArray(n);
                    CTPropertiesImpl.this.setPropertyArray(n, ctProperty);
                    return propertyArray;
                }
                
                @Override
                public void add(final int n, final CTProperty ctProperty) {
                    CTPropertiesImpl.this.insertNewProperty(n).set((XmlObject)ctProperty);
                }
                
                @Override
                public CTProperty remove(final int n) {
                    final CTProperty propertyArray = CTPropertiesImpl.this.getPropertyArray(n);
                    CTPropertiesImpl.this.removeProperty(n);
                    return propertyArray;
                }
                
                @Override
                public int size() {
                    return CTPropertiesImpl.this.sizeOfPropertyArray();
                }
            }
            return new PropertyList();
        }
    }
    
    @Deprecated
    public CTProperty[] getPropertyArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPropertiesImpl.PROPERTY$0, (List)list);
            final CTProperty[] array = new CTProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTProperty getPropertyArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTProperty ctProperty = (CTProperty)this.get_store().find_element_user(CTPropertiesImpl.PROPERTY$0, n);
            if (ctProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctProperty;
        }
    }
    
    public int sizeOfPropertyArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.PROPERTY$0);
        }
    }
    
    public void setPropertyArray(final CTProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPropertiesImpl.PROPERTY$0);
    }
    
    public void setPropertyArray(final int n, final CTProperty ctProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctProperty, CTPropertiesImpl.PROPERTY$0, n, (short)2);
    }
    
    public CTProperty insertNewProperty(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProperty)this.get_store().insert_element_user(CTPropertiesImpl.PROPERTY$0, n);
        }
    }
    
    public CTProperty addNewProperty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProperty)this.get_store().add_element_user(CTPropertiesImpl.PROPERTY$0);
        }
    }
    
    public void removeProperty(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.PROPERTY$0, n);
        }
    }
    
    static {
        PROPERTY$0 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/custom-properties", "property");
    }
}
