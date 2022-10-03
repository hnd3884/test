package org.bouncycastle.jce;

import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import java.util.HashSet;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.security.AlgorithmParameters;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.DERBitString;
import java.security.Signature;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.util.Strings;
import java.io.IOException;
import javax.security.auth.x500.X500Principal;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import org.bouncycastle.asn1.ASN1Set;
import java.security.PublicKey;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.util.Set;
import java.util.Hashtable;
import org.bouncycastle.asn1.pkcs.CertificationRequest;

public class PKCS10CertificationRequest extends CertificationRequest
{
    private static Hashtable algorithms;
    private static Hashtable params;
    private static Hashtable keyAlgorithms;
    private static Hashtable oids;
    private static Set noParams;
    
    private static RSASSAPSSparams creatPSSParams(final AlgorithmIdentifier algorithmIdentifier, final int n) {
        return new RSASSAPSSparams(algorithmIdentifier, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, algorithmIdentifier), new ASN1Integer(n), new ASN1Integer(1L));
    }
    
    private static ASN1Sequence toDERSequence(final byte[] array) {
        try {
            return (ASN1Sequence)new ASN1InputStream(array).readObject();
        }
        catch (final Exception ex) {
            throw new IllegalArgumentException("badly encoded request");
        }
    }
    
    public PKCS10CertificationRequest(final byte[] array) {
        super(toDERSequence(array));
    }
    
    public PKCS10CertificationRequest(final ASN1Sequence asn1Sequence) {
        super(asn1Sequence);
    }
    
    public PKCS10CertificationRequest(final String s, final X509Name x509Name, final PublicKey publicKey, final ASN1Set set, final PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        this(s, x509Name, publicKey, set, privateKey, "BC");
    }
    
    private static X509Name convertName(final X500Principal x500Principal) {
        try {
            return new X509Principal(x500Principal.getEncoded());
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("can't convert name");
        }
    }
    
    public PKCS10CertificationRequest(final String s, final X500Principal x500Principal, final PublicKey publicKey, final ASN1Set set, final PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        this(s, convertName(x500Principal), publicKey, set, privateKey, "BC");
    }
    
    public PKCS10CertificationRequest(final String s, final X500Principal x500Principal, final PublicKey publicKey, final ASN1Set set, final PrivateKey privateKey, final String s2) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        this(s, convertName(x500Principal), publicKey, set, privateKey, s2);
    }
    
    public PKCS10CertificationRequest(final String s, final X509Name x509Name, final PublicKey publicKey, final ASN1Set set, final PrivateKey privateKey, final String s2) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        final String upperCase = Strings.toUpperCase(s);
        ASN1ObjectIdentifier asn1ObjectIdentifier = PKCS10CertificationRequest.algorithms.get(upperCase);
        if (asn1ObjectIdentifier == null) {
            try {
                asn1ObjectIdentifier = new ASN1ObjectIdentifier(upperCase);
            }
            catch (final Exception ex) {
                throw new IllegalArgumentException("Unknown signature type requested");
            }
        }
        if (x509Name == null) {
            throw new IllegalArgumentException("subject must not be null");
        }
        if (publicKey == null) {
            throw new IllegalArgumentException("public key must not be null");
        }
        if (PKCS10CertificationRequest.noParams.contains(asn1ObjectIdentifier)) {
            this.sigAlgId = new AlgorithmIdentifier(asn1ObjectIdentifier);
        }
        else if (PKCS10CertificationRequest.params.containsKey(upperCase)) {
            this.sigAlgId = new AlgorithmIdentifier(asn1ObjectIdentifier, PKCS10CertificationRequest.params.get(upperCase));
        }
        else {
            this.sigAlgId = new AlgorithmIdentifier(asn1ObjectIdentifier, DERNull.INSTANCE);
        }
        try {
            this.reqInfo = new CertificationRequestInfo(x509Name, SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(publicKey.getEncoded())), set);
        }
        catch (final IOException ex2) {
            throw new IllegalArgumentException("can't encode public key");
        }
        Signature signature;
        if (s2 == null) {
            signature = Signature.getInstance(s);
        }
        else {
            signature = Signature.getInstance(s, s2);
        }
        signature.initSign(privateKey);
        try {
            signature.update(this.reqInfo.getEncoded("DER"));
        }
        catch (final Exception ex3) {
            throw new IllegalArgumentException("exception encoding TBS cert request - " + ex3);
        }
        this.sigBits = new DERBitString(signature.sign());
    }
    
    public PublicKey getPublicKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        return this.getPublicKey("BC");
    }
    
    public PublicKey getPublicKey(final String s) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        final SubjectPublicKeyInfo subjectPublicKeyInfo = this.reqInfo.getSubjectPublicKeyInfo();
        try {
            final X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(new DERBitString(subjectPublicKeyInfo).getOctets());
            final AlgorithmIdentifier algorithm = subjectPublicKeyInfo.getAlgorithm();
            try {
                if (s == null) {
                    return KeyFactory.getInstance(algorithm.getAlgorithm().getId()).generatePublic(x509EncodedKeySpec);
                }
                return KeyFactory.getInstance(algorithm.getAlgorithm().getId(), s).generatePublic(x509EncodedKeySpec);
            }
            catch (final NoSuchAlgorithmException ex) {
                if (PKCS10CertificationRequest.keyAlgorithms.get(algorithm.getAlgorithm()) == null) {
                    throw ex;
                }
                final String s2 = PKCS10CertificationRequest.keyAlgorithms.get(algorithm.getAlgorithm());
                if (s == null) {
                    return KeyFactory.getInstance(s2).generatePublic(x509EncodedKeySpec);
                }
                return KeyFactory.getInstance(s2, s).generatePublic(x509EncodedKeySpec);
            }
        }
        catch (final InvalidKeySpecException ex2) {
            throw new InvalidKeyException("error decoding public key");
        }
        catch (final IOException ex3) {
            throw new InvalidKeyException("error decoding public key");
        }
    }
    
    public boolean verify() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        return this.verify("BC");
    }
    
    public boolean verify(final String s) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        return this.verify(this.getPublicKey(s), s);
    }
    
    public boolean verify(final PublicKey publicKey, final String s) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        Signature signature;
        try {
            if (s == null) {
                signature = Signature.getInstance(getSignatureName(this.sigAlgId));
            }
            else {
                signature = Signature.getInstance(getSignatureName(this.sigAlgId), s);
            }
        }
        catch (final NoSuchAlgorithmException ex) {
            if (PKCS10CertificationRequest.oids.get(this.sigAlgId.getAlgorithm()) == null) {
                throw ex;
            }
            final String s2 = PKCS10CertificationRequest.oids.get(this.sigAlgId.getAlgorithm());
            if (s == null) {
                signature = Signature.getInstance(s2);
            }
            else {
                signature = Signature.getInstance(s2, s);
            }
        }
        this.setSignatureParameters(signature, this.sigAlgId.getParameters());
        signature.initVerify(publicKey);
        try {
            signature.update(this.reqInfo.getEncoded("DER"));
        }
        catch (final Exception ex2) {
            throw new SignatureException("exception encoding TBS cert request - " + ex2);
        }
        return signature.verify(this.sigBits.getOctets());
    }
    
    @Override
    public byte[] getEncoded() {
        try {
            return this.getEncoded("DER");
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex.toString());
        }
    }
    
    private void setSignatureParameters(final Signature signature, final ASN1Encodable asn1Encodable) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if (asn1Encodable != null && !DERNull.INSTANCE.equals(asn1Encodable)) {
            final AlgorithmParameters instance = AlgorithmParameters.getInstance(signature.getAlgorithm(), signature.getProvider());
            try {
                instance.init(asn1Encodable.toASN1Primitive().getEncoded("DER"));
            }
            catch (final IOException ex) {
                throw new SignatureException("IOException decoding parameters: " + ex.getMessage());
            }
            if (signature.getAlgorithm().endsWith("MGF1")) {
                try {
                    signature.setParameter(instance.getParameterSpec(PSSParameterSpec.class));
                }
                catch (final GeneralSecurityException ex2) {
                    throw new SignatureException("Exception extracting parameters: " + ex2.getMessage());
                }
            }
        }
    }
    
    static String getSignatureName(final AlgorithmIdentifier algorithmIdentifier) {
        final ASN1Encodable parameters = algorithmIdentifier.getParameters();
        if (parameters != null && !DERNull.INSTANCE.equals(parameters) && algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS)) {
            return getDigestAlgName(RSASSAPSSparams.getInstance(parameters).getHashAlgorithm().getAlgorithm()) + "withRSAandMGF1";
        }
        return algorithmIdentifier.getAlgorithm().getId();
    }
    
    private static String getDigestAlgName(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        if (PKCSObjectIdentifiers.md5.equals(asn1ObjectIdentifier)) {
            return "MD5";
        }
        if (OIWObjectIdentifiers.idSHA1.equals(asn1ObjectIdentifier)) {
            return "SHA1";
        }
        if (NISTObjectIdentifiers.id_sha224.equals(asn1ObjectIdentifier)) {
            return "SHA224";
        }
        if (NISTObjectIdentifiers.id_sha256.equals(asn1ObjectIdentifier)) {
            return "SHA256";
        }
        if (NISTObjectIdentifiers.id_sha384.equals(asn1ObjectIdentifier)) {
            return "SHA384";
        }
        if (NISTObjectIdentifiers.id_sha512.equals(asn1ObjectIdentifier)) {
            return "SHA512";
        }
        if (TeleTrusTObjectIdentifiers.ripemd128.equals(asn1ObjectIdentifier)) {
            return "RIPEMD128";
        }
        if (TeleTrusTObjectIdentifiers.ripemd160.equals(asn1ObjectIdentifier)) {
            return "RIPEMD160";
        }
        if (TeleTrusTObjectIdentifiers.ripemd256.equals(asn1ObjectIdentifier)) {
            return "RIPEMD256";
        }
        if (CryptoProObjectIdentifiers.gostR3411.equals(asn1ObjectIdentifier)) {
            return "GOST3411";
        }
        return asn1ObjectIdentifier.getId();
    }
    
    static {
        PKCS10CertificationRequest.algorithms = new Hashtable();
        PKCS10CertificationRequest.params = new Hashtable();
        PKCS10CertificationRequest.keyAlgorithms = new Hashtable();
        PKCS10CertificationRequest.oids = new Hashtable();
        PKCS10CertificationRequest.noParams = new HashSet();
        PKCS10CertificationRequest.algorithms.put("MD2WITHRSAENCRYPTION", new ASN1ObjectIdentifier("1.2.840.113549.1.1.2"));
        PKCS10CertificationRequest.algorithms.put("MD2WITHRSA", new ASN1ObjectIdentifier("1.2.840.113549.1.1.2"));
        PKCS10CertificationRequest.algorithms.put("MD5WITHRSAENCRYPTION", new ASN1ObjectIdentifier("1.2.840.113549.1.1.4"));
        PKCS10CertificationRequest.algorithms.put("MD5WITHRSA", new ASN1ObjectIdentifier("1.2.840.113549.1.1.4"));
        PKCS10CertificationRequest.algorithms.put("RSAWITHMD5", new ASN1ObjectIdentifier("1.2.840.113549.1.1.4"));
        PKCS10CertificationRequest.algorithms.put("SHA1WITHRSAENCRYPTION", new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"));
        PKCS10CertificationRequest.algorithms.put("SHA1WITHRSA", new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"));
        PKCS10CertificationRequest.algorithms.put("SHA224WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha224WithRSAEncryption);
        PKCS10CertificationRequest.algorithms.put("SHA224WITHRSA", PKCSObjectIdentifiers.sha224WithRSAEncryption);
        PKCS10CertificationRequest.algorithms.put("SHA256WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha256WithRSAEncryption);
        PKCS10CertificationRequest.algorithms.put("SHA256WITHRSA", PKCSObjectIdentifiers.sha256WithRSAEncryption);
        PKCS10CertificationRequest.algorithms.put("SHA384WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha384WithRSAEncryption);
        PKCS10CertificationRequest.algorithms.put("SHA384WITHRSA", PKCSObjectIdentifiers.sha384WithRSAEncryption);
        PKCS10CertificationRequest.algorithms.put("SHA512WITHRSAENCRYPTION", PKCSObjectIdentifiers.sha512WithRSAEncryption);
        PKCS10CertificationRequest.algorithms.put("SHA512WITHRSA", PKCSObjectIdentifiers.sha512WithRSAEncryption);
        PKCS10CertificationRequest.algorithms.put("SHA1WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        PKCS10CertificationRequest.algorithms.put("SHA224WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        PKCS10CertificationRequest.algorithms.put("SHA256WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        PKCS10CertificationRequest.algorithms.put("SHA384WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        PKCS10CertificationRequest.algorithms.put("SHA512WITHRSAANDMGF1", PKCSObjectIdentifiers.id_RSASSA_PSS);
        PKCS10CertificationRequest.algorithms.put("RSAWITHSHA1", new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"));
        PKCS10CertificationRequest.algorithms.put("RIPEMD128WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
        PKCS10CertificationRequest.algorithms.put("RIPEMD128WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128);
        PKCS10CertificationRequest.algorithms.put("RIPEMD160WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
        PKCS10CertificationRequest.algorithms.put("RIPEMD160WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160);
        PKCS10CertificationRequest.algorithms.put("RIPEMD256WITHRSAENCRYPTION", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
        PKCS10CertificationRequest.algorithms.put("RIPEMD256WITHRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256);
        PKCS10CertificationRequest.algorithms.put("SHA1WITHDSA", new ASN1ObjectIdentifier("1.2.840.10040.4.3"));
        PKCS10CertificationRequest.algorithms.put("DSAWITHSHA1", new ASN1ObjectIdentifier("1.2.840.10040.4.3"));
        PKCS10CertificationRequest.algorithms.put("SHA224WITHDSA", NISTObjectIdentifiers.dsa_with_sha224);
        PKCS10CertificationRequest.algorithms.put("SHA256WITHDSA", NISTObjectIdentifiers.dsa_with_sha256);
        PKCS10CertificationRequest.algorithms.put("SHA384WITHDSA", NISTObjectIdentifiers.dsa_with_sha384);
        PKCS10CertificationRequest.algorithms.put("SHA512WITHDSA", NISTObjectIdentifiers.dsa_with_sha512);
        PKCS10CertificationRequest.algorithms.put("SHA1WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA1);
        PKCS10CertificationRequest.algorithms.put("SHA224WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA224);
        PKCS10CertificationRequest.algorithms.put("SHA256WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA256);
        PKCS10CertificationRequest.algorithms.put("SHA384WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA384);
        PKCS10CertificationRequest.algorithms.put("SHA512WITHECDSA", X9ObjectIdentifiers.ecdsa_with_SHA512);
        PKCS10CertificationRequest.algorithms.put("ECDSAWITHSHA1", X9ObjectIdentifiers.ecdsa_with_SHA1);
        PKCS10CertificationRequest.algorithms.put("GOST3411WITHGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
        PKCS10CertificationRequest.algorithms.put("GOST3410WITHGOST3411", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
        PKCS10CertificationRequest.algorithms.put("GOST3411WITHECGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        PKCS10CertificationRequest.algorithms.put("GOST3411WITHECGOST3410-2001", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        PKCS10CertificationRequest.algorithms.put("GOST3411WITHGOST3410-2001", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        PKCS10CertificationRequest.oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.5"), "SHA1WITHRSA");
        PKCS10CertificationRequest.oids.put(PKCSObjectIdentifiers.sha224WithRSAEncryption, "SHA224WITHRSA");
        PKCS10CertificationRequest.oids.put(PKCSObjectIdentifiers.sha256WithRSAEncryption, "SHA256WITHRSA");
        PKCS10CertificationRequest.oids.put(PKCSObjectIdentifiers.sha384WithRSAEncryption, "SHA384WITHRSA");
        PKCS10CertificationRequest.oids.put(PKCSObjectIdentifiers.sha512WithRSAEncryption, "SHA512WITHRSA");
        PKCS10CertificationRequest.oids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3411WITHGOST3410");
        PKCS10CertificationRequest.oids.put(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, "GOST3411WITHECGOST3410");
        PKCS10CertificationRequest.oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.4"), "MD5WITHRSA");
        PKCS10CertificationRequest.oids.put(new ASN1ObjectIdentifier("1.2.840.113549.1.1.2"), "MD2WITHRSA");
        PKCS10CertificationRequest.oids.put(new ASN1ObjectIdentifier("1.2.840.10040.4.3"), "SHA1WITHDSA");
        PKCS10CertificationRequest.oids.put(X9ObjectIdentifiers.ecdsa_with_SHA1, "SHA1WITHECDSA");
        PKCS10CertificationRequest.oids.put(X9ObjectIdentifiers.ecdsa_with_SHA224, "SHA224WITHECDSA");
        PKCS10CertificationRequest.oids.put(X9ObjectIdentifiers.ecdsa_with_SHA256, "SHA256WITHECDSA");
        PKCS10CertificationRequest.oids.put(X9ObjectIdentifiers.ecdsa_with_SHA384, "SHA384WITHECDSA");
        PKCS10CertificationRequest.oids.put(X9ObjectIdentifiers.ecdsa_with_SHA512, "SHA512WITHECDSA");
        PKCS10CertificationRequest.oids.put(OIWObjectIdentifiers.sha1WithRSA, "SHA1WITHRSA");
        PKCS10CertificationRequest.oids.put(OIWObjectIdentifiers.dsaWithSHA1, "SHA1WITHDSA");
        PKCS10CertificationRequest.oids.put(NISTObjectIdentifiers.dsa_with_sha224, "SHA224WITHDSA");
        PKCS10CertificationRequest.oids.put(NISTObjectIdentifiers.dsa_with_sha256, "SHA256WITHDSA");
        PKCS10CertificationRequest.keyAlgorithms.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
        PKCS10CertificationRequest.keyAlgorithms.put(X9ObjectIdentifiers.id_dsa, "DSA");
        PKCS10CertificationRequest.noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA1);
        PKCS10CertificationRequest.noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA224);
        PKCS10CertificationRequest.noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA256);
        PKCS10CertificationRequest.noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA384);
        PKCS10CertificationRequest.noParams.add(X9ObjectIdentifiers.ecdsa_with_SHA512);
        PKCS10CertificationRequest.noParams.add(X9ObjectIdentifiers.id_dsa_with_sha1);
        PKCS10CertificationRequest.noParams.add(NISTObjectIdentifiers.dsa_with_sha224);
        PKCS10CertificationRequest.noParams.add(NISTObjectIdentifiers.dsa_with_sha256);
        PKCS10CertificationRequest.noParams.add(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94);
        PKCS10CertificationRequest.noParams.add(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001);
        PKCS10CertificationRequest.params.put("SHA1WITHRSAANDMGF1", creatPSSParams(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE), 20));
        PKCS10CertificationRequest.params.put("SHA224WITHRSAANDMGF1", creatPSSParams(new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, DERNull.INSTANCE), 28));
        PKCS10CertificationRequest.params.put("SHA256WITHRSAANDMGF1", creatPSSParams(new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE), 32));
        PKCS10CertificationRequest.params.put("SHA384WITHRSAANDMGF1", creatPSSParams(new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, DERNull.INSTANCE), 48));
        PKCS10CertificationRequest.params.put("SHA512WITHRSAANDMGF1", creatPSSParams(new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, DERNull.INSTANCE), 64));
    }
}
