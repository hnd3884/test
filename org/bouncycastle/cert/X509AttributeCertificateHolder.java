package org.bouncycastle.cert;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.asn1.x509.AttributeCertificateInfo;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.asn1.x509.AttCertValidityPeriod;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.util.Set;
import java.util.List;
import org.bouncycastle.asn1.x509.Extension;
import java.util.ArrayList;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Date;
import org.bouncycastle.asn1.ASN1Sequence;
import java.math.BigInteger;
import java.io.IOException;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.asn1.x509.Attribute;
import java.io.Serializable;
import org.bouncycastle.util.Encodable;

public class X509AttributeCertificateHolder implements Encodable, Serializable
{
    private static final long serialVersionUID = 20170722001L;
    private static Attribute[] EMPTY_ARRAY;
    private transient AttributeCertificate attrCert;
    private transient Extensions extensions;
    
    private static AttributeCertificate parseBytes(final byte[] array) throws IOException {
        try {
            return AttributeCertificate.getInstance((Object)CertUtils.parseNonEmptyASN1(array));
        }
        catch (final ClassCastException ex) {
            throw new CertIOException("malformed data: " + ex.getMessage(), ex);
        }
        catch (final IllegalArgumentException ex2) {
            throw new CertIOException("malformed data: " + ex2.getMessage(), ex2);
        }
    }
    
    public X509AttributeCertificateHolder(final byte[] array) throws IOException {
        this(parseBytes(array));
    }
    
    public X509AttributeCertificateHolder(final AttributeCertificate attributeCertificate) {
        this.init(attributeCertificate);
    }
    
    private void init(final AttributeCertificate attrCert) {
        this.attrCert = attrCert;
        this.extensions = attrCert.getAcinfo().getExtensions();
    }
    
    public byte[] getEncoded() throws IOException {
        return this.attrCert.getEncoded();
    }
    
    public int getVersion() {
        return this.attrCert.getAcinfo().getVersion().getValue().intValue() + 1;
    }
    
    public BigInteger getSerialNumber() {
        return this.attrCert.getAcinfo().getSerialNumber().getValue();
    }
    
    public AttributeCertificateHolder getHolder() {
        return new AttributeCertificateHolder((ASN1Sequence)this.attrCert.getAcinfo().getHolder().toASN1Primitive());
    }
    
    public AttributeCertificateIssuer getIssuer() {
        return new AttributeCertificateIssuer(this.attrCert.getAcinfo().getIssuer());
    }
    
    public Date getNotBefore() {
        return CertUtils.recoverDate(this.attrCert.getAcinfo().getAttrCertValidityPeriod().getNotBeforeTime());
    }
    
    public Date getNotAfter() {
        return CertUtils.recoverDate(this.attrCert.getAcinfo().getAttrCertValidityPeriod().getNotAfterTime());
    }
    
    public Attribute[] getAttributes() {
        final ASN1Sequence attributes = this.attrCert.getAcinfo().getAttributes();
        final Attribute[] array = new Attribute[attributes.size()];
        for (int i = 0; i != attributes.size(); ++i) {
            array[i] = Attribute.getInstance((Object)attributes.getObjectAt(i));
        }
        return array;
    }
    
    public Attribute[] getAttributes(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        final ASN1Sequence attributes = this.attrCert.getAcinfo().getAttributes();
        final ArrayList list = new ArrayList();
        for (int i = 0; i != attributes.size(); ++i) {
            final Attribute instance = Attribute.getInstance((Object)attributes.getObjectAt(i));
            if (instance.getAttrType().equals((Object)asn1ObjectIdentifier)) {
                list.add(instance);
            }
        }
        if (list.size() == 0) {
            return X509AttributeCertificateHolder.EMPTY_ARRAY;
        }
        return (Attribute[])list.toArray(new Attribute[list.size()]);
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
    
    public boolean[] getIssuerUniqueID() {
        return CertUtils.bitStringToBoolean(this.attrCert.getAcinfo().getIssuerUniqueID());
    }
    
    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.attrCert.getSignatureAlgorithm();
    }
    
    public byte[] getSignature() {
        return this.attrCert.getSignatureValue().getOctets();
    }
    
    public AttributeCertificate toASN1Structure() {
        return this.attrCert;
    }
    
    public boolean isValidOn(final Date date) {
        final AttCertValidityPeriod attrCertValidityPeriod = this.attrCert.getAcinfo().getAttrCertValidityPeriod();
        return !date.before(CertUtils.recoverDate(attrCertValidityPeriod.getNotBeforeTime())) && !date.after(CertUtils.recoverDate(attrCertValidityPeriod.getNotAfterTime()));
    }
    
    public boolean isSignatureValid(final ContentVerifierProvider contentVerifierProvider) throws CertException {
        final AttributeCertificateInfo acinfo = this.attrCert.getAcinfo();
        if (!CertUtils.isAlgIdEqual(acinfo.getSignature(), this.attrCert.getSignatureAlgorithm())) {
            throw new CertException("signature invalid - algorithm identifier mismatch");
        }
        ContentVerifier value;
        try {
            value = contentVerifierProvider.get(acinfo.getSignature());
            final OutputStream outputStream = value.getOutputStream();
            new DEROutputStream(outputStream).writeObject((ASN1Encodable)acinfo);
            outputStream.close();
        }
        catch (final Exception ex) {
            throw new CertException("unable to process signature: " + ex.getMessage(), ex);
        }
        return value.verify(this.getSignature());
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof X509AttributeCertificateHolder && this.attrCert.equals((Object)((X509AttributeCertificateHolder)o).attrCert));
    }
    
    @Override
    public int hashCode() {
        return this.attrCert.hashCode();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.init(AttributeCertificate.getInstance(objectInputStream.readObject()));
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.getEncoded());
    }
    
    static {
        X509AttributeCertificateHolder.EMPTY_ARRAY = new Attribute[0];
    }
}
