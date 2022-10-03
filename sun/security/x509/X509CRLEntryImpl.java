package sun.security.x509;

import java.util.Collection;
import java.util.TreeMap;
import java.util.Collections;
import java.util.Map;
import java.util.Enumeration;
import sun.security.util.ObjectIdentifier;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Set;
import sun.misc.HexDumpEncoder;
import java.security.cert.CRLReason;
import java.io.OutputStream;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import java.security.cert.CRLException;
import sun.security.util.DerValue;
import java.math.BigInteger;
import javax.security.auth.x500.X500Principal;
import java.util.Date;
import java.security.cert.X509CRLEntry;

public class X509CRLEntryImpl extends X509CRLEntry implements Comparable<X509CRLEntryImpl>
{
    private SerialNumber serialNumber;
    private Date revocationDate;
    private CRLExtensions extensions;
    private byte[] revokedCert;
    private X500Principal certIssuer;
    private static final boolean isExplicit = false;
    
    public X509CRLEntryImpl(final BigInteger bigInteger, final Date revocationDate) {
        this.serialNumber = null;
        this.revocationDate = null;
        this.extensions = null;
        this.revokedCert = null;
        this.serialNumber = new SerialNumber(bigInteger);
        this.revocationDate = revocationDate;
    }
    
    public X509CRLEntryImpl(final BigInteger bigInteger, final Date revocationDate, final CRLExtensions extensions) {
        this.serialNumber = null;
        this.revocationDate = null;
        this.extensions = null;
        this.revokedCert = null;
        this.serialNumber = new SerialNumber(bigInteger);
        this.revocationDate = revocationDate;
        this.extensions = extensions;
    }
    
    public X509CRLEntryImpl(final byte[] array) throws CRLException {
        this.serialNumber = null;
        this.revocationDate = null;
        this.extensions = null;
        this.revokedCert = null;
        try {
            this.parse(new DerValue(array));
        }
        catch (final IOException ex) {
            this.revokedCert = null;
            throw new CRLException("Parsing error: " + ex.toString());
        }
    }
    
    public X509CRLEntryImpl(final DerValue derValue) throws CRLException {
        this.serialNumber = null;
        this.revocationDate = null;
        this.extensions = null;
        this.revokedCert = null;
        try {
            this.parse(derValue);
        }
        catch (final IOException ex) {
            this.revokedCert = null;
            throw new CRLException("Parsing error: " + ex.toString());
        }
    }
    
    @Override
    public boolean hasExtensions() {
        return this.extensions != null;
    }
    
    public void encode(final DerOutputStream derOutputStream) throws CRLException {
        try {
            if (this.revokedCert == null) {
                final DerOutputStream derOutputStream2 = new DerOutputStream();
                this.serialNumber.encode(derOutputStream2);
                if (this.revocationDate.getTime() < 2524608000000L) {
                    derOutputStream2.putUTCTime(this.revocationDate);
                }
                else {
                    derOutputStream2.putGeneralizedTime(this.revocationDate);
                }
                if (this.extensions != null) {
                    this.extensions.encode(derOutputStream2, false);
                }
                final DerOutputStream derOutputStream3 = new DerOutputStream();
                derOutputStream3.write((byte)48, derOutputStream2);
                this.revokedCert = derOutputStream3.toByteArray();
            }
            derOutputStream.write(this.revokedCert);
        }
        catch (final IOException ex) {
            throw new CRLException("Encoding error: " + ex.toString());
        }
    }
    
    @Override
    public byte[] getEncoded() throws CRLException {
        return this.getEncoded0().clone();
    }
    
    private byte[] getEncoded0() throws CRLException {
        if (this.revokedCert == null) {
            this.encode(new DerOutputStream());
        }
        return this.revokedCert;
    }
    
    @Override
    public X500Principal getCertificateIssuer() {
        return this.certIssuer;
    }
    
    void setCertificateIssuer(final X500Principal x500Principal, final X500Principal certIssuer) {
        if (x500Principal.equals(certIssuer)) {
            this.certIssuer = null;
        }
        else {
            this.certIssuer = certIssuer;
        }
    }
    
    @Override
    public BigInteger getSerialNumber() {
        return this.serialNumber.getNumber();
    }
    
    @Override
    public Date getRevocationDate() {
        return new Date(this.revocationDate.getTime());
    }
    
    @Override
    public CRLReason getRevocationReason() {
        final Extension extension = this.getExtension(PKIXExtensions.ReasonCode_Id);
        if (extension == null) {
            return null;
        }
        return ((CRLReasonCodeExtension)extension).getReasonCode();
    }
    
