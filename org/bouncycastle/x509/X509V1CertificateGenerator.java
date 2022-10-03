package org.bouncycastle.x509;

import java.util.Iterator;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.ASN1Encodable;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.PrivateKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.PublicKey;
import org.bouncycastle.asn1.x509.Time;
import java.util.Date;
import java.io.IOException;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.X509Principal;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Integer;
import java.math.BigInteger;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.V1TBSCertificateGenerator;
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import org.bouncycastle.jcajce.util.JcaJceHelper;

public class X509V1CertificateGenerator
{
    private final JcaJceHelper bcHelper;
    private final CertificateFactory certificateFactory;
    private V1TBSCertificateGenerator tbsGen;
    private ASN1ObjectIdentifier sigOID;
    private AlgorithmIdentifier sigAlgId;
    private String signatureAlgorithm;
    
    public X509V1CertificateGenerator() {
        this.bcHelper = new BCJcaJceHelper();
        this.certificateFactory = new CertificateFactory();
        this.tbsGen = new V1TBSCertificateGenerator();
    }
    
    public void reset() {
        this.tbsGen = new V1TBSCertificateGenerator();
    }
    
    public void setSerialNumber(final BigInteger bigInteger) {
        if (bigInteger.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalArgumentException("serial number must be a positive integer");
        }
        this.tbsGen.setSerialNumber(new ASN1Integer(bigInteger));
    }
    
    public void setIssuerDN(final X500Principal x500Principal) {
        try {
            this.tbsGen.setIssuer(new X509Principal(x500Principal.getEncoded()));
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("can't process principal: " + ex);
        }
    }
    
    public void setIssuerDN(final X509Name issuer) {
        this.tbsGen.setIssuer(issuer);
    }
    
    public void setNotBefore(final Date date) {
        this.tbsGen.setStartDate(new Time(date));
    }
    
    public void setNotAfter(final Date date) {
        this.tbsGen.setEndDate(new Time(date));
    }
    
    public void setSubjectDN(final X500Principal x500Principal) {
        try {
            this.tbsGen.setSubject(new X509Principal(x500Principal.getEncoded()));
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("can't process principal: " + ex);
        }
    }
    
    public void setSubjectDN(final X509Name subject) {
        this.tbsGen.setSubject(subject);
    }
    
    public void setPublicKey(final PublicKey publicKey) {
        try {
            this.tbsGen.setSubjectPublicKeyInfo(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
        }
        catch (final Exception ex) {
            throw new IllegalArgumentException("unable to process key - " + ex.toString());
        }
    }
    
    public void setSignatureAlgorithm(final String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
        try {
            this.sigOID = X509Util.getAlgorithmOID(signatureAlgorithm);
        }
        catch (final Exception ex) {
            throw new IllegalArgumentException("Unknown signature type requested");
        }
        this.sigAlgId = X509Util.getSigAlgID(this.sigOID, signatureAlgorithm);
        this.tbsGen.setSignature(this.sigAlgId);
    }
    
    @Deprecated
    public X509Certificate generateX509Certificate(final PrivateKey privateKey) throws SecurityException, SignatureException, InvalidKeyException {
        try {
            return this.generateX509Certificate(privateKey, "BC", null);
        }
        catch (final NoSuchProviderException ex) {
            throw new SecurityException("BC provider not installed!");
        }
    }
    
    @Deprecated
    public X509Certificate generateX509Certificate(final PrivateKey privateKey, final SecureRandom secureRandom) throws SecurityException, SignatureException, InvalidKeyException {
        try {
            return this.generateX509Certificate(privateKey, "BC", secureRandom);
        }
        catch (final NoSuchProviderException ex) {
            throw new SecurityException("BC provider not installed!");
        }
    }
    
    @Deprecated
    public X509Certificate generateX509Certificate(final PrivateKey privateKey, final String s) throws NoSuchProviderException, SecurityException, SignatureException, InvalidKeyException {
        return this.generateX509Certificate(privateKey, s, null);
    }
    
    @Deprecated
    public X509Certificate generateX509Certificate(final PrivateKey privateKey, final String s, final SecureRandom secureRandom) throws NoSuchProviderException, SecurityException, SignatureException, InvalidKeyException {
        try {
            return this.generate(privateKey, s, secureRandom);
        }
        catch (final NoSuchProviderException ex) {
            throw ex;
        }
        catch (final SignatureException ex2) {
            throw ex2;
        }
        catch (final InvalidKeyException ex3) {
            throw ex3;
        }
        catch (final GeneralSecurityException ex4) {
            throw new SecurityException("exception: " + ex4);
        }
    }
    
    public X509Certificate generate(final PrivateKey privateKey) throws CertificateEncodingException, IllegalStateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        return this.generate(privateKey, (SecureRandom)null);
    }
    
    public X509Certificate generate(final PrivateKey privateKey, final SecureRandom secureRandom) throws CertificateEncodingException, IllegalStateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        final TBSCertificate generateTBSCertificate = this.tbsGen.generateTBSCertificate();
        byte[] calculateSignature;
        try {
            calculateSignature = X509Util.calculateSignature(this.sigOID, this.signatureAlgorithm, privateKey, secureRandom, generateTBSCertificate);
        }
        catch (final IOException ex) {
            throw new ExtCertificateEncodingException("exception encoding TBS cert", ex);
        }
        return this.generateJcaObject(generateTBSCertificate, calculateSignature);
    }
    
    public X509Certificate generate(final PrivateKey privateKey, final String s) throws CertificateEncodingException, IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        return this.generate(privateKey, s, null);
    }
    
    public X509Certificate generate(final PrivateKey privateKey, final String s, final SecureRandom secureRandom) throws CertificateEncodingException, IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        final TBSCertificate generateTBSCertificate = this.tbsGen.generateTBSCertificate();
        byte[] calculateSignature;
        try {
            calculateSignature = X509Util.calculateSignature(this.sigOID, this.signatureAlgorithm, s, privateKey, secureRandom, generateTBSCertificate);
        }
        catch (final IOException ex) {
            throw new ExtCertificateEncodingException("exception encoding TBS cert", ex);
        }
        return this.generateJcaObject(generateTBSCertificate, calculateSignature);
    }
    
    private X509Certificate generateJcaObject(final TBSCertificate tbsCertificate, final byte[] array) throws CertificateEncodingException {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(tbsCertificate);
        asn1EncodableVector.add(this.sigAlgId);
        asn1EncodableVector.add(new DERBitString(array));
        try {
            return (X509Certificate)this.certificateFactory.engineGenerateCertificate(new ByteArrayInputStream(new DERSequence(asn1EncodableVector).getEncoded("DER")));
        }
        catch (final Exception ex) {
            throw new ExtCertificateEncodingException("exception producing certificate object", ex);
        }
    }
    
    public Iterator getSignatureAlgNames() {
        return X509Util.getAlgNames();
    }
}
