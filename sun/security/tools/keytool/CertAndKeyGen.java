package sun.security.tools.keytool;

import java.security.Signature;
import sun.security.pkcs10.PKCS10;
import java.security.spec.PSSParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import sun.security.x509.X509CertImpl;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.CertificateAlgorithmId;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.x509.CertificateSerialNumber;
import java.util.Random;
import sun.security.x509.CertificateVersion;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X509CertInfo;
import sun.security.x509.CertificateValidity;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import sun.security.x509.CertificateExtensions;
import java.security.cert.X509Certificate;
import java.util.Date;
import sun.security.x509.X500Name;
import sun.security.x509.X509Key;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

public final class CertAndKeyGen
{
    private SecureRandom prng;
    private String sigAlg;
    private KeyPairGenerator keyGen;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    
    public CertAndKeyGen(final String s, final String sigAlg) throws NoSuchAlgorithmException {
        this.keyGen = KeyPairGenerator.getInstance(s);
        this.sigAlg = sigAlg;
    }
    
    public CertAndKeyGen(final String s, final String sigAlg, final String s2) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (s2 == null) {
            this.keyGen = KeyPairGenerator.getInstance(s);
        }
        else {
            try {
                this.keyGen = KeyPairGenerator.getInstance(s, s2);
            }
            catch (final Exception ex) {
                this.keyGen = KeyPairGenerator.getInstance(s);
            }
        }
        this.sigAlg = sigAlg;
    }
    
    public void setRandom(final SecureRandom prng) {
        this.prng = prng;
    }
    
    public void generate(final int n) throws InvalidKeyException {
        KeyPair generateKeyPair;
        try {
            if (this.prng == null) {
                this.prng = new SecureRandom();
            }
            this.keyGen.initialize(n, this.prng);
            generateKeyPair = this.keyGen.generateKeyPair();
        }
        catch (final Exception ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
        this.publicKey = generateKeyPair.getPublic();
        this.privateKey = generateKeyPair.getPrivate();
        if (!"X.509".equalsIgnoreCase(this.publicKey.getFormat())) {
            throw new IllegalArgumentException("publicKey's is not X.509, but " + this.publicKey.getFormat());
        }
    }
    
    public X509Key getPublicKey() {
        if (!(this.publicKey instanceof X509Key)) {
            return null;
        }
        return (X509Key)this.publicKey;
    }
    
    public PublicKey getPublicKeyAnyway() {
        return this.publicKey;
    }
    
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }
    
    public X509Certificate getSelfCertificate(final X500Name x500Name, final Date date, final long n) throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException {
        return this.getSelfCertificate(x500Name, date, n, null);
    }
    
    public X509Certificate getSelfCertificate(final X500Name x500Name, final Date date, final long n, final CertificateExtensions certificateExtensions) throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException {
        try {
            final Date date2 = new Date();
            date2.setTime(date.getTime() + n * 1000L);
            final CertificateValidity certificateValidity = new CertificateValidity(date, date2);
            final X509CertInfo x509CertInfo = new X509CertInfo();
            final PSSParameterSpec defaultAlgorithmParameterSpec = AlgorithmId.getDefaultAlgorithmParameterSpec(this.sigAlg, this.privateKey);
            x509CertInfo.set("version", new CertificateVersion(2));
            x509CertInfo.set("serialNumber", new CertificateSerialNumber(new Random().nextInt() & Integer.MAX_VALUE));
            x509CertInfo.set("algorithmID", new CertificateAlgorithmId(AlgorithmId.getWithParameterSpec(this.sigAlg, defaultAlgorithmParameterSpec)));
            x509CertInfo.set("subject", x500Name);
            x509CertInfo.set("key", new CertificateX509Key(this.publicKey));
            x509CertInfo.set("validity", certificateValidity);
            x509CertInfo.set("issuer", x500Name);
            if (certificateExtensions != null) {
                x509CertInfo.set("extensions", certificateExtensions);
            }
            final X509CertImpl x509CertImpl = new X509CertImpl(x509CertInfo);
            x509CertImpl.sign(this.privateKey, defaultAlgorithmParameterSpec, this.sigAlg, null);
            return x509CertImpl;
        }
        catch (final IOException ex) {
            throw new CertificateEncodingException("getSelfCert: " + ex.getMessage());
        }
        catch (final InvalidAlgorithmParameterException ex2) {
            throw new SignatureException("Unsupported PSSParameterSpec: " + ex2.getMessage());
        }
    }
    
    public X509Certificate getSelfCertificate(final X500Name x500Name, final long n) throws CertificateException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException {
        return this.getSelfCertificate(x500Name, new Date(), n);
    }
    
    public PKCS10 getCertRequest(final X500Name x500Name) throws InvalidKeyException, SignatureException {
        final PKCS10 pkcs10 = new PKCS10(this.publicKey);
        try {
            final Signature instance = Signature.getInstance(this.sigAlg);
            instance.initSign(this.privateKey);
            pkcs10.encodeAndSign(x500Name, instance);
        }
        catch (final CertificateException ex) {
            throw new SignatureException(this.sigAlg + " CertificateException");
        }
        catch (final IOException ex2) {
            throw new SignatureException(this.sigAlg + " IOException");
        }
        catch (final NoSuchAlgorithmException ex3) {
            throw new SignatureException(this.sigAlg + " unavailable?");
        }
        return pkcs10;
    }
}
