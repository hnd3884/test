package net.oauth.signature;

import java.security.Signature;
import java.io.UnsupportedEncodingException;
import net.oauth.signature.pem.PKCS1EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import net.oauth.signature.pem.PEMReader;
import java.security.spec.EncodedKeySpec;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import net.oauth.OAuthException;
import net.oauth.OAuthAccessor;
import java.security.PublicKey;
import java.security.PrivateKey;

public class RSA_SHA1 extends OAuthSignatureMethod
{
    public static final String PRIVATE_KEY = "RSA-SHA1.PrivateKey";
    public static final String PUBLIC_KEY = "RSA-SHA1.PublicKey";
    public static final String X509_CERTIFICATE = "RSA-SHA1.X509Certificate";
    private PrivateKey privateKey;
    private PublicKey publicKey;
    
    public RSA_SHA1() {
        this.privateKey = null;
        this.publicKey = null;
    }
    
    @Override
    protected void initialize(final String name, final OAuthAccessor accessor) throws OAuthException {
        super.initialize(name, accessor);
        try {
            final Object privateKeyObject = accessor.consumer.getProperty("RSA-SHA1.PrivateKey");
            if (privateKeyObject != null) {
                this.privateKey = this.loadPrivateKey(privateKeyObject);
            }
            final Object publicKeyObject = accessor.consumer.getProperty("RSA-SHA1.PublicKey");
            if (publicKeyObject != null) {
                this.publicKey = this.loadPublicKey(publicKeyObject, false);
            }
            else {
                final Object certObject = accessor.consumer.getProperty("RSA-SHA1.X509Certificate");
                if (certObject != null) {
                    this.publicKey = this.loadPublicKey(certObject, true);
                }
            }
        }
        catch (final GeneralSecurityException e) {
            throw new OAuthException(e);
        }
        catch (final IOException e2) {
            throw new OAuthException(e2);
        }
    }
    
    private PublicKey getPublicKeyFromDerCert(final byte[] certObject) throws GeneralSecurityException {
        final CertificateFactory fac = CertificateFactory.getInstance("X509");
        final ByteArrayInputStream in = new ByteArrayInputStream(certObject);
        final X509Certificate cert = (X509Certificate)fac.generateCertificate(in);
        return cert.getPublicKey();
    }
    
