package org.bouncycastle.openssl;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import java.util.Iterator;
import org.bouncycastle.util.encoders.Hex;
import java.util.StringTokenizer;
import org.bouncycastle.util.io.pem.PemHeader;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.util.io.pem.PemObject;
import java.io.IOException;
import org.bouncycastle.util.io.pem.PemObjectParser;
import java.util.HashMap;
import java.io.Reader;
import java.util.Map;
import org.bouncycastle.util.io.pem.PemReader;

public class PEMParser extends PemReader
{
    private final Map parsers;
    
    public PEMParser(final Reader reader) {
        super(reader);
        (this.parsers = new HashMap()).put("CERTIFICATE REQUEST", new PKCS10CertificationRequestParser());
        this.parsers.put("NEW CERTIFICATE REQUEST", new PKCS10CertificationRequestParser());
        this.parsers.put("CERTIFICATE", new X509CertificateParser());
        this.parsers.put("TRUSTED CERTIFICATE", new X509TrustedCertificateParser());
        this.parsers.put("X509 CERTIFICATE", new X509CertificateParser());
        this.parsers.put("X509 CRL", new X509CRLParser());
        this.parsers.put("PKCS7", new PKCS7Parser());
        this.parsers.put("CMS", new PKCS7Parser());
        this.parsers.put("ATTRIBUTE CERTIFICATE", new X509AttributeCertificateParser());
        this.parsers.put("EC PARAMETERS", new ECCurveParamsParser());
        this.parsers.put("PUBLIC KEY", new PublicKeyParser());
        this.parsers.put("RSA PUBLIC KEY", new RSAPublicKeyParser());
        this.parsers.put("RSA PRIVATE KEY", new KeyPairParser(new RSAKeyPairParser()));
        this.parsers.put("DSA PRIVATE KEY", new KeyPairParser(new DSAKeyPairParser()));
        this.parsers.put("EC PRIVATE KEY", new KeyPairParser(new ECDSAKeyPairParser()));
        this.parsers.put("ENCRYPTED PRIVATE KEY", new EncryptedPrivateKeyParser());
        this.parsers.put("PRIVATE KEY", new PrivateKeyParser());
    }
    
    public Object readObject() throws IOException {
        final PemObject pemObject = this.readPemObject();
        if (pemObject == null) {
            return null;
        }
        final String type = pemObject.getType();
        if (this.parsers.containsKey(type)) {
            return ((PemObjectParser)this.parsers.get(type)).parseObject(pemObject);
        }
        throw new IOException("unrecognised object: " + type);
    }
    
