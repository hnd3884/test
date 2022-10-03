package org.bouncycastle.jcajce.provider.asymmetric.x509;

import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.util.Strings;
import java.util.Date;
import java.math.BigInteger;
import java.security.cert.CRLException;
import java.util.Enumeration;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.HashSet;
import java.io.IOException;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.Extension;
import java.util.Set;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.TBSCertList;
import java.security.cert.X509CRLEntry;

class X509CRLEntryObject extends X509CRLEntry
{
    private TBSCertList.CRLEntry c;
    private X500Name certificateIssuer;
    private int hashValue;
    private boolean isHashValueSet;
    
    protected X509CRLEntryObject(final TBSCertList.CRLEntry c) {
        this.c = c;
        this.certificateIssuer = null;
    }
    
    protected X509CRLEntryObject(final TBSCertList.CRLEntry c, final boolean b, final X500Name x500Name) {
        this.c = c;
        this.certificateIssuer = this.loadCertificateIssuer(b, x500Name);
    }
    
    public boolean hasUnsupportedCriticalExtension() {
        final Set criticalExtensionOIDs = this.getCriticalExtensionOIDs();
        return criticalExtensionOIDs != null && !criticalExtensionOIDs.isEmpty();
    }
    
    private X500Name loadCertificateIssuer(final boolean b, final X500Name x500Name) {
        if (!b) {
            return null;
        }
        final Extension extension = this.getExtension(Extension.certificateIssuer);
        if (extension == null) {
            return x500Name;
        }
        try {
            final GeneralName[] names = GeneralNames.getInstance(extension.getParsedValue()).getNames();
            for (int i = 0; i < names.length; ++i) {
                if (names[i].getTagNo() == 4) {
                    return X500Name.getInstance(names[i].getName());
                }
            }
            return null;
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    @Override
    public X500Principal getCertificateIssuer() {
        if (this.certificateIssuer == null) {
            return null;
        }
        try {
            return new X500Principal(this.certificateIssuer.getEncoded());
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    private Set getExtensionOIDs(final boolean b) {
        final Extensions extensions = this.c.getExtensions();
        if (extensions != null) {
            final HashSet set = new HashSet();
            final Enumeration oids = extensions.oids();
            while (oids.hasMoreElements()) {
                final ASN1ObjectIdentifier asn1ObjectIdentifier = oids.nextElement();
                if (b == extensions.getExtension(asn1ObjectIdentifier).isCritical()) {
                    set.add(asn1ObjectIdentifier.getId());
                }
            }
            return set;
        }
        return null;
    }
    
    public Set getCriticalExtensionOIDs() {
        return this.getExtensionOIDs(true);
    }
    
    public Set getNonCriticalExtensionOIDs() {
        return this.getExtensionOIDs(false);
    }
    
    private Extension getExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final Extensions extensions = this.c.getExtensions();
        if (extensions != null) {
            return extensions.getExtension(asn1ObjectIdentifier);
        }
        return null;
    }
    
    public byte[] getExtensionValue(final String s) {
        final Extension extension = this.getExtension(new ASN1ObjectIdentifier(s));
        if (extension != null) {
            try {
                return extension.getExtnValue().getEncoded();
            }
            catch (final Exception ex) {
                throw new IllegalStateException("Exception encoding: " + ex.toString());
            }
        }
        return null;
    }
    
    @Override
    public int hashCode() {
        if (!this.isHashValueSet) {
            this.hashValue = super.hashCode();
            this.isHashValueSet = true;
        }
        return this.hashValue;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof X509CRLEntryObject) {
            return this.c.equals(((X509CRLEntryObject)o).c);
        }
        return super.equals(this);
    }
    
    @Override
    public byte[] getEncoded() throws CRLException {
        try {
            return this.c.getEncoded("DER");
        }
        catch (final IOException ex) {
            throw new CRLException(ex.toString());
        }
    }
    
    @Override
    public BigInteger getSerialNumber() {
        return this.c.getUserCertificate().getValue();
    }
    
    @Override
    public Date getRevocationDate() {
        return this.c.getRevocationDate().getDate();
    }
    
    @Override
    public boolean hasExtensions() {
        return this.c.getExtensions() != null;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final String lineSeparator = Strings.lineSeparator();
        sb.append("      userCertificate: ").append(this.getSerialNumber()).append(lineSeparator);
        sb.append("       revocationDate: ").append(this.getRevocationDate()).append(lineSeparator);
        sb.append("       certificateIssuer: ").append(this.getCertificateIssuer()).append(lineSeparator);
        final Extensions extensions = this.c.getExtensions();
        if (extensions != null) {
            final Enumeration oids = extensions.oids();
            if (oids.hasMoreElements()) {
                sb.append("   crlEntryExtensions:").append(lineSeparator);
                while (oids.hasMoreElements()) {
                    final ASN1ObjectIdentifier asn1ObjectIdentifier = oids.nextElement();
                    final Extension extension = extensions.getExtension(asn1ObjectIdentifier);
                    if (extension.getExtnValue() != null) {
                        final ASN1InputStream asn1InputStream = new ASN1InputStream(extension.getExtnValue().getOctets());
                        sb.append("                       critical(").append(extension.isCritical()).append(") ");
                        try {
                            if (asn1ObjectIdentifier.equals(Extension.reasonCode)) {
                                sb.append(CRLReason.getInstance(ASN1Enumerated.getInstance(asn1InputStream.readObject()))).append(lineSeparator);
                            }
                            else if (asn1ObjectIdentifier.equals(Extension.certificateIssuer)) {
                                sb.append("Certificate issuer: ").append(GeneralNames.getInstance(asn1InputStream.readObject())).append(lineSeparator);
                            }
                            else {
                                sb.append(asn1ObjectIdentifier.getId());
                                sb.append(" value = ").append(ASN1Dump.dumpAsString(asn1InputStream.readObject())).append(lineSeparator);
                            }
                        }
                        catch (final Exception ex) {
                            sb.append(asn1ObjectIdentifier.getId());
                            sb.append(" value = ").append("*****").append(lineSeparator);
                        }
                    }
                    else {
                        sb.append(lineSeparator);
                    }
                }
            }
        }
        return sb.toString();
    }
}
