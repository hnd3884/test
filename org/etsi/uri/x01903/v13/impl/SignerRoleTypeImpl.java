package org.etsi.uri.x01903.v13.impl;

import org.etsi.uri.x01903.v13.CertifiedRolesListType;
import org.apache.xmlbeans.XmlObject;
import org.etsi.uri.x01903.v13.ClaimedRolesListType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.SignerRoleType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SignerRoleTypeImpl extends XmlComplexContentImpl implements SignerRoleType
{
    private static final long serialVersionUID = 1L;
    private static final QName CLAIMEDROLES$0;
    private static final QName CERTIFIEDROLES$2;
    
    public SignerRoleTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public ClaimedRolesListType getClaimedRoles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ClaimedRolesListType claimedRolesListType = (ClaimedRolesListType)this.get_store().find_element_user(SignerRoleTypeImpl.CLAIMEDROLES$0, 0);
            if (claimedRolesListType == null) {
                return null;
            }
            return claimedRolesListType;
        }
    }
    
    public boolean isSetClaimedRoles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SignerRoleTypeImpl.CLAIMEDROLES$0) != 0;
        }
    }
    
    public void setClaimedRoles(final ClaimedRolesListType claimedRolesListType) {
        this.generatedSetterHelperImpl((XmlObject)claimedRolesListType, SignerRoleTypeImpl.CLAIMEDROLES$0, 0, (short)1);
    }
    
    public ClaimedRolesListType addNewClaimedRoles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ClaimedRolesListType)this.get_store().add_element_user(SignerRoleTypeImpl.CLAIMEDROLES$0);
        }
    }
    
    public void unsetClaimedRoles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SignerRoleTypeImpl.CLAIMEDROLES$0, 0);
        }
    }
    
    public CertifiedRolesListType getCertifiedRoles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CertifiedRolesListType certifiedRolesListType = (CertifiedRolesListType)this.get_store().find_element_user(SignerRoleTypeImpl.CERTIFIEDROLES$2, 0);
            if (certifiedRolesListType == null) {
                return null;
            }
            return certifiedRolesListType;
        }
    }
    
    public boolean isSetCertifiedRoles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(SignerRoleTypeImpl.CERTIFIEDROLES$2) != 0;
        }
    }
    
    public void setCertifiedRoles(final CertifiedRolesListType certifiedRolesListType) {
        this.generatedSetterHelperImpl((XmlObject)certifiedRolesListType, SignerRoleTypeImpl.CERTIFIEDROLES$2, 0, (short)1);
    }
    
    public CertifiedRolesListType addNewCertifiedRoles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CertifiedRolesListType)this.get_store().add_element_user(SignerRoleTypeImpl.CERTIFIEDROLES$2);
        }
    }
    
    public void unsetCertifiedRoles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(SignerRoleTypeImpl.CERTIFIEDROLES$2, 0);
        }
    }
    
    static {
        CLAIMEDROLES$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "ClaimedRoles");
        CERTIFIEDROLES$2 = new QName("http://uri.etsi.org/01903/v1.3.2#", "CertifiedRoles");
    }
}
