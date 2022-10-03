package org.bouncycastle.cert;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.util.Date;
import org.bouncycastle.asn1.x500.X500Name;
import java.math.BigInteger;
import java.util.Set;
import java.util.List;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.io.IOException;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.Certificate;
import java.io.Serializable;
import org.bouncycastle.util.Encodable;

public class X509CertificateHolder implements Encodable, Serializable
{
    private static final long serialVersionUID = 20170722001L;
    private transient Certificate x509Certificate;
    private transient Extensions extensions;
    
    private static Certificate parseBytes(final byte[] array) throws IOException {
        try {
            return Certificate.getInstance((Object)CertUtils.parseNonEmptyASN1(array));
        }
        catch (final ClassCastException ex) {
            throw new CertIOException("malformed data: " + ex.getMessage(), ex);
        }
        catch (final IllegalArgumentException ex2) {
            throw new CertIOException("malformed data: " + ex2.getMessage(), ex2);
        }
    }
    
    public X509CertificateHolder(final byte[] array) throws IOException {
        this(parseBytes(array));
    }
    
    public X509CertificateHolder(final Certificate certificate) {
        this.init(certificate);
    }
    
    private void init(final Certificate x509Certificate) {
        this.x509Certificate = x509Certificate;
        this.extensions = x509Certificate.getTBSCertificate().getExtensions();
    }
    
    public int getVersionNumber() {
        return this.x509Certificate.getVersionNumber();
    }
    
    @Deprecated
    public int getVersion() {
        return this.x509Certificate.getVersionNumber();
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
    
    public BigInteger getSerialNumber() {
        return this.x509Certificate.getSerialNumber().getValue();
    }
    
    public X500Name getIssuer() {
        return X500Name.getInstance((Object)this.x509Certificate.getIssuer());
    }
    
    public X500Name getSubject() {
        return X500Name.getInstance((Object)this.x509Certificate.getSubject());
    }
    
    public Date getNotBefore() {
        return this.x509Certificate.getStartDate().getDate();
    }
    
    public Date getNotAfter() {
        return this.x509Certificate.getEndDate().getDate();
    }
    
    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.x509Certificate.getSubjectPublicKeyInfo();
    }
    
    public Certificate toASN1Structure() {
        return this.x509Certificate;
    }
    
    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.x509Certificate.getSignatureAlgorithm();
    }
    
    public byte[] getSignature() {
        return this.x509Certificate.getSignature().getOctets();
    }
    
    public boolean isValidOn(final Date date) {
        return !date.before(this.x509Certificate.getStartDate().getDate()) && !date.after(this.x509Certificate.getEndDate().getDate());
    }
    
    public boolean isSignatureValid(final ContentVerifierProvider contentVerifierProvider) throws CertException {
        final TBSCertificate tbsCertificate = this.x509Certificate.getTBSCertificate();
        if (!CertUtils.isAlgIdEqual(tbsCertificate.getSignature(), this.x509Certificate.getSignatureAlgorithm())) {
            throw new CertException("signature invalid - algorithm identifier mismatch");
        }
        ContentVerifier value;
        try {
            value = contentVerifierProvider.get(tbsCertificate.getSignature());
            final OutputStream outputStream = value.getOutputStream();
            new DEROutputStream(outputStream).writeObject((ASN1Encodable)tbsCertificate);
            outputStream.close();
        }
        catch (final Exception ex) {
            throw new CertException("unable to process signature: " + ex.getMessage(), ex);
        }
        return value.verify(this.getSignature());
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof X509CertificateHolder && this.x509Certificate.equals((Object)((X509CertificateHolder)o).x509Certificate));
    }
    
    @Override
    public int hashCode() {
        return this.x509Certificate.hashCode();
    }
    
    public byte[] getEncoded() throws IOException {
        return this.x509Certificate.getEncoded();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.init(Certificate.getInstance(objectInputStream.readObject()));
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.getEncoded());
    }
}