    public static CRLReason getRevocationReason(final X509CRLEntry x509CRLEntry) {
        try {
            final byte[] extensionValue = x509CRLEntry.getExtensionValue("2.5.29.21");
            if (extensionValue == null) {
                return null;
            }
            return new CRLReasonCodeExtension(Boolean.FALSE, new DerValue(extensionValue).getOctetString()).getReasonCode();
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    public Integer getReasonCode() throws IOException {
        final Extension extension = this.getExtension(PKIXExtensions.ReasonCode_Id);
        if (extension == null) {
            return null;
        }
        return ((CRLReasonCodeExtension)extension).get("reason");
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.serialNumber.toString());
        sb.append("  On: " + this.revocationDate.toString());
        if (this.certIssuer != null) {
            sb.append("\n    Certificate issuer: " + this.certIssuer);
        }
        if (this.extensions != null) {
            final Extension[] array = this.extensions.getAllExtensions().toArray(new Extension[0]);
            sb.append("\n    CRL Entry Extensions: " + array.length);
            for (int i = 0; i < array.length; ++i) {
                sb.append("\n    [" + (i + 1) + "]: ");
                final Extension extension = array[i];
                try {
                    if (OIDMap.getClass(extension.getExtensionId()) == null) {
                        sb.append(extension.toString());
                        final byte[] extensionValue = extension.getExtensionValue();
                        if (extensionValue != null) {
                            final DerOutputStream derOutputStream = new DerOutputStream();
                            derOutputStream.putOctetString(extensionValue);
                            sb.append("Extension unknown: DER encoded OCTET string =\n" + new HexDumpEncoder().encodeBuffer(derOutputStream.toByteArray()) + "\n");
                        }
                    }
                    else {
                        sb.append(extension.toString());
                    }
                }
                catch (final Exception ex) {
                    sb.append(", Error parsing this extension");
                }
            }
        }
        sb.append("\n");
        return sb.toString();
    }
    
    @Override
    public boolean hasUnsupportedCriticalExtension() {
        return this.extensions != null && this.extensions.hasUnsupportedCriticalExtension();
    }
    
    @Override
    public Set<String> getCriticalExtensionOIDs() {
        if (this.extensions == null) {
            return null;
        }
        final TreeSet set = new TreeSet();
        for (final Extension extension : this.extensions.getAllExtensions()) {
            if (extension.isCritical()) {
                set.add(extension.getExtensionId().toString());
            }
        }
        return set;
    }
    
    @Override
    public Set<String> getNonCriticalExtensionOIDs() {
        if (this.extensions == null) {
            return null;
        }
        final TreeSet set = new TreeSet();
        for (final Extension extension : this.extensions.getAllExtensions()) {
            if (!extension.isCritical()) {
                set.add(extension.getExtensionId().toString());
            }
        }
        return set;
    }
    
    @Override
    public byte[] getExtensionValue(final String s) {
        if (this.extensions == null) {
            return null;
        }
        try {
            final String name = OIDMap.getName(new ObjectIdentifier(s));
            Extension value = null;
            if (name == null) {
                final ObjectIdentifier objectIdentifier = new ObjectIdentifier(s);
                final Enumeration<Extension> elements = this.extensions.getElements();
                while (elements.hasMoreElements()) {
                    final Extension extension = elements.nextElement();
                    if (extension.getExtensionId().equals((Object)objectIdentifier)) {
                        value = extension;
                        break;
                    }
                }
            }
            else {
                value = this.extensions.get(name);
            }
            if (value == null) {
                return null;
            }
            final byte[] extensionValue = value.getExtensionValue();
            if (extensionValue == null) {
                return null;
            }
            final DerOutputStream derOutputStream = new DerOutputStream();
            derOutputStream.putOctetString(extensionValue);
            return derOutputStream.toByteArray();
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public Extension getExtension(final ObjectIdentifier objectIdentifier) {
        if (this.extensions == null) {
            return null;
        }
        return this.extensions.get(OIDMap.getName(objectIdentifier));
    }
    
    private void parse(final DerValue derValue) throws CRLException, IOException {
        if (derValue.tag != 48) {
            throw new CRLException("Invalid encoded RevokedCertificate, starting sequence tag missing.");
        }
        if (derValue.data.available() == 0) {
            throw new CRLException("No data encoded for RevokedCertificates");
        }
        this.revokedCert = derValue.toByteArray();
        this.serialNumber = new SerialNumber(derValue.toDerInputStream().getDerValue());
        final int peekByte = derValue.data.peekByte();
        if ((byte)peekByte == 23) {
            this.revocationDate = derValue.data.getUTCTime();
        }
        else {
            if ((byte)peekByte != 24) {
                throw new CRLException("Invalid encoding for revocation date");
            }
            this.revocationDate = derValue.data.getGeneralizedTime();
        }
        if (derValue.data.available() == 0) {
            return;
        }
        this.extensions = new CRLExtensions(derValue.toDerInputStream());
    }
    
    public static X509CRLEntryImpl toImpl(final X509CRLEntry x509CRLEntry) throws CRLException {
        if (x509CRLEntry instanceof X509CRLEntryImpl) {
            return (X509CRLEntryImpl)x509CRLEntry;
        }
        return new X509CRLEntryImpl(x509CRLEntry.getEncoded());
    }
    
    CertificateIssuerExtension getCertificateIssuerExtension() {
        return (CertificateIssuerExtension)this.getExtension(PKIXExtensions.CertificateIssuer_Id);
    }
    
    public Map<String, java.security.cert.Extension> getExtensions() {
        if (this.extensions == null) {
            return Collections.emptyMap();
        }
        final Collection<Extension> allExtensions = this.extensions.getAllExtensions();
        final TreeMap treeMap = new TreeMap();
        for (final Extension extension : allExtensions) {
            treeMap.put(extension.getId(), extension);
        }
        return treeMap;
    }
    
    @Override
    public int compareTo(final X509CRLEntryImpl x509CRLEntryImpl) {
        final int compareTo = this.getSerialNumber().compareTo(x509CRLEntryImpl.getSerialNumber());
        if (compareTo != 0) {
            return compareTo;
        }
        try {
            final byte[] encoded0 = this.getEncoded0();
            final byte[] encoded2 = x509CRLEntryImpl.getEncoded0();
            for (int n = 0; n < encoded0.length && n < encoded2.length; ++n) {
                final int n2 = encoded0[n] & 0xFF;
                final int n3 = encoded2[n] & 0xFF;
                if (n2 != n3) {
                    return n2 - n3;
                }
            }
            return encoded0.length - encoded2.length;
        }
        catch (final CRLException ex) {
            return -1;
        }
    }
}
