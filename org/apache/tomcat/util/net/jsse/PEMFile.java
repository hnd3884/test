package org.apache.tomcat.util.net.jsse;

import java.security.spec.RSAPrivateCrtKeySpec;
import java.math.BigInteger;
import org.apache.tomcat.util.buf.Asn1Writer;
import org.apache.tomcat.util.buf.Asn1Parser;
import javax.crypto.SecretKey;
import java.security.spec.InvalidKeySpecException;
import java.security.KeyFactory;
import java.security.InvalidKeyException;
import java.security.Key;
import javax.crypto.Cipher;
import java.security.spec.KeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.EncryptedPrivateKeyInfo;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.cert.CertificateException;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.util.Iterator;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.tomcat.util.file.ConfigFileLoader;
import java.util.ArrayList;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import org.apache.tomcat.util.codec.binary.Base64;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;
import org.apache.tomcat.util.res.StringManager;

public class PEMFile
{
    private static final StringManager sm;
    private static final byte[] OID_EC_PUBLIC_KEY;
    private String filename;
    private List<X509Certificate> certificates;
    private PrivateKey privateKey;
    
    public static String toPEM(final X509Certificate certificate) throws CertificateEncodingException {
        final StringBuilder result = new StringBuilder();
        result.append("-----BEGIN CERTIFICATE-----");
        result.append(System.lineSeparator());
        final Base64 b64 = new Base64(64);
        result.append(b64.encodeAsString(certificate.getEncoded()));
        result.append("-----END CERTIFICATE-----");
        return result.toString();
    }
    