    private PublicKey getPublicKeyFromDer(final byte[] publicKeyObject) throws GeneralSecurityException {
        final KeyFactory fac = KeyFactory.getInstance("RSA");
        final EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKeyObject);
        return fac.generatePublic(pubKeySpec);
    }
    
    private PublicKey getPublicKeyFromPem(final String pem) throws GeneralSecurityException, IOException {
        final InputStream stream = new ByteArrayInputStream(pem.getBytes("UTF-8"));
        final PEMReader reader = new PEMReader(stream);
        final byte[] bytes = reader.getDerBytes();
        PublicKey pubKey;
        if ("-----BEGIN PUBLIC KEY-----".equals(reader.getBeginMarker())) {
            final KeySpec keySpec = new X509EncodedKeySpec(bytes);
            final KeyFactory fac = KeyFactory.getInstance("RSA");
            pubKey = fac.generatePublic(keySpec);
        }
        else {
            if (!"-----BEGIN CERTIFICATE-----".equals(reader.getBeginMarker())) {
                throw new IOException("Invalid PEM fileL: Unknown marker for  public key or cert " + reader.getBeginMarker());
            }
            pubKey = this.getPublicKeyFromDerCert(bytes);
        }
        return pubKey;
    }
    
    private PrivateKey getPrivateKeyFromDer(final byte[] privateKeyObject) throws GeneralSecurityException {
        final KeyFactory fac = KeyFactory.getInstance("RSA");
        final EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(privateKeyObject);
        return fac.generatePrivate(privKeySpec);
    }
    
    private PrivateKey getPrivateKeyFromPem(final String pem) throws GeneralSecurityException, IOException {
        final InputStream stream = new ByteArrayInputStream(pem.getBytes("UTF-8"));
        final PEMReader reader = new PEMReader(stream);
        final byte[] bytes = reader.getDerBytes();
        KeySpec keySpec;
        if ("-----BEGIN RSA PRIVATE KEY-----".equals(reader.getBeginMarker())) {
            keySpec = new PKCS1EncodedKeySpec(bytes).getKeySpec();
        }
        else {
            if (!"-----BEGIN PRIVATE KEY-----".equals(reader.getBeginMarker())) {
                throw new IOException("Invalid PEM file: Unknown marker for private key " + reader.getBeginMarker());
            }
            keySpec = new PKCS8EncodedKeySpec(bytes);
        }
        final KeyFactory fac = KeyFactory.getInstance("RSA");
        return fac.generatePrivate(keySpec);
    }
    
    @Override
    protected String getSignature(final String baseString) throws OAuthException {
        try {
            final byte[] signature = this.sign(baseString.getBytes("UTF-8"));
            return OAuthSignatureMethod.base64Encode(signature);
        }
        catch (final UnsupportedEncodingException e) {
            throw new OAuthException(e);
        }
        catch (final GeneralSecurityException e2) {
            throw new OAuthException(e2);
        }
    }
    
    @Override
    protected boolean isValid(final String signature, final String baseString) throws OAuthException {
        try {
            return this.verify(OAuthSignatureMethod.decodeBase64(signature), baseString.getBytes("UTF-8"));
        }
        catch (final UnsupportedEncodingException e) {
            throw new OAuthException(e);
        }
        catch (final GeneralSecurityException e2) {
            throw new OAuthException(e2);
        }
    }
    
    private byte[] sign(final byte[] message) throws GeneralSecurityException {
        if (this.privateKey == null) {
            throw new IllegalStateException("need to set private key with OAuthConsumer.setProperty when generating RSA-SHA1 signatures.");
        }
        final Signature signer = Signature.getInstance("SHA1withRSA");
        signer.initSign(this.privateKey);
        signer.update(message);
        return signer.sign();
    }
    
    private boolean verify(final byte[] signature, final byte[] message) throws GeneralSecurityException {
        if (this.publicKey == null) {
            throw new IllegalStateException("need to set public key with  OAuthConsumer.setProperty when verifying RSA-SHA1 signatures.");
        }
        final Signature verifier = Signature.getInstance("SHA1withRSA");
        verifier.initVerify(this.publicKey);
        verifier.update(message);
        return verifier.verify(signature);
    }
    
    private PrivateKey loadPrivateKey(final Object privateKeyObject) throws IOException, GeneralSecurityException {
        PrivateKey privateKey;
        if (privateKeyObject instanceof PrivateKey) {
            privateKey = (PrivateKey)privateKeyObject;
        }
        else if (privateKeyObject instanceof String) {
            try {
                privateKey = this.getPrivateKeyFromPem((String)privateKeyObject);
            }
            catch (final IOException e) {
                privateKey = this.getPrivateKeyFromDer(OAuthSignatureMethod.decodeBase64((String)privateKeyObject));
            }
        }
        else {
            if (!(privateKeyObject instanceof byte[])) {
                throw new IllegalArgumentException("Private key set through RSA_SHA1.PRIVATE_KEY must be of type PrivateKey, String or byte[] and not " + privateKeyObject.getClass().getName());
            }
            privateKey = this.getPrivateKeyFromDer((byte[])privateKeyObject);
        }
        return privateKey;
    }
    
    private PublicKey loadPublicKey(final Object publicKeyObject, final boolean isCert) throws IOException, GeneralSecurityException {
        PublicKey publicKey;
        if (publicKeyObject instanceof PublicKey) {
            publicKey = (PublicKey)publicKeyObject;
        }
        else if (publicKeyObject instanceof X509Certificate) {
            publicKey = ((X509Certificate)publicKeyObject).getPublicKey();
        }
        else if (publicKeyObject instanceof String) {
            try {
                publicKey = this.getPublicKeyFromPem((String)publicKeyObject);
            }
            catch (final IOException e) {
                if (isCert) {
                    throw e;
                }
                publicKey = this.getPublicKeyFromDer(OAuthSignatureMethod.decodeBase64((String)publicKeyObject));
            }
        }
        else {
            if (!(publicKeyObject instanceof byte[])) {
                String source;
                if (isCert) {
                    source = "RSA_SHA1.X509_CERTIFICATE";
                }
                else {
                    source = "RSA_SHA1.PUBLIC_KEY";
                }
                throw new IllegalArgumentException("Public key or certificate set through " + source + " must be of " + "type PublicKey, String or byte[], and not " + publicKeyObject.getClass().getName());
            }
            if (isCert) {
                publicKey = this.getPublicKeyFromDerCert((byte[])publicKeyObject);
            }
            else {
                publicKey = this.getPublicKeyFromDer((byte[])publicKeyObject);
            }
        }
        return publicKey;
    }
}
