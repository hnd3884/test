package org.bouncycastle.voms;

import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x509.IetfAttrSyntax;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.cert.X509AttributeCertificateHolder;

public class VOMSAttribute
{
    public static final String VOMS_ATTR_OID = "1.3.6.1.4.1.8005.100.100.4";
    private X509AttributeCertificateHolder myAC;
    private String myHostPort;
    private String myVo;
    private List myStringList;
    private List myFQANs;
    
    public VOMSAttribute(final X509AttributeCertificateHolder myAC) {
        this.myStringList = new ArrayList();
        this.myFQANs = new ArrayList();
        if (myAC == null) {
            throw new IllegalArgumentException("VOMSAttribute: AttributeCertificate is NULL");
        }
        this.myAC = myAC;
        final Attribute[] attributes = myAC.getAttributes(new ASN1ObjectIdentifier("1.3.6.1.4.1.8005.100.100.4"));
        if (attributes == null) {
            return;
        }
        try {
            for (int i = 0; i != attributes.length; ++i) {
                final IetfAttrSyntax instance = IetfAttrSyntax.getInstance((Object)attributes[i].getAttributeValues()[0]);
                final String string = ((DERIA5String)instance.getPolicyAuthority().getNames()[0].getName()).getString();
                final int index = string.indexOf("://");
                if (index < 0 || index == string.length() - 1) {
                    throw new IllegalArgumentException("Bad encoding of VOMS policyAuthority : [" + string + "]");
                }
                this.myVo = string.substring(0, index);
                this.myHostPort = string.substring(index + 3);
                if (instance.getValueType() != 1) {
                    throw new IllegalArgumentException("VOMS attribute values are not encoded as octet strings, policyAuthority = " + string);
                }
                final ASN1OctetString[] array = (ASN1OctetString[])instance.getValues();
                for (int j = 0; j != array.length; ++j) {
                    final String s = new String(array[j].getOctets());
                    final FQAN fqan = new FQAN(s);
                    if (!this.myStringList.contains(s) && s.startsWith("/" + this.myVo + "/")) {
                        this.myStringList.add(s);
                        this.myFQANs.add(fqan);
                    }
                }
            }
        }
        catch (final IllegalArgumentException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new IllegalArgumentException("Badly encoded VOMS extension in AC issued by " + myAC.getIssuer());
        }
    }
    
    public X509AttributeCertificateHolder getAC() {
        return this.myAC;
    }
    
    public List getFullyQualifiedAttributes() {
        return this.myStringList;
    }
    
    public List getListOfFQAN() {
        return this.myFQANs;
    }
    
    public String getHostPort() {
        return this.myHostPort;
    }
    
    public String getVO() {
        return this.myVo;
    }
    
    @Override
    public String toString() {
        return "VO      :" + this.myVo + "\nHostPort:" + this.myHostPort + "\nFQANs   :" + this.myFQANs;
    }
    
    public class FQAN
    {
        String fqan;
        String group;
        String role;
        String capability;
        
        public FQAN(final String fqan) {
            this.fqan = fqan;
        }
        
        public FQAN(final String group, final String role, final String capability) {
            this.group = group;
            this.role = role;
            this.capability = capability;
        }
        
        public String getFQAN() {
            if (this.fqan != null) {
                return this.fqan;
            }
            return this.fqan = this.group + "/Role=" + ((this.role != null) ? this.role : "") + ((this.capability != null) ? ("/Capability=" + this.capability) : "");
        }
        
        protected void split() {
            this.fqan.length();
            final int index = this.fqan.indexOf("/Role=");
            if (index < 0) {
                return;
            }
            this.group = this.fqan.substring(0, index);
            final int index2 = this.fqan.indexOf("/Capability=", index + 6);
            final String s = (index2 < 0) ? this.fqan.substring(index + 6) : this.fqan.substring(index + 6, index2);
            this.role = ((s.length() == 0) ? null : s);
            final String s2 = (index2 < 0) ? null : this.fqan.substring(index2 + 12);
            this.capability = ((s2 == null || s2.length() == 0) ? null : s2);
        }
        
        public String getGroup() {
            if (this.group == null && this.fqan != null) {
                this.split();
            }
            return this.group;
        }
        
        public String getRole() {
            if (this.group == null && this.fqan != null) {
                this.split();
            }
            return this.role;
        }
        
        public String getCapability() {
            if (this.group == null && this.fqan != null) {
                this.split();
            }
            return this.capability;
        }
        
        @Override
        public String toString() {
            return this.getFQAN();
        }
    }
}
