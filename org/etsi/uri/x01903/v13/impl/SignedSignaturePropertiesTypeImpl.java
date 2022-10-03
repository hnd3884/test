package org.etsi.uri.x01903.v13.impl;

import org.apache.xmlbeans.XmlID;
import org.etsi.uri.x01903.v13.SignerRoleType;
import org.etsi.uri.x01903.v13.SignatureProductionPlaceType;
import org.etsi.uri.x01903.v13.SignaturePolicyIdentifierType;
import org.etsi.uri.x01903.v13.CertIDListType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlDateTime;
import org.apache.xmlbeans.SimpleValue;
import java.util.Calendar;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.SignedSignaturePropertiesType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SignedSignaturePropertiesTypeImpl extends XmlComplexContentImpl implements SignedSignaturePropertiesType
{
    private static final long serialVersionUID = 1L;
    private static final QName SIGNINGTIME$0;
    private static final QName SIGNINGCERTIFICATE$2;
    private static final QName SIGNATUREPOLICYIDENTIFIER$4;
    private static final QName SIGNATUREPRODUCTIONPLACE$6;
    private static final QName SIGNERROLE$8;
    private static final QName ID$10;
    
    public SignedSignaturePropertiesTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public Calendar getSigningTime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(SignedSignaturePropertiesTypeImpl.SIGNINGTIME$0, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getCalendarValue();
        }
    }
    
    public XmlDateTime xgetSigningTime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDateTime)this.get_store().find_element_user(SignedSignaturePropertiesTypeImpl.SIGNINGTIME$0, 0);
        }
    }
    
    public boolean isSetSigningTime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SignedSignaturePropertiesTypeImpl.SIGNINGTIME$0) != 0;
        }
    }
    
    public void setSigningTime(final Calendar calendarValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(SignedSignaturePropertiesTypeImpl.SIGNINGTIME$0, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(SignedSignaturePropertiesTypeImpl.SIGNINGTIME$0);
            }
            simpleValue.setCalendarValue(calendarValue);
        }
    }
    
    public void xsetSigningTime(final XmlDateTime xmlDateTime) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDateTime xmlDateTime2 = (XmlDateTime)this.get_store().find_element_user(SignedSignaturePropertiesTypeImpl.SIGNINGTIME$0, 0);
            if (xmlDateTime2 == null) {
                xmlDateTime2 = (XmlDateTime)this.get_store().add_element_user(SignedSignaturePropertiesTypeImpl.SIGNINGTIME$0);
            }
            xmlDateTime2.set((XmlObject)xmlDateTime);
        }
    }
    
    public void unsetSigningTime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SignedSignaturePropertiesTypeImpl.SIGNINGTIME$0, 0);
        }
    }
    
    public CertIDListType getSigningCertificate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CertIDListType certIDListType = (CertIDListType)this.get_store().find_element_user(SignedSignaturePropertiesTypeImpl.SIGNINGCERTIFICATE$2, 0);
            if (certIDListType == null) {
                return null;
            }
            return certIDListType;
        }
    }
    
    public boolean isSetSigningCertificate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SignedSignaturePropertiesTypeImpl.SIGNINGCERTIFICATE$2) != 0;
        }
    }
    
    public void setSigningCertificate(final CertIDListType certIDListType) {
        this.generatedSetterHelperImpl((XmlObject)certIDListType, SignedSignaturePropertiesTypeImpl.SIGNINGCERTIFICATE$2, 0, (short)1);
    }
    
    public CertIDListType addNewSigningCertificate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CertIDListType)this.get_store().add_element_user(SignedSignaturePropertiesTypeImpl.SIGNINGCERTIFICATE$2);
        }
    }
    
    public void unsetSigningCertificate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SignedSignaturePropertiesTypeImpl.SIGNINGCERTIFICATE$2, 0);
        }
    }
    
    public SignaturePolicyIdentifierType getSignaturePolicyIdentifier() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SignaturePolicyIdentifierType signaturePolicyIdentifierType = (SignaturePolicyIdentifierType)this.get_store().find_element_user(SignedSignaturePropertiesTypeImpl.SIGNATUREPOLICYIDENTIFIER$4, 0);
            if (signaturePolicyIdentifierType == null) {
                return null;
            }
            return signaturePolicyIdentifierType;
        }
    }
    
    public boolean isSetSignaturePolicyIdentifier() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SignedSignaturePropertiesTypeImpl.SIGNATUREPOLICYIDENTIFIER$4) != 0;
        }
    }
    
    public void setSignaturePolicyIdentifier(final SignaturePolicyIdentifierType signaturePolicyIdentifierType) {
        this.generatedSetterHelperImpl((XmlObject)signaturePolicyIdentifierType, SignedSignaturePropertiesTypeImpl.SIGNATUREPOLICYIDENTIFIER$4, 0, (short)1);
    }
    
    public SignaturePolicyIdentifierType addNewSignaturePolicyIdentifier() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (SignaturePolicyIdentifierType)this.get_store().add_element_user(SignedSignaturePropertiesTypeImpl.SIGNATUREPOLICYIDENTIFIER$4);
        }
    }
    
    public void unsetSignaturePolicyIdentifier() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SignedSignaturePropertiesTypeImpl.SIGNATUREPOLICYIDENTIFIER$4, 0);
        }
    }
    
    public SignatureProductionPlaceType getSignatureProductionPlace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SignatureProductionPlaceType signatureProductionPlaceType = (SignatureProductionPlaceType)this.get_store().find_element_user(SignedSignaturePropertiesTypeImpl.SIGNATUREPRODUCTIONPLACE$6, 0);
            if (signatureProductionPlaceType == null) {
                return null;
            }
            return signatureProductionPlaceType;
        }
    }
    
    public boolean isSetSignatureProductionPlace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SignedSignaturePropertiesTypeImpl.SIGNATUREPRODUCTIONPLACE$6) != 0;
        }
    }
    
    public void setSignatureProductionPlace(final SignatureProductionPlaceType signatureProductionPlaceType) {
        this.generatedSetterHelperImpl((XmlObject)signatureProductionPlaceType, SignedSignaturePropertiesTypeImpl.SIGNATUREPRODUCTIONPLACE$6, 0, (short)1);
    }
    
    public SignatureProductionPlaceType addNewSignatureProductionPlace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (SignatureProductionPlaceType)this.get_store().add_element_user(SignedSignaturePropertiesTypeImpl.SIGNATUREPRODUCTIONPLACE$6);
        }
    }
    
    public void unsetSignatureProductionPlace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SignedSignaturePropertiesTypeImpl.SIGNATUREPRODUCTIONPLACE$6, 0);
        }
    }
    
    public SignerRoleType getSignerRole() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SignerRoleType signerRoleType = (SignerRoleType)this.get_store().find_element_user(SignedSignaturePropertiesTypeImpl.SIGNERROLE$8, 0);
            if (signerRoleType == null) {
                return null;
            }
            return signerRoleType;
        }
    }
    
    public boolean isSetSignerRole() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SignedSignaturePropertiesTypeImpl.SIGNERROLE$8) != 0;
        }
    }
    
    public void setSignerRole(final SignerRoleType signerRoleType) {
        this.generatedSetterHelperImpl((XmlObject)signerRoleType, SignedSignaturePropertiesTypeImpl.SIGNERROLE$8, 0, (short)1);
    }
    
    public SignerRoleType addNewSignerRole() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (SignerRoleType)this.get_store().add_element_user(SignedSignaturePropertiesTypeImpl.SIGNERROLE$8);
        }
    }
    
    public void unsetSignerRole() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SignedSignaturePropertiesTypeImpl.SIGNERROLE$8, 0);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SignedSignaturePropertiesTypeImpl.ID$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlID xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlID)this.get_store().find_attribute_user(SignedSignaturePropertiesTypeImpl.ID$10);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(SignedSignaturePropertiesTypeImpl.ID$10) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(SignedSignaturePropertiesTypeImpl.ID$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(SignedSignaturePropertiesTypeImpl.ID$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlID xmlID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID xmlID2 = (XmlID)this.get_store().find_attribute_user(SignedSignaturePropertiesTypeImpl.ID$10);
            if (xmlID2 == null) {
                xmlID2 = (XmlID)this.get_store().add_attribute_user(SignedSignaturePropertiesTypeImpl.ID$10);
            }
            xmlID2.set((XmlObject)xmlID);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(SignedSignaturePropertiesTypeImpl.ID$10);
        }
    }
    
    static {
        SIGNINGTIME$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "SigningTime");
        SIGNINGCERTIFICATE$2 = new QName("http://uri.etsi.org/01903/v1.3.2#", "SigningCertificate");
        SIGNATUREPOLICYIDENTIFIER$4 = new QName("http://uri.etsi.org/01903/v1.3.2#", "SignaturePolicyIdentifier");
        SIGNATUREPRODUCTIONPLACE$6 = new QName("http://uri.etsi.org/01903/v1.3.2#", "SignatureProductionPlace");
        SIGNERROLE$8 = new QName("http://uri.etsi.org/01903/v1.3.2#", "SignerRole");
        ID$10 = new QName("", "Id");
    }
}
