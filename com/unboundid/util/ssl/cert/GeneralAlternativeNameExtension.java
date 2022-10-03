package com.unboundid.util.ssl.cert;

import java.util.Iterator;
import java.net.InetAddress;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.util.ObjectPair;
import java.util.List;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.OID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public abstract class GeneralAlternativeNameExtension extends X509CertificateExtension
{
    private static final long serialVersionUID = -1076071031835517176L;
    private final GeneralNames generalNames;
    
    protected GeneralAlternativeNameExtension(final OID oid, final boolean isCritical, final GeneralNames generalNames) throws CertException {
        super(oid, isCritical, generalNames.encode().encode());
        this.generalNames = generalNames;
    }
    
    protected GeneralAlternativeNameExtension(final X509CertificateExtension extension) throws CertException {
        super(extension);
        try {
            this.generalNames = new GeneralNames(ASN1Element.decode(extension.getValue()));
        }
        catch (final Exception e) {
            Debug.debugException(e);
            String name;
            if (extension.getOID().equals(SubjectAlternativeNameExtension.SUBJECT_ALTERNATIVE_NAME_OID)) {
                name = CertMessages.INFO_SUBJECT_ALT_NAME_EXTENSION_NAME.get();
            }
            else if (extension.getOID().equals(IssuerAlternativeNameExtension.ISSUER_ALTERNATIVE_NAME_OID)) {
                name = CertMessages.INFO_ISSUER_ALT_NAME_EXTENSION_NAME.get();
            }
            else {
                name = extension.getOID().toString();
            }
            throw new CertException(CertMessages.ERR_GENERAL_ALT_NAME_EXTENSION_CANNOT_PARSE.get(String.valueOf(extension), name, StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public final GeneralNames getGeneralNames() {
        return this.generalNames;
    }
    
    public final List<ObjectPair<OID, ASN1Element>> getOtherNames() {
        return this.generalNames.getOtherNames();
    }
    
    public final List<String> getRFC822Names() {
        return this.generalNames.getRFC822Names();
    }
    
    public final List<String> getDNSNames() {
        return this.generalNames.getDNSNames();
    }
    
    public final List<ASN1Element> getX400Addresses() {
        return this.generalNames.getX400Addresses();
    }
    
    public final List<DN> getDirectoryNames() {
        return this.generalNames.getDirectoryNames();
    }
    
    public final List<ASN1Element> getEDIPartyNames() {
        return this.generalNames.getEDIPartyNames();
    }
    
    public final List<String> getUniformResourceIdentifiers() {
        return this.generalNames.getUniformResourceIdentifiers();
    }
    
    public final List<InetAddress> getIPAddresses() {
        return this.generalNames.getIPAddresses();
    }
    
    public final List<OID> getRegisteredIDs() {
        return this.generalNames.getRegisteredIDs();
    }
    
    protected void toString(final String extensionName, final StringBuilder buffer) {
        buffer.append(extensionName);
        buffer.append("(oid='");
        buffer.append(this.getOID());
        buffer.append("', isCritical=");
        buffer.append(this.isCritical());
        if (!this.getDNSNames().isEmpty()) {
            buffer.append(", dnsNames={");
            final Iterator<String> iterator = this.getDNSNames().iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator.next());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
        }
        if (!this.getIPAddresses().isEmpty()) {
            buffer.append(", ipAddresses={");
            final Iterator<InetAddress> iterator2 = this.getIPAddresses().iterator();
            while (iterator2.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator2.next().getHostAddress());
                buffer.append('\'');
                if (iterator2.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
        }
        if (!this.getRFC822Names().isEmpty()) {
            buffer.append(", rfc822Names={");
            final Iterator<String> iterator = this.getRFC822Names().iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator.next());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
        }
        if (!this.getDirectoryNames().isEmpty()) {
            buffer.append(", directoryNames={");
            final Iterator<DN> iterator3 = this.getDirectoryNames().iterator();
            while (iterator3.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator3.next());
                buffer.append('\'');
                if (iterator3.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
        }
        if (!this.getUniformResourceIdentifiers().isEmpty()) {
            buffer.append(", uniformResourceIdentifiers={");
            final Iterator<String> iterator = this.getUniformResourceIdentifiers().iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator.next());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
        }
        if (!this.getRegisteredIDs().isEmpty()) {
            buffer.append(", registeredIDs={");
            final Iterator<OID> iterator4 = this.getRegisteredIDs().iterator();
            while (iterator4.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator4.next());
                buffer.append('\'');
                if (iterator4.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append('}');
        }
        if (!this.getOtherNames().isEmpty()) {
            buffer.append(", otherNameCount=");
            buffer.append(this.getOtherNames().size());
        }
        if (!this.getX400Addresses().isEmpty()) {
            buffer.append(", x400AddressCount=");
            buffer.append(this.getX400Addresses().size());
        }
        if (!this.getEDIPartyNames().isEmpty()) {
            buffer.append(", ediPartyNameCount=");
            buffer.append(this.getEDIPartyNames().size());
        }
        buffer.append(')');
    }
}
