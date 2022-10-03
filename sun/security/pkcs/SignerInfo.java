package sun.security.pkcs;

import java.util.Collections;
import java.util.EnumSet;
import sun.misc.HexDumpEncoder;
import java.util.Arrays;
import java.security.cert.CertificateException;
import java.security.cert.CertPath;
import sun.security.timestamp.TimestampToken;
import java.security.cert.Certificate;
import java.util.List;
import java.security.cert.CertificateFactory;
import java.security.NoSuchAlgorithmException;
import java.security.AlgorithmParameters;
import java.security.PublicKey;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.security.ProviderException;
import sun.security.util.SignatureUtil;
import java.security.Signature;
import sun.security.x509.KeyUsageExtension;
import sun.security.util.KeyUtil;
import java.security.Key;
import java.security.MessageDigest;
import java.security.cert.CertPathValidatorException;
import java.security.SignatureException;
import sun.security.util.ObjectIdentifier;
import sun.security.util.ConstraintsParameters;
import java.security.Principal;
import java.util.ArrayList;
import java.security.cert.X509Certificate;
import java.io.OutputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.util.DerInputStream;
import sun.security.util.Debug;
import java.security.Timestamp;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X500Name;
import java.math.BigInteger;
import sun.security.util.DisabledAlgorithmConstraints;
import java.security.CryptoPrimitive;
import java.util.Set;
import sun.security.util.DerEncoder;

public class SignerInfo implements DerEncoder
{
    private static final Set<CryptoPrimitive> DIGEST_PRIMITIVE_SET;
    private static final Set<CryptoPrimitive> SIG_PRIMITIVE_SET;
    private static final DisabledAlgorithmConstraints JAR_DISABLED_CHECK;
    BigInteger version;
    X500Name issuerName;
    BigInteger certificateSerialNumber;
    AlgorithmId digestAlgorithmId;
    AlgorithmId digestEncryptionAlgorithmId;
    byte[] encryptedDigest;
    Timestamp timestamp;
    private boolean hasTimestamp;
    private static final Debug debug;
    PKCS9Attributes authenticatedAttributes;
    PKCS9Attributes unauthenticatedAttributes;
    
    public SignerInfo(final X500Name issuerName, final BigInteger certificateSerialNumber, final AlgorithmId digestAlgorithmId, final AlgorithmId digestEncryptionAlgorithmId, final byte[] encryptedDigest) {
        this.hasTimestamp = true;
        this.version = BigInteger.ONE;
        this.issuerName = issuerName;
        this.certificateSerialNumber = certificateSerialNumber;
        this.digestAlgorithmId = digestAlgorithmId;
        this.digestEncryptionAlgorithmId = digestEncryptionAlgorithmId;
        this.encryptedDigest = encryptedDigest;
    }
    
    public SignerInfo(final X500Name issuerName, final BigInteger certificateSerialNumber, final AlgorithmId digestAlgorithmId, final PKCS9Attributes authenticatedAttributes, final AlgorithmId digestEncryptionAlgorithmId, final byte[] encryptedDigest, final PKCS9Attributes unauthenticatedAttributes) {
        this.hasTimestamp = true;
        this.version = BigInteger.ONE;
        this.issuerName = issuerName;
        this.certificateSerialNumber = certificateSerialNumber;
        this.digestAlgorithmId = digestAlgorithmId;
        this.authenticatedAttributes = authenticatedAttributes;
        this.digestEncryptionAlgorithmId = digestEncryptionAlgorithmId;
        this.encryptedDigest = encryptedDigest;
        this.unauthenticatedAttributes = unauthenticatedAttributes;
    }
    
    public SignerInfo(final DerInputStream derInputStream) throws IOException, ParsingException {
        this(derInputStream, false);
    }
    