    private class DSAKeyPairParser implements PEMKeyPairParser
    {
        public PEMKeyPair parse(final byte[] array) throws IOException {
            try {
                final ASN1Sequence instance = ASN1Sequence.getInstance((Object)array);
                if (instance.size() != 6) {
                    throw new PEMException("malformed sequence in DSA private key");
                }
                final ASN1Integer instance2 = ASN1Integer.getInstance((Object)instance.getObjectAt(1));
                final ASN1Integer instance3 = ASN1Integer.getInstance((Object)instance.getObjectAt(2));
                final ASN1Integer instance4 = ASN1Integer.getInstance((Object)instance.getObjectAt(3));
                return new PEMKeyPair(new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, (ASN1Encodable)new DSAParameter(instance2.getValue(), instance3.getValue(), instance4.getValue())), (ASN1Encodable)ASN1Integer.getInstance((Object)instance.getObjectAt(4))), new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, (ASN1Encodable)new DSAParameter(instance2.getValue(), instance3.getValue(), instance4.getValue())), (ASN1Encodable)ASN1Integer.getInstance((Object)instance.getObjectAt(5))));
            }
            catch (final IOException ex) {
                throw ex;
            }
            catch (final Exception ex2) {
                throw new PEMException("problem creating DSA private key: " + ex2.toString(), ex2);
            }
        }
    }
    
    private class ECCurveParamsParser implements PemObjectParser
    {
        public Object parseObject(final PemObject pemObject) throws IOException {
            try {
                final ASN1Primitive fromByteArray = ASN1Primitive.fromByteArray(pemObject.getContent());
                if (fromByteArray instanceof ASN1ObjectIdentifier) {
                    return ASN1Primitive.fromByteArray(pemObject.getContent());
                }
                if (fromByteArray instanceof ASN1Sequence) {
                    return X9ECParameters.getInstance((Object)fromByteArray);
                }
                return null;
            }
            catch (final IOException ex) {
                throw ex;
            }
            catch (final Exception ex2) {
                throw new PEMException("exception extracting EC named curve: " + ex2.toString());
            }
        }
    }
    
    private class ECDSAKeyPairParser implements PEMKeyPairParser
    {
        public PEMKeyPair parse(final byte[] array) throws IOException {
            try {
                final ECPrivateKey instance = ECPrivateKey.getInstance((Object)ASN1Sequence.getInstance((Object)array));
                final AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, (ASN1Encodable)instance.getParameters());
                return new PEMKeyPair(new SubjectPublicKeyInfo(algorithmIdentifier, instance.getPublicKey().getBytes()), new PrivateKeyInfo(algorithmIdentifier, (ASN1Encodable)instance));
            }
            catch (final IOException ex) {
                throw ex;
            }
            catch (final Exception ex2) {
                throw new PEMException("problem creating EC private key: " + ex2.toString(), ex2);
            }
        }
    }
    
    private class EncryptedPrivateKeyParser implements PemObjectParser
    {
        public EncryptedPrivateKeyParser() {
        }
        
        public Object parseObject(final PemObject pemObject) throws IOException {
            try {
                return new PKCS8EncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo.getInstance((Object)pemObject.getContent()));
            }
            catch (final Exception ex) {
                throw new PEMException("problem parsing ENCRYPTED PRIVATE KEY: " + ex.toString(), ex);
            }
        }
    }
    
    private class KeyPairParser implements PemObjectParser
    {
        private final PEMKeyPairParser pemKeyPairParser;
        
        public KeyPairParser(final PEMKeyPairParser pemKeyPairParser) {
            this.pemKeyPairParser = pemKeyPairParser;
        }
        
        public Object parseObject(final PemObject pemObject) throws IOException {
            boolean b = false;
            String value = null;
            for (final PemHeader pemHeader : pemObject.getHeaders()) {
                if (pemHeader.getName().equals("Proc-Type") && pemHeader.getValue().equals("4,ENCRYPTED")) {
                    b = true;
                }
                else {
                    if (!pemHeader.getName().equals("DEK-Info")) {
                        continue;
                    }
                    value = pemHeader.getValue();
                }
            }
            final byte[] content = pemObject.getContent();
            try {
                if (b) {
                    final StringTokenizer stringTokenizer = new StringTokenizer(value, ",");
                    return new PEMEncryptedKeyPair(stringTokenizer.nextToken(), Hex.decode(stringTokenizer.nextToken()), content, this.pemKeyPairParser);
                }
                return this.pemKeyPairParser.parse(content);
            }
            catch (final IOException ex) {
                if (b) {
                    throw new PEMException("exception decoding - please check password and data.", ex);
                }
                throw new PEMException(ex.getMessage(), ex);
            }
            catch (final IllegalArgumentException ex2) {
                if (b) {
                    throw new PEMException("exception decoding - please check password and data.", ex2);
                }
                throw new PEMException(ex2.getMessage(), ex2);
            }
        }
    }
    
    private class PKCS10CertificationRequestParser implements PemObjectParser
    {
        public Object parseObject(final PemObject pemObject) throws IOException {
            try {
                return new PKCS10CertificationRequest(pemObject.getContent());
            }
            catch (final Exception ex) {
                throw new PEMException("problem parsing certrequest: " + ex.toString(), ex);
            }
        }
    }
    
    private class PKCS7Parser implements PemObjectParser
    {
        public Object parseObject(final PemObject pemObject) throws IOException {
            try {
                return ContentInfo.getInstance((Object)new ASN1InputStream(pemObject.getContent()).readObject());
            }
            catch (final Exception ex) {
                throw new PEMException("problem parsing PKCS7 object: " + ex.toString(), ex);
            }
        }
    }
    
    private class PrivateKeyParser implements PemObjectParser
    {
        public PrivateKeyParser() {
        }
        
        public Object parseObject(final PemObject pemObject) throws IOException {
            try {
                return PrivateKeyInfo.getInstance((Object)pemObject.getContent());
            }
            catch (final Exception ex) {
                throw new PEMException("problem parsing PRIVATE KEY: " + ex.toString(), ex);
            }
        }
    }
    
    private class PublicKeyParser implements PemObjectParser
    {
        public PublicKeyParser() {
        }
        
        public Object parseObject(final PemObject pemObject) throws IOException {
            return SubjectPublicKeyInfo.getInstance((Object)pemObject.getContent());
        }
    }
    
    private class RSAKeyPairParser implements PEMKeyPairParser
    {
        public PEMKeyPair parse(final byte[] array) throws IOException {
            try {
                final ASN1Sequence instance = ASN1Sequence.getInstance((Object)array);
                if (instance.size() != 9) {
                    throw new PEMException("malformed sequence in RSA private key");
                }
                final RSAPrivateKey instance2 = RSAPrivateKey.getInstance((Object)instance);
                final RSAPublicKey rsaPublicKey = new RSAPublicKey(instance2.getModulus(), instance2.getPublicExponent());
                final AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, (ASN1Encodable)DERNull.INSTANCE);
                return new PEMKeyPair(new SubjectPublicKeyInfo(algorithmIdentifier, (ASN1Encodable)rsaPublicKey), new PrivateKeyInfo(algorithmIdentifier, (ASN1Encodable)instance2));
            }
            catch (final IOException ex) {
                throw ex;
            }
            catch (final Exception ex2) {
                throw new PEMException("problem creating RSA private key: " + ex2.toString(), ex2);
            }
        }
    }
    
    private class RSAPublicKeyParser implements PemObjectParser
    {
        public RSAPublicKeyParser() {
        }
        
        public Object parseObject(final PemObject pemObject) throws IOException {
            try {
                return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, (ASN1Encodable)DERNull.INSTANCE), (ASN1Encodable)RSAPublicKey.getInstance((Object)pemObject.getContent()));
            }
            catch (final IOException ex) {
                throw ex;
            }
            catch (final Exception ex2) {
                throw new PEMException("problem extracting key: " + ex2.toString(), ex2);
            }
        }
    }
    
    private class X509AttributeCertificateParser implements PemObjectParser
    {
        public Object parseObject(final PemObject pemObject) throws IOException {
            return new X509AttributeCertificateHolder(pemObject.getContent());
        }
    }
    
    private class X509CRLParser implements PemObjectParser
    {
        public Object parseObject(final PemObject pemObject) throws IOException {
            try {
                return new X509CRLHolder(pemObject.getContent());
            }
            catch (final Exception ex) {
                throw new PEMException("problem parsing cert: " + ex.toString(), ex);
            }
        }
    }
    
    private class X509CertificateParser implements PemObjectParser
    {
        public Object parseObject(final PemObject pemObject) throws IOException {
            try {
                return new X509CertificateHolder(pemObject.getContent());
            }
            catch (final Exception ex) {
                throw new PEMException("problem parsing cert: " + ex.toString(), ex);
            }
        }
    }
    
    private class X509TrustedCertificateParser implements PemObjectParser
    {
        public Object parseObject(final PemObject pemObject) throws IOException {
            try {
                return new X509TrustedCertificateBlock(pemObject.getContent());
            }
            catch (final Exception ex) {
                throw new PEMException("problem parsing cert: " + ex.toString(), ex);
            }
        }
    }
}
