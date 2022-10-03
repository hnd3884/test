package org.bouncycastle.cert;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.operator.ContentVerifierProvider;
import java.util.Set;
import java.util.List;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import org.bouncycastle.asn1.x509.TBSCertList;
import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import java.io.ByteArrayInputStream;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.ASN1Primitive;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1InputStream;
import java.io.InputStream;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.CertificateList;
import java.io.Serializable;
import org.bouncycastle.util.Encodable;

public class X509CRLHolder implements Encodable, Serializable
{
    private static final long serialVersionUID = 20170722001L;
    private transient CertificateList x509CRL;
    private transient boolean isIndirect;
    private transient Extensions extensions;
    private transient GeneralNames issuerName;
    
    private static CertificateList parseStream(final InputStream inputStream) throws IOException {
        try {
            final ASN1Primitive object = new ASN1InputStream(inputStream, true).readObject();
            if (object == null) {
                throw new IOException("no content found");
            }
            return CertificateList.getInstance((Object)object);
        }
        catch (final ClassCastException ex) {
            throw new CertIOException("malformed data: " + ex.getMessage(), ex);
        }
        catch (final IllegalArgumentException ex2) {
            throw new CertIOException("malformed data: " + ex2.getMessage(), ex2);
        }
    }
    
    private static boolean isIndirectCRL(final Extensions extensions) {
        if (extensions == null) {
            return false;
        }
        final Extension extension = extensions.getExtension(Extension.issuingDistributionPoint);
        return extension != null && IssuingDistributionPoint.getInstance((Object)extension.getParsedValue()).isIndirectCRL();
    }
    
    public X509CRLHolder(final byte[] array) throws IOException {
        this(parseStream(new ByteArrayInputStream(array)));
    }
    
    public X509CRLHolder(final InputStream inputStream) throws IOException {
        this(parseStream(inputStream));
    }
    
    public X509CRLHolder(final CertificateList list) {
        this.init(list);
    }
    
    private void init(final CertificateList x509CRL) {
        this.x509CRL = x509CRL;
        this.extensions = x509CRL.getTBSCertList().getExtensions();
        this.isIndirect = isIndirectCRL(this.extensions);
        this.issuerName = new GeneralNames(new GeneralName(x509CRL.getIssuer()));
    }
    
    public byte[] getEncoded() throws IOException {
        return this.x509CRL.getEncoded();
    }
    
    public X500Name getIssuer() {
        return X500Name.getInstance((Object)this.x509CRL.getIssuer());
    }
    
    public X509CRLEntryHolder getRevokedCertificate(final BigInteger bigInteger) {
        GeneralNames generalNames = this.issuerName;
        final Enumeration revokedCertificateEnumeration = this.x509CRL.getRevokedCertificateEnumeration();
        while (revokedCertificateEnumeration.hasMoreElements()) {
            final TBSCertList.CRLEntry crlEntry = revokedCertificateEnumeration.nextElement();
            if (crlEntry.getUserCertificate().getValue().equals(bigInteger)) {
                return new X509CRLEntryHolder(crlEntry, this.isIndirect, generalNames);
            }
            if (!this.isIndirect || !crlEntry.hasExtensions()) {
                continue;
            }
            final Extension extension = crlEntry.getExtensions().getExtension(Extension.certificateIssuer);
            if (extension == null) {
                continue;
            }
            generalNames = GeneralNames.getInstance((Object)extension.getParsedValue());
        }
        return null;
    }
    
    public Collection getRevokedCertificates() {
        final ArrayList list = new ArrayList(this.x509CRL.getRevokedCertificates().length);
        GeneralNames generalNames = this.issuerName;
        final Enumeration revokedCertificateEnumeration = this.x509CRL.getRevokedCertificateEnumeration();
        while (revokedCertificateEnumeration.hasMoreElements()) {
            final X509CRLEntryHolder x509CRLEntryHolder = new X509CRLEntryHolder(revokedCertificateEnumeration.nextElement(), this.isIndirect, generalNames);
            list.add(x509CRLEntryHolder);
            generalNames = x509CRLEntryHolder.getCertificateIssuer();
        }
        return list;
    }
    
    public boolean hasExtensions() {
        return this.extensions != null;
    }
    
    public Extension getExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        if (this.extensions != null) {
            return this.extensions.getExtension(asn1ObjectIdentifier);
        }
        return null;
    }
    
    public Extensions getExtensions() {
        return this.extensions;
    }
    
    public List getExtensionOIDs() {
        return CertUtils.getExtensionOIDs(this.extensions);
    }
    
    public Set getCriticalExtensionOIDs() {
        return CertUtils.getCriticalExtensionOIDs(this.extensions);
    }
    
    public Set getNonCriticalExtensionOIDs() {
        return CertUtils.getNonCriticalExtensionOIDs(this.extensions);
    }
    
    public CertificateList toASN1Structure() {
        return this.x509CRL;
    }
    
    public boolean isSignatureValid(final ContentVerifierProvider contentVerifierProvider) throws CertException {
        final TBSCertList tbsCertList = this.x509CRL.getTBSCertList();
        if (!CertUtils.isAlgIdEqual(tbsCertList.getSignature(), this.x509CRL.getSignatureAlgorithm())) {
            throw new CertException("signature invalid - algorithm identifier mismatch");
        }
        ContentVerifier value;
        try {
            value = contentVerifierProvider.get(tbsCertList.getSignature());
            final OutputStream outputStream = value.getOutputStream();
            new DEROutputStream(outputStream).writeObject((ASN1Encodable)tbsCertList);
            outputStream.close();
        }
        catch (final Exception ex) {
            throw new CertException("unable to process signature: " + ex.getMessage(), ex);
        }
        return value.verify(this.x509CRL.getSignature().getOctets());
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof X509CRLHolder && this.x509CRL.equals((Object)((X509CRLHolder)o).x509CRL));
    }
    
    @Override
    public int hashCode() {
        return this.x509CRL.hashCode();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.init(CertificateList.getInstance(objectInputStream.readObject()));
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.getEncoded());
    }
}
