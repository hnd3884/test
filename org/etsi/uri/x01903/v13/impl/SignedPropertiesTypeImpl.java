package org.etsi.uri.x01903.v13.impl;

import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SimpleValue;
import org.etsi.uri.x01903.v13.SignedDataObjectPropertiesType;
import org.apache.xmlbeans.XmlObject;
import org.etsi.uri.x01903.v13.SignedSignaturePropertiesType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.SignedPropertiesType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SignedPropertiesTypeImpl extends XmlComplexContentImpl implements SignedPropertiesType
{
    private static final long serialVersionUID = 1L;
    private static final QName SIGNEDSIGNATUREPROPERTIES$0;
    private static final QName SIGNEDDATAOBJECTPROPERTIES$2;
    private static final QName ID$4;
    
    public SignedPropertiesTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public SignedSignaturePropertiesType getSignedSignatureProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SignedSignaturePropertiesType signedSignaturePropertiesType = (SignedSignaturePropertiesType)this.get_store().find_element_user(SignedPropertiesTypeImpl.SIGNEDSIGNATUREPROPERTIES$0, 0);
            if (signedSignaturePropertiesType == null) {
                return null;
            }
            return signedSignaturePropertiesType;
        }
    }
    
    public boolean isSetSignedSignatureProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SignedPropertiesTypeImpl.SIGNEDSIGNATUREPROPERTIES$0) != 0;
        }
    }
    
    public void setSignedSignatureProperties(final SignedSignaturePropertiesType signedSignaturePropertiesType) {
        this.generatedSetterHelperImpl((XmlObject)signedSignaturePropertiesType, SignedPropertiesTypeImpl.SIGNEDSIGNATUREPROPERTIES$0, 0, (short)1);
    }
    
    public SignedSignaturePropertiesType addNewSignedSignatureProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (SignedSignaturePropertiesType)this.get_store().add_element_user(SignedPropertiesTypeImpl.SIGNEDSIGNATUREPROPERTIES$0);
        }
    }
    
    public void unsetSignedSignatureProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SignedPropertiesTypeImpl.SIGNEDSIGNATUREPROPERTIES$0, 0);
        }
    }
    
    public SignedDataObjectPropertiesType getSignedDataObjectProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SignedDataObjectPropertiesType signedDataObjectPropertiesType = (SignedDataObjectPropertiesType)this.get_store().find_element_user(SignedPropertiesTypeImpl.SIGNEDDATAOBJECTPROPERTIES$2, 0);
            if (signedDataObjectPropertiesType == null) {
                return null;
            }
            return signedDataObjectPropertiesType;
        }
    }
    
    public boolean isSetSignedDataObjectProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SignedPropertiesTypeImpl.SIGNEDDATAOBJECTPROPERTIES$2) != 0;
        }
    }
    
    public void setSignedDataObjectProperties(final SignedDataObjectPropertiesType signedDataObjectPropertiesType) {
        this.generatedSetterHelperImpl((XmlObject)signedDataObjectPropertiesType, SignedPropertiesTypeImpl.SIGNEDDATAOBJECTPROPERTIES$2, 0, (short)1);
    }
    
    public SignedDataObjectPropertiesType addNewSignedDataObjectProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (SignedDataObjectPropertiesType)this.get_store().add_element_user(SignedPropertiesTypeImpl.SIGNEDDATAOBJECTPROPERTIES$2);
        }
    }
    
    public void unsetSignedDataObjectProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SignedPropertiesTypeImpl.SIGNEDDATAOBJECTPROPERTIES$2, 0);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SignedPropertiesTypeImpl.ID$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlID xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlID)this.get_store().find_attribute_user(SignedPropertiesTypeImpl.ID$4);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(SignedPropertiesTypeImpl.ID$4) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SignedPropertiesTypeImpl.ID$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(SignedPropertiesTypeImpl.ID$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlID xmlID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID xmlID2 = (XmlID)this.get_store().find_attribute_user(SignedPropertiesTypeImpl.ID$4);
            if (xmlID2 == null) {
                xmlID2 = (XmlID)this.get_store().add_attribute_user(SignedPropertiesTypeImpl.ID$4);
            }
            xmlID2.set((XmlObject)xmlID);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(SignedPropertiesTypeImpl.ID$4);
        }
    }
    
    static {
        SIGNEDSIGNATUREPROPERTIES$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "SignedSignatureProperties");
        SIGNEDDATAOBJECTPROPERTIES$2 = new QName("http://uri.etsi.org/01903/v1.3.2#", "SignedDataObjectProperties");
        ID$4 = new QName("", "Id");
    }
}