    public SignerInfo(final DerInputStream derInputStream, final boolean b) throws IOException, ParsingException {
        this.hasTimestamp = true;
        this.version = derInputStream.getBigInteger();
        final DerValue[] sequence = derInputStream.getSequence(2);
        if (sequence.length != 2) {
            throw new ParsingException("Invalid length for IssuerAndSerialNumber");
        }
        this.issuerName = new X500Name(new DerValue((byte)48, sequence[0].toByteArray()));
        this.certificateSerialNumber = sequence[1].getBigInteger();
        this.digestAlgorithmId = AlgorithmId.parse(derInputStream.getDerValue());
        if (b) {
            derInputStream.getSet(0);
        }
        else if ((byte)derInputStream.peekByte() == -96) {
            this.authenticatedAttributes = new PKCS9Attributes(derInputStream);
        }
        this.digestEncryptionAlgorithmId = AlgorithmId.parse(derInputStream.getDerValue());
        this.encryptedDigest = derInputStream.getOctetString();
        if (b) {
            derInputStream.getSet(0);
        }
        else if (derInputStream.available() != 0 && (byte)derInputStream.peekByte() == -95) {
            this.unauthenticatedAttributes = new PKCS9Attributes(derInputStream, true);
        }
        if (derInputStream.available() != 0) {
            throw new ParsingException("extra data at the end");
        }
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        this.derEncode(derOutputStream);
    }
    
