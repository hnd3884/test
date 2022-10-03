package org.bouncycastle.x509;

import org.bouncycastle.util.Arrays;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.PublicKey;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateExpiredException;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1Sequence;
import java.math.BigInteger;
import java.text.ParseException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1InputStream;
import java.io.InputStream;
import java.util.Date;
import org.bouncycastle.asn1.x509.AttributeCertificate;

public class X509V2AttributeCertificate implements X509AttributeCertificate
{
    private AttributeCertificate cert;
    private Date notBefore;
    private Date notAfter;
    
    private static AttributeCertificate getObject(final InputStream inputStream) throws IOException {
        try {
            return AttributeCertificate.getInstance(new ASN1InputStream(inputStream).readObject());
        }
        catch (final IOException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new IOException("exception decoding certificate structure: " + ex2.toString());
        }
    }
    
    public X509V2AttributeCertificate(final InputStream inputStream) throws IOException {
        this(getObject(inputStream));
    }
    
    public X509V2AttributeCertificate(final byte[] array) throws IOException {
        this(new ByteArrayInputStream(array));
    }
    
    X509V2AttributeCertificate(final AttributeCertificate cert) throws IOException {
        this.cert = cert;
        try {
            this.notAfter = cert.getAcinfo().getAttrCertValidityPeriod().getNotAfterTime().getDate();
            this.notBefore = cert.getAcinfo().getAttrCertValidityPeriod().getNotBeforeTime().getDate();
        }
        catch (final ParseException ex) {
            throw new IOException("invalid data structure in certificate!");
        }
    }
    
    public int getVersion() {
        return this.cert.getAcinfo().getVersion().getValue().intValue() + 1;
    }
    
    public BigInteger getSerialNumber() {
        return this.cert.getAcinfo().getSerialNumber().getValue();
    }
    
    public AttributeCertificateHolder getHolder() {
        return new AttributeCertificateHolder((ASN1Sequence)this.cert.getAcinfo().getHolder().toASN1Primitive());
    }
    
    public AttributeCertificateIssuer getIssuer() {
        return new AttributeCertificateIssuer(this.cert.getAcinfo().getIssuer());
    }
    
    public Date getNotBefore() {
        return this.notBefore;
    }
    
    public Date getNotAfter() {
        return this.notAfter;
    }
    
    public boolean[] getIssuerUniqueID() {
        final DERBitString issuerUniqueID = this.cert.getAcinfo().getIssuerUniqueID();
        if (issuerUniqueID != null) {
            final byte[] bytes = issuerUniqueID.getBytes();
            final boolean[] array = new boolean[bytes.length * 8 - issuerUniqueID.getPadBits()];
            for (int i = 0; i != array.length; ++i) {
                array[i] = ((bytes[i / 8] & 128 >>> i % 8) != 0x0);
            }
            return array;
        }
        return null;
    }
    
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
        this.checkValidity(new Date());
    }
    
    public void checkValidity(final Date date) throws CertificateExpiredException, CertificateNotYetValidException {
        if (date.after(this.getNotAfter())) {
            throw new CertificateExpiredException("certificate expired on " + this.getNotAfter());
        }
        if (date.before(this.getNotBefore())) {
            throw new CertificateNotYetValidException("certificate not valid till " + this.getNotBefore());
        }
    }
    
    public byte[] getSignature() {
        return this.cert.getSignatureValue().getOctets();
    }
    
    public final void verify(final PublicKey publicKey, final String s) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        if (!this.cert.getSignatureAlgorithm().equals(this.cert.getAcinfo().getSignature())) {
            throw new CertificateException("Signature algorithm in certificate info not same as outer certificate");
        }
        final Signature instance = Signature.getInstance(this.cert.getSignatureAlgorithm().getAlgorithm().getId(), s);
        instance.initVerify(publicKey);
        try {
            instance.update(this.cert.getAcinfo().getEncoded());
        }
        catch (final IOException ex) {
            throw new SignatureException("Exception encoding certificate info object");
        }
        if (!instance.verify(this.getSignature())) {
            throw new InvalidKeyException("Public key presented not for certificate signature");
        }
    }
    
    public byte[] getEncoded() throws IOException {
        return this.cert.getEncoded();
    }
    
    public byte[] getExtensionValue(final String s) {
        final Extensions extensions = this.cert.getAcinfo().getExtensions();
        if (extensions != null) {
            final Extension extension = extensions.getExtension(new ASN1ObjectIdentifier(s));
            if (extension != null) {
                try {
                    return extension.getExtnValue().getEncoded("DER");
                }
                catch (final Exception ex) {
                    throw new RuntimeException("error encoding " + ex.toString());
                }
            }
        }
        return null;
    }
    
    private Set getExtensionOIDs(final boolean b) {
        final Extensions extensions = this.cert.getAcinfo().getExtensions();
        if (extensions != null) {
            final HashSet set = new HashSet();
            final Enumeration oids = extensions.oids();
            while (oids.hasMoreElements()) {
                final ASN1ObjectIdentifier asn1ObjectIdentifier = oids.nextElement();
                if (extensions.getExtension(asn1ObjectIdentifier).isCritical() == b) {
                    set.add(asn1ObjectIdentifier.getId());
                }
            }
            return set;
        }
        return null;
    }
    
    public Set getNonCriticalExtensionOIDs() {
        return this.getExtensionOIDs(false);
    }
    
    public Set getCriticalExtensionOIDs() {
        return this.getExtensionOIDs(true);
    }
    
    public boolean hasUnsupportedCriticalExtension() {
        final Set criticalExtensionOIDs = this.getCriticalExtensionOIDs();
        return criticalExtensionOIDs != null && !criticalExtensionOIDs.isEmpty();
    }
    
    public X509Attribute[] getAttributes() {
        final ASN1Sequence attributes = this.cert.getAcinfo().getAttributes();
        final X509Attribute[] array = new X509Attribute[attributes.size()];
        for (int i = 0; i != attributes.size(); ++i) {
            array[i] = new X509Attribute(attributes.getObjectAt(i));
        }
        return array;
    }
    
    public X509Attribute[] getAttributes(final String s) {
        final ASN1Sequence attributes = this.cert.getAcinfo().getAttributes();
        final ArrayList list = new ArrayList();
        for (int i = 0; i != attributes.size(); ++i) {
            final X509Attribute x509Attribute = new X509Attribute(attributes.getObjectAt(i));
            if (x509Attribute.getOID().equals(s)) {
                list.add(x509Attribute);
            }
        }
        if (list.size() == 0) {
            return null;
        }
        return (X509Attribute[])list.toArray(new X509Attribute[list.size()]);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof X509AttributeCertificate)) {
            return false;
        }
        final X509AttributeCertificate x509AttributeCertificate = (X509AttributeCertificate)o;
        try {
            return Arrays.areEqual(this.getEncoded(), x509AttributeCertificate.getEncoded());
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        try {
            return Arrays.hashCode(this.getEncoded());
        }
        catch (final IOException ex) {
            return 0;
        }
    }
}