    public List<X509Certificate> getCertificates() {
        return this.certificates;
    }
    
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }
    
    public PEMFile(final String filename) throws IOException, GeneralSecurityException {
        this(filename, null);
    }
    
    public PEMFile(final String filename, final String password) throws IOException, GeneralSecurityException {
        this(filename, password, null);
    }
    
    public PEMFile(final String filename, final String password, final String keyAlgorithm) throws IOException, GeneralSecurityException {
        this.certificates = new ArrayList<X509Certificate>();
        this.filename = filename;
        final List<Part> parts = new ArrayList<Part>();
        try (final InputStream inputStream = ConfigFileLoader.getInputStream(filename)) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII));
            Part part = null;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("-----BEGIN ")) {
                    part = new Part();
                    part.type = line.substring("-----BEGIN ".length(), line.length() - "-----".length()).trim();
                }
                else if (line.startsWith("-----END ")) {
                    parts.add(part);
                    part = null;
                }
                else {
                    if (part == null || line.contains(":") || line.startsWith(" ")) {
                        continue;
                    }
                    final StringBuilder sb = new StringBuilder();
                    final Part part3 = part;
                    part3.content = sb.append(part3.content).append(line).toString();
                }
            }
        }
        for (final Part part2 : parts) {
            final String type = part2.type;
            switch (type) {
                case "PRIVATE KEY": {
                    this.privateKey = part2.toPrivateKey(null, keyAlgorithm, Format.PKCS8);
                    continue;
                }
                case "EC PRIVATE KEY": {
                    this.privateKey = part2.toPrivateKey(null, "EC", Format.RFC5915);
                    continue;
                }
                case "ENCRYPTED PRIVATE KEY": {
                    this.privateKey = part2.toPrivateKey(password, keyAlgorithm, Format.PKCS8);
                    continue;
                }
                case "RSA PRIVATE KEY": {
                    this.privateKey = part2.toPrivateKey(null, keyAlgorithm, Format.PKCS1);
                    continue;
                }
                case "CERTIFICATE":
                case "X509 CERTIFICATE": {
                    this.certificates.add(part2.toCertificate());
                    continue;
                }
            }
        }
    }
    
    static {
        sm = StringManager.getManager((Class)PEMFile.class);
        OID_EC_PUBLIC_KEY = new byte[] { 6, 7, 42, -122, 72, -50, 61, 2, 1 };
    }
    
    private class Part
    {
        public static final String BEGIN_BOUNDARY = "-----BEGIN ";
        public static final String END_BOUNDARY = "-----END ";
        public static final String FINISH_BOUNDARY = "-----";
        public static final String PRIVATE_KEY = "PRIVATE KEY";
        public static final String EC_PRIVATE_KEY = "EC PRIVATE KEY";
        public static final String ENCRYPTED_PRIVATE_KEY = "ENCRYPTED PRIVATE KEY";
        public static final String RSA_PRIVATE_KEY = "RSA PRIVATE KEY";
        public static final String CERTIFICATE = "CERTIFICATE";
        public static final String X509_CERTIFICATE = "X509 CERTIFICATE";
        public String type;
        public String content;
        
        private Part() {
            this.content = "";
        }
        
        private byte[] decode() {
            return Base64.decodeBase64(this.content);
        }
        
        public X509Certificate toCertificate() throws CertificateException {
            final CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate)factory.generateCertificate(new ByteArrayInputStream(this.decode()));
        }
        
        public PrivateKey toPrivateKey(final String password, final String keyAlgorithm, final Format format) throws GeneralSecurityException, IOException {
            KeySpec keySpec = null;
            if (password == null) {
                switch (format) {
                    case PKCS1: {
                        keySpec = this.parsePKCS1(this.decode());
                        break;
                    }
                    case PKCS8: {
                        keySpec = new PKCS8EncodedKeySpec(this.decode());
                        break;
                    }
                    case RFC5915: {
                        keySpec = new PKCS8EncodedKeySpec(this.rfc5915ToPkcs8(this.decode()));
                        break;
                    }
                }
            }
            else {
                final EncryptedPrivateKeyInfo privateKeyInfo = new EncryptedPrivateKeyInfo(this.decode());
                final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(privateKeyInfo.getAlgName());
                final SecretKey secretKey = secretKeyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
                final Cipher cipher = Cipher.getInstance(privateKeyInfo.getAlgName());
                cipher.init(2, secretKey, privateKeyInfo.getAlgParameters());
                keySpec = privateKeyInfo.getKeySpec(cipher);
            }
            final InvalidKeyException exception = new InvalidKeyException(PEMFile.sm.getString("pemFile.parseError", new Object[] { PEMFile.this.filename }));
            if (keyAlgorithm == null) {
                final String[] arr$ = { "RSA", "DSA", "EC" };
                final int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    final String algorithm = arr$[i$];
                    try {
                        return KeyFactory.getInstance(algorithm).generatePrivate(keySpec);
                    }
                    catch (final InvalidKeySpecException e) {
                        exception.addSuppressed(e);
                        ++i$;
                        continue;
                    }
                    break;
                }
            }
            else {
                try {
                    return KeyFactory.getInstance(keyAlgorithm).generatePrivate(keySpec);
                }
                catch (final InvalidKeySpecException e2) {
                    exception.addSuppressed(e2);
                }
            }
            throw exception;
        }
        
        private byte[] rfc5915ToPkcs8(final byte[] source) {
            final Asn1Parser p = new Asn1Parser(source);
            p.parseTag(48);
            p.parseFullLength();
            final BigInteger version = p.parseInt();
            if (version.intValue() != 1) {
                throw new IllegalArgumentException(PEMFile.sm.getString("pemFile.notValidRFC5915"));
            }
            p.parseTag(4);
            final int privateKeyLen = p.parseLength();
            final byte[] privateKey = new byte[privateKeyLen];
            p.parseBytes(privateKey);
            p.parseTag(160);
            final int oidLen = p.parseLength();
            final byte[] oid = new byte[oidLen];
            p.parseBytes(oid);
            if (oid[0] != 6) {
                throw new IllegalArgumentException(PEMFile.sm.getString("pemFile.notValidRFC5915"));
            }
            p.parseTag(161);
            final int publicKeyLen = p.parseLength();
            final byte[] publicKey = new byte[publicKeyLen];
            p.parseBytes(publicKey);
            if (publicKey[0] != 3) {
                throw new IllegalArgumentException(PEMFile.sm.getString("pemFile.notValidRFC5915"));
            }
            return Asn1Writer.writeSequence(new byte[][] { Asn1Writer.writeInteger(0), Asn1Writer.writeSequence(new byte[][] { PEMFile.OID_EC_PUBLIC_KEY, oid }), Asn1Writer.writeOctetString(Asn1Writer.writeSequence(new byte[][] { Asn1Writer.writeInteger(1), Asn1Writer.writeOctetString(privateKey), Asn1Writer.writeTag((byte)(-95), publicKey) })) });
        }
        
        private RSAPrivateCrtKeySpec parsePKCS1(final byte[] source) {
            final Asn1Parser p = new Asn1Parser(source);
            p.parseTag(48);
            p.parseFullLength();
            final BigInteger version = p.parseInt();
            if (version.intValue() == 1) {
                throw new IllegalArgumentException(PEMFile.sm.getString("pemFile.noMultiPrimes"));
            }
            return new RSAPrivateCrtKeySpec(p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt(), p.parseInt());
        }
    }
    
    private enum Format
    {
        PKCS1, 
        PKCS8, 
        RFC5915;
    }
}
