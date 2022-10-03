package org.etsi.uri.x01903.v13.impl;

import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.SimpleValue;
import org.etsi.uri.x01903.v13.UnsignedPropertiesType;
import org.apache.xmlbeans.XmlObject;
import org.etsi.uri.x01903.v13.SignedPropertiesType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.QualifyingPropertiesType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class QualifyingPropertiesTypeImpl extends XmlComplexContentImpl implements QualifyingPropertiesType
{
    private static final long serialVersionUID = 1L;
    private static final QName SIGNEDPROPERTIES$0;
    private static final QName UNSIGNEDPROPERTIES$2;
    private static final QName TARGET$4;
    private static final QName ID$6;
    
    public QualifyingPropertiesTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public SignedPropertiesType getSignedProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SignedPropertiesType signedPropertiesType = (SignedPropertiesType)this.get_store().find_element_user(QualifyingPropertiesTypeImpl.SIGNEDPROPERTIES$0, 0);
            if (signedPropertiesType == null) {
                return null;
            }
            return signedPropertiesType;
        }
    }
    
    public boolean isSetSignedProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(QualifyingPropertiesTypeImpl.SIGNEDPROPERTIES$0) != 0;
        }
    }
    
    public void setSignedProperties(final SignedPropertiesType signedPropertiesType) {
        this.generatedSetterHelperImpl((XmlObject)signedPropertiesType, QualifyingPropertiesTypeImpl.SIGNEDPROPERTIES$0, 0, (short)1);
    }
    
    public SignedPropertiesType addNewSignedProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (SignedPropertiesType)this.get_store().add_element_user(QualifyingPropertiesTypeImpl.SIGNEDPROPERTIES$0);
        }
    }
    
    public void unsetSignedProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(QualifyingPropertiesTypeImpl.SIGNEDPROPERTIES$0, 0);
        }
    }
    
    public UnsignedPropertiesType getUnsignedProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final UnsignedPropertiesType unsignedPropertiesType = (UnsignedPropertiesType)this.get_store().find_element_user(QualifyingPropertiesTypeImpl.UNSIGNEDPROPERTIES$2, 0);
            if (unsignedPropertiesType == null) {
                return null;
            }
            return unsignedPropertiesType;
        }
    }
    
    public boolean isSetUnsignedProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(QualifyingPropertiesTypeImpl.UNSIGNEDPROPERTIES$2) != 0;
        }
    }
    
    public void setUnsignedProperties(final UnsignedPropertiesType unsignedPropertiesType) {
        this.generatedSetterHelperImpl((XmlObject)unsignedPropertiesType, QualifyingPropertiesTypeImpl.UNSIGNEDPROPERTIES$2, 0, (short)1);
    }
    
    public UnsignedPropertiesType addNewUnsignedProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (UnsignedPropertiesType)this.get_store().add_element_user(QualifyingPropertiesTypeImpl.UNSIGNEDPROPERTIES$2);
        }
    }
    
    public void unsetUnsignedProperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(QualifyingPropertiesTypeImpl.UNSIGNEDPROPERTIES$2, 0);
        }
    }
    
    public String getTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(QualifyingPropertiesTypeImpl.TARGET$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlAnyURI xgetTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlAnyURI)this.get_store().find_attribute_user(QualifyingPropertiesTypeImpl.TARGET$4);
        }
    }
    
    public void setTarget(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(QualifyingPropertiesTypeImpl.TARGET$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(QualifyingPropertiesTypeImpl.TARGET$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTarget(final XmlAnyURI xmlAnyURI) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI xmlAnyURI2 = (XmlAnyURI)this.get_store().find_attribute_user(QualifyingPropertiesTypeImpl.TARGET$4);
            if (xmlAnyURI2 == null) {
                xmlAnyURI2 = (XmlAnyURI)this.get_store().add_attribute_user(QualifyingPropertiesTypeImpl.TARGET$4);
            }
            xmlAnyURI2.set((XmlObject)xmlAnyURI);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(QualifyingPropertiesTypeImpl.ID$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlID xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlID)this.get_store().find_attribute_user(QualifyingPropertiesTypeImpl.ID$6);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(QualifyingPropertiesTypeImpl.ID$6) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(QualifyingPropertiesTypeImpl.ID$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(QualifyingPropertiesTypeImpl.ID$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlID xmlID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID xmlID2 = (XmlID)this.get_store().find_attribute_user(QualifyingPropertiesTypeImpl.ID$6);
            if (xmlID2 == null) {
                xmlID2 = (XmlID)this.get_store().add_attribute_user(QualifyingPropertiesTypeImpl.ID$6);
            }
            xmlID2.set((XmlObject)xmlID);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(QualifyingPropertiesTypeImpl.ID$6);
        }
    }
    
    static {
        SIGNEDPROPERTIES$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "SignedProperties");
        UNSIGNEDPROPERTIES$2 = new QName("http://uri.etsi.org/01903/v1.3.2#", "UnsignedProperties");
        TARGET$4 = new QName("", "Target");
        ID$6 = new QName("", "Id");
    }
}
