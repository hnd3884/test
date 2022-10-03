package org.etsi.uri.x01903.v13.impl;

import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SimpleValue;
import org.etsi.uri.x01903.v13.UnsignedDataObjectPropertiesType;
import org.apache.xmlbeans.XmlObject;
import org.etsi.uri.x01903.v13.UnsignedSignaturePropertiesType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.UnsignedPropertiesType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class UnsignedPropertiesTypeImpl extends XmlComplexContentImpl implements UnsignedPropertiesType
{
    private static final long serialVersionUID = 1L;
    private static final QName UNSIGNEDSIGNATUREPROPERTIES$0;
    private static final QName UNSIGNEDDATAOBJECTPROPERTIES$2;
    private static final QName ID$4;
    
    public UnsignedPropertiesTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public UnsignedSignaturePropertiesType getUnsignedSignatureProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final UnsignedSignaturePropertiesType unsignedSignaturePropertiesType = (UnsignedSignaturePropertiesType)this.get_store().find_element_user(UnsignedPropertiesTypeImpl.UNSIGNEDSIGNATUREPROPERTIES$0, 0);
            if (unsignedSignaturePropertiesType == null) {
                return null;
            }
            return unsignedSignaturePropertiesType;
        }
    }
    
    public boolean isSetUnsignedSignatureProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(UnsignedPropertiesTypeImpl.UNSIGNEDSIGNATUREPROPERTIES$0) != 0;
        }
    }
    
    public void setUnsignedSignatureProperties(final UnsignedSignaturePropertiesType unsignedSignaturePropertiesType) {
        this.generatedSetterHelperImpl((XmlObject)unsignedSignaturePropertiesType, UnsignedPropertiesTypeImpl.UNSIGNEDSIGNATUREPROPERTIES$0, 0, (short)1);
    }
    
    public UnsignedSignaturePropertiesType addNewUnsignedSignatureProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (UnsignedSignaturePropertiesType)this.get_store().add_element_user(UnsignedPropertiesTypeImpl.UNSIGNEDSIGNATUREPROPERTIES$0);
        }
    }
    
    public void unsetUnsignedSignatureProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(UnsignedPropertiesTypeImpl.UNSIGNEDSIGNATUREPROPERTIES$0, 0);
        }
    }
    
    public UnsignedDataObjectPropertiesType getUnsignedDataObjectProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final UnsignedDataObjectPropertiesType unsignedDataObjectPropertiesType = (UnsignedDataObjectPropertiesType)this.get_store().find_element_user(UnsignedPropertiesTypeImpl.UNSIGNEDDATAOBJECTPROPERTIES$2, 0);
            if (unsignedDataObjectPropertiesType == null) {
                return null;
            }
            return unsignedDataObjectPropertiesType;
        }
    }
    
    public boolean isSetUnsignedDataObjectProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(UnsignedPropertiesTypeImpl.UNSIGNEDDATAOBJECTPROPERTIES$2) != 0;
        }
    }
    
    public void setUnsignedDataObjectProperties(final UnsignedDataObjectPropertiesType unsignedDataObjectPropertiesType) {
        this.generatedSetterHelperImpl((XmlObject)unsignedDataObjectPropertiesType, UnsignedPropertiesTypeImpl.UNSIGNEDDATAOBJECTPROPERTIES$2, 0, (short)1);
    }
    
    public UnsignedDataObjectPropertiesType addNewUnsignedDataObjectProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (UnsignedDataObjectPropertiesType)this.get_store().add_element_user(UnsignedPropertiesTypeImpl.UNSIGNEDDATAOBJECTPROPERTIES$2);
        }
    }
    
    public void unsetUnsignedDataObjectProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(UnsignedPropertiesTypeImpl.UNSIGNEDDATAOBJECTPROPERTIES$2, 0);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(UnsignedPropertiesTypeImpl.ID$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlID xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlID)this.get_store().find_attribute_user(UnsignedPropertiesTypeImpl.ID$4);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(UnsignedPropertiesTypeImpl.ID$4) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(UnsignedPropertiesTypeImpl.ID$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(UnsignedPropertiesTypeImpl.ID$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlID xmlID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID xmlID2 = (XmlID)this.get_store().find_attribute_user(UnsignedPropertiesTypeImpl.ID$4);
            if (xmlID2 == null) {
                xmlID2 = (XmlID)this.get_store().add_attribute_user(UnsignedPropertiesTypeImpl.ID$4);
            }
            xmlID2.set((XmlObject)xmlID);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(UnsignedPropertiesTypeImpl.ID$4);
        }
    }
    
    static {
        UNSIGNEDSIGNATUREPROPERTIES$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "UnsignedSignatureProperties");
        UNSIGNEDDATAOBJECTPROPERTIES$2 = new QName("http://uri.etsi.org/01903/v1.3.2#", "UnsignedDataObjectProperties");
        ID$4 = new QName("", "Id");
    }
}