    @Override
    public void derEncode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putInteger(this.version);
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        this.issuerName.encode(derOutputStream2);
        derOutputStream2.putInteger(this.certificateSerialNumber);
        derOutputStream.write((byte)48, derOutputStream2);
        this.digestAlgorithmId.encode(derOutputStream);
        if (this.authenticatedAttributes != null) {
            this.authenticatedAttributes.encode((byte)(-96), derOutputStream);
        }
        this.digestEncryptionAlgorithmId.encode(derOutputStream);
        derOutputStream.putOctetString(this.encryptedDigest);
        if (this.unauthenticatedAttributes != null) {
            this.unauthenticatedAttributes.encode((byte)(-95), derOutputStream);
        }
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.write((byte)48, derOutputStream);
        outputStream.write(derOutputStream3.toByteArray());
    }
    
    public X509Certificate getCertificate(final PKCS7 pkcs7) throws IOException {
        return pkcs7.getCertificate(this.certificateSerialNumber, this.issuerName);
    }
    
    public ArrayList<X509Certificate> getCertificateChain(final PKCS7 pkcs7) throws IOException {
        final X509Certificate certificate = pkcs7.getCertificate(this.certificateSerialNumber, this.issuerName);
        if (certificate == null) {
            return null;
        }
        final ArrayList list = new ArrayList();
        list.add(certificate);
        final X509Certificate[] certificates = pkcs7.getCertificates();
        if (certificates == null || certificate.getSubjectDN().equals(certificate.getIssuerDN())) {
            return list;
        }
        Principal principal = certificate.getIssuerDN();
        int length = 0;
        boolean b;
        do {
            b = false;
            for (int i = length; i < certificates.length; ++i) {
                if (principal.equals(certificates[i].getSubjectDN())) {
                    list.add(certificates[i]);
                    if (certificates[i].getSubjectDN().equals(certificates[i].getIssuerDN())) {
                        length = certificates.length;
                    }
                    else {
                        principal = certificates[i].getIssuerDN();
                        final X509Certificate x509Certificate = certificates[length];
                        certificates[length] = certificates[i];
                        certificates[i] = x509Certificate;
                        ++length;
                    }
                    b = true;
                    break;
                }
            }
        } while (b);
        return list;
    }
    
    SignerInfo verify(final PKCS7 pkcs7, byte[] contentBytes) throws NoSuchAlgorithmException, SignatureException {
        try {
            final ContentInfo contentInfo = pkcs7.getContentInfo();
            if (contentBytes == null) {
                contentBytes = contentInfo.getContentBytes();
            }
            Timestamp timestamp = null;
            try {
                timestamp = this.getTimestamp();
            }
            catch (final Exception ex) {}
            final ConstraintsParameters constraintsParameters = new ConstraintsParameters(timestamp);
            final String name = this.getDigestAlgorithmId().getName();
            byte[] derEncoding;
            if (this.authenticatedAttributes == null) {
                derEncoding = contentBytes;
            }
            else {
                final ObjectIdentifier objectIdentifier = (ObjectIdentifier)this.authenticatedAttributes.getAttributeValue(PKCS9Attribute.CONTENT_TYPE_OID);
                if (objectIdentifier == null || !objectIdentifier.equals((Object)contentInfo.contentType)) {
                    return null;
                }
                final byte[] array = (byte[])this.authenticatedAttributes.getAttributeValue(PKCS9Attribute.MESSAGE_DIGEST_OID);
                if (array == null) {
                    return null;
                }
                try {
                    SignerInfo.JAR_DISABLED_CHECK.permits(name, constraintsParameters);
                }
                catch (final CertPathValidatorException ex2) {
                    throw new SignatureException(ex2.getMessage(), ex2);
                }
                final byte[] digest = MessageDigest.getInstance(name).digest(contentBytes);
                if (array.length != digest.length) {
                    return null;
                }
                for (int i = 0; i < array.length; ++i) {
                    if (array[i] != digest[i]) {
                        return null;
                    }
                }
                derEncoding = this.authenticatedAttributes.getDerEncoding();
            }
            String name2 = this.getDigestEncryptionAlgorithmId().getName();
            final String encAlgFromSigAlg = AlgorithmId.getEncAlgFromSigAlg(name2);
            if (encAlgFromSigAlg != null) {
                name2 = encAlgFromSigAlg;
            }
            final String sigAlg = AlgorithmId.makeSigAlg(name, name2);
            try {
                SignerInfo.JAR_DISABLED_CHECK.permits(sigAlg, constraintsParameters);
            }
            catch (final CertPathValidatorException ex3) {
                throw new SignatureException(ex3.getMessage(), ex3);
            }
            final X509Certificate certificate = this.getCertificate(pkcs7);
            if (certificate == null) {
                return null;
            }
            final PublicKey publicKey = certificate.getPublicKey();
            if (!SignerInfo.JAR_DISABLED_CHECK.permits(SignerInfo.SIG_PRIMITIVE_SET, publicKey)) {
                throw new SignatureException("Public key check failed. Disabled key used: " + KeyUtil.getKeySize(publicKey) + " bit " + publicKey.getAlgorithm());
            }
            if (certificate.hasUnsupportedCriticalExtension()) {
                throw new SignatureException("Certificate has unsupported critical extension(s)");
            }
            final boolean[] keyUsage = certificate.getKeyUsage();
            if (keyUsage != null) {
                KeyUsageExtension keyUsageExtension;
                try {
                    keyUsageExtension = new KeyUsageExtension(keyUsage);
                }
                catch (final IOException ex4) {
                    throw new SignatureException("Failed to parse keyUsage extension");
                }
                final boolean booleanValue = keyUsageExtension.get("digital_signature");
                final boolean booleanValue2 = keyUsageExtension.get("non_repudiation");
                if (!booleanValue && !booleanValue2) {
                    throw new SignatureException("Key usage restricted: cannot be used for digital signatures");
                }
            }
            final Signature instance = Signature.getInstance(sigAlg);
            final AlgorithmParameters parameters = this.digestEncryptionAlgorithmId.getParameters();
            try {
                SignatureUtil.initVerifyWithParam(instance, publicKey, SignatureUtil.getParamSpec(sigAlg, parameters));
            }
            catch (final ProviderException | InvalidAlgorithmParameterException | InvalidKeyException ex5) {
                throw new SignatureException(((Throwable)ex5).getMessage(), (Throwable)ex5);
            }
            instance.update(derEncoding);
            if (instance.verify(this.encryptedDigest)) {
                return this;
            }
        }
        catch (final IOException ex6) {
            throw new SignatureException("IO error verifying signature:\n" + ex6.getMessage());
        }
        return null;
    }
    
    SignerInfo verify(final PKCS7 pkcs7) throws NoSuchAlgorithmException, SignatureException {
        return this.verify(pkcs7, null);
    }
    
    public BigInteger getVersion() {
        return this.version;
    }
    
    public X500Name getIssuerName() {
        return this.issuerName;
    }
    
    public BigInteger getCertificateSerialNumber() {
        return this.certificateSerialNumber;
    }
    
    public AlgorithmId getDigestAlgorithmId() {
        return this.digestAlgorithmId;
    }
    
    public PKCS9Attributes getAuthenticatedAttributes() {
        return this.authenticatedAttributes;
    }
    
    public AlgorithmId getDigestEncryptionAlgorithmId() {
        return this.digestEncryptionAlgorithmId;
    }
    
    public byte[] getEncryptedDigest() {
        return this.encryptedDigest;
    }
    
    public PKCS9Attributes getUnauthenticatedAttributes() {
        return this.unauthenticatedAttributes;
    }
    
    public PKCS7 getTsToken() throws IOException {
        if (this.unauthenticatedAttributes == null) {
            return null;
        }
        final PKCS9Attribute attribute = this.unauthenticatedAttributes.getAttribute(PKCS9Attribute.SIGNATURE_TIMESTAMP_TOKEN_OID);
        if (attribute == null) {
            return null;
        }
        return new PKCS7((byte[])attribute.getValue());
    }
    
    public Timestamp getTimestamp() throws IOException, NoSuchAlgorithmException, SignatureException, CertificateException {
        if (this.timestamp != null || !this.hasTimestamp) {
            return this.timestamp;
        }
        final PKCS7 tsToken = this.getTsToken();
        if (tsToken == null) {
            this.hasTimestamp = false;
            return null;
        }
        final byte[] data = tsToken.getContentInfo().getData();
        final CertPath generateCertPath = CertificateFactory.getInstance("X.509").generateCertPath(tsToken.verify(data)[0].getCertificateChain(tsToken));
        final TimestampToken timestampToken = new TimestampToken(data);
        this.verifyTimestamp(timestampToken);
        return this.timestamp = new Timestamp(timestampToken.getDate(), generateCertPath);
    }
    
    private void verifyTimestamp(final TimestampToken timestampToken) throws NoSuchAlgorithmException, SignatureException {
        final String name = timestampToken.getHashAlgorithm().getName();
        if (!SignerInfo.JAR_DISABLED_CHECK.permits(SignerInfo.DIGEST_PRIMITIVE_SET, name, null)) {
            throw new SignatureException("Timestamp token digest check failed. Disabled algorithm used: " + name);
        }
        if (!Arrays.equals(timestampToken.getHashedMessage(), MessageDigest.getInstance(name).digest(this.encryptedDigest))) {
            throw new SignatureException("Signature timestamp (#" + timestampToken.getSerialNumber() + ") generated on " + timestampToken.getDate() + " is inapplicable");
        }
        if (SignerInfo.debug != null) {
            SignerInfo.debug.println();
            SignerInfo.debug.println("Detected signature timestamp (#" + timestampToken.getSerialNumber() + ") generated on " + timestampToken.getDate());
            SignerInfo.debug.println();
        }
    }
    
    @Override
    public String toString() {
        final HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
        String s = "" + "Signer Info for (issuer): " + this.issuerName + "\n" + "\tversion: " + Debug.toHexString(this.version) + "\n" + "\tcertificateSerialNumber: " + Debug.toHexString(this.certificateSerialNumber) + "\n" + "\tdigestAlgorithmId: " + this.digestAlgorithmId + "\n";
        if (this.authenticatedAttributes != null) {
            s = s + "\tauthenticatedAttributes: " + this.authenticatedAttributes + "\n";
        }
        String s2 = s + "\tdigestEncryptionAlgorithmId: " + this.digestEncryptionAlgorithmId + "\n" + "\tencryptedDigest: \n" + hexDumpEncoder.encodeBuffer(this.encryptedDigest) + "\n";
        if (this.unauthenticatedAttributes != null) {
            s2 = s2 + "\tunauthenticatedAttributes: " + this.unauthenticatedAttributes + "\n";
        }
        return s2;
    }
    
    static {
        DIGEST_PRIMITIVE_SET = Collections.unmodifiableSet((Set<? extends CryptoPrimitive>)EnumSet.of(CryptoPrimitive.MESSAGE_DIGEST));
        SIG_PRIMITIVE_SET = Collections.unmodifiableSet((Set<? extends CryptoPrimitive>)EnumSet.of(CryptoPrimitive.SIGNATURE));
        JAR_DISABLED_CHECK = new DisabledAlgorithmConstraints("jdk.jar.disabledAlgorithms");
        debug = Debug.getInstance("jar");
    }
}
