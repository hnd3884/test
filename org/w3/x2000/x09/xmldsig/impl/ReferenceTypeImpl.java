package org.w3.x2000.x09.xmldsig.impl;

import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlID;
import org.w3.x2000.x09.xmldsig.DigestValueType;
import org.apache.xmlbeans.SimpleValue;
import org.w3.x2000.x09.xmldsig.DigestMethodType;
import org.apache.xmlbeans.XmlObject;
import org.w3.x2000.x09.xmldsig.TransformsType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.w3.x2000.x09.xmldsig.ReferenceType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ReferenceTypeImpl extends XmlComplexContentImpl implements ReferenceType
{
    private static final long serialVersionUID = 1L;
    private static final QName TRANSFORMS$0;
    private static final QName DIGESTMETHOD$2;
    private static final QName DIGESTVALUE$4;
    private static final QName ID$6;
    private static final QName URI$8;
    private static final QName TYPE$10;
    
    public ReferenceTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public TransformsType getTransforms() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final TransformsType transformsType = (TransformsType)this.get_store().find_element_user(ReferenceTypeImpl.TRANSFORMS$0, 0);
            if (transformsType == null) {
                return null;
            }
            return transformsType;
        }
    }
    
    public boolean isSetTransforms() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(ReferenceTypeImpl.TRANSFORMS$0) != 0;
        }
    }
    
    public void setTransforms(final TransformsType transformsType) {
        this.generatedSetterHelperImpl((XmlObject)transformsType, ReferenceTypeImpl.TRANSFORMS$0, 0, (short)1);
    }
    
    public TransformsType addNewTransforms() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (TransformsType)this.get_store().add_element_user(ReferenceTypeImpl.TRANSFORMS$0);
        }
    }
    
    public void unsetTransforms() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(ReferenceTypeImpl.TRANSFORMS$0, 0);
        }
    }
    
    public DigestMethodType getDigestMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final DigestMethodType digestMethodType = (DigestMethodType)this.get_store().find_element_user(ReferenceTypeImpl.DIGESTMETHOD$2, 0);
            if (digestMethodType == null) {
                return null;
            }
            return digestMethodType;
        }
    }
    
    public void setDigestMethod(final DigestMethodType digestMethodType) {
        this.generatedSetterHelperImpl((XmlObject)digestMethodType, ReferenceTypeImpl.DIGESTMETHOD$2, 0, (short)1);
    }
    
    public DigestMethodType addNewDigestMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (DigestMethodType)this.get_store().add_element_user(ReferenceTypeImpl.DIGESTMETHOD$2);
        }
    }
    
    public byte[] getDigestValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(ReferenceTypeImpl.DIGESTVALUE$4, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public DigestValueType xgetDigestValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (DigestValueType)this.get_store().find_element_user(ReferenceTypeImpl.DIGESTVALUE$4, 0);
        }
    }
    
    public void setDigestValue(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(ReferenceTypeImpl.DIGESTVALUE$4, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(ReferenceTypeImpl.DIGESTVALUE$4);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetDigestValue(final DigestValueType digestValueType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            DigestValueType digestValueType2 = (DigestValueType)this.get_store().find_element_user(ReferenceTypeImpl.DIGESTVALUE$4, 0);
            if (digestValueType2 == null) {
                digestValueType2 = (DigestValueType)this.get_store().add_element_user(ReferenceTypeImpl.DIGESTVALUE$4);
            }
            digestValueType2.set((XmlObject)digestValueType);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ReferenceTypeImpl.ID$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlID xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlID)this.get_store().find_attribute_user(ReferenceTypeImpl.ID$6);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ReferenceTypeImpl.ID$6) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ReferenceTypeImpl.ID$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ReferenceTypeImpl.ID$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlID xmlID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID xmlID2 = (XmlID)this.get_store().find_attribute_user(ReferenceTypeImpl.ID$6);
            if (xmlID2 == null) {
                xmlID2 = (XmlID)this.get_store().add_attribute_user(ReferenceTypeImpl.ID$6);
            }
            xmlID2.set((XmlObject)xmlID);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ReferenceTypeImpl.ID$6);
        }
    }
    
    public String getURI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ReferenceTypeImpl.URI$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlAnyURI xgetURI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlAnyURI)this.get_store().find_attribute_user(ReferenceTypeImpl.URI$8);
        }
    }
    
    public boolean isSetURI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ReferenceTypeImpl.URI$8) != null;
        }
    }
    
    public void setURI(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ReferenceTypeImpl.URI$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ReferenceTypeImpl.URI$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetURI(final XmlAnyURI xmlAnyURI) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI xmlAnyURI2 = (XmlAnyURI)this.get_store().find_attribute_user(ReferenceTypeImpl.URI$8);
            if (xmlAnyURI2 == null) {
                xmlAnyURI2 = (XmlAnyURI)this.get_store().add_attribute_user(ReferenceTypeImpl.URI$8);
            }
            xmlAnyURI2.set((XmlObject)xmlAnyURI);
        }
    }
    
    public void unsetURI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ReferenceTypeImpl.URI$8);
        }
    }
    
    public String getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ReferenceTypeImpl.TYPE$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlAnyURI xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlAnyURI)this.get_store().find_attribute_user(ReferenceTypeImpl.TYPE$10);
        }
    }
    
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(ReferenceTypeImpl.TYPE$10) != null;
        }
    }
    
    public void setType(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ReferenceTypeImpl.TYPE$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(ReferenceTypeImpl.TYPE$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetType(final XmlAnyURI xmlAnyURI) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI xmlAnyURI2 = (XmlAnyURI)this.get_store().find_attribute_user(ReferenceTypeImpl.TYPE$10);
            if (xmlAnyURI2 == null) {
                xmlAnyURI2 = (XmlAnyURI)this.get_store().add_attribute_user(ReferenceTypeImpl.TYPE$10);
            }
            xmlAnyURI2.set((XmlObject)xmlAnyURI);
        }
    }
    
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(ReferenceTypeImpl.TYPE$10);
        }
    }
    
    static {
        TRANSFORMS$0 = new QName("http://www.w3.org/2000/09/xmldsig#", "Transforms");
        DIGESTMETHOD$2 = new QName("http://www.w3.org/2000/09/xmldsig#", "DigestMethod");
        DIGESTVALUE$4 = new QName("http://www.w3.org/2000/09/xmldsig#", "DigestValue");
        ID$6 = new QName("", "Id");
        URI$8 = new QName("", "URI");
        TYPE$10 = new QName("", "Type");
    }
}
