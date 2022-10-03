package sun.security.pkcs;

import java.security.SecureRandom;
import java.util.List;
import sun.security.timestamp.TimestampToken;
import sun.security.timestamp.TSResponse;
import java.util.Random;
import sun.security.timestamp.TSRequest;
import java.security.MessageDigest;
import java.io.ByteArrayOutputStream;
import sun.security.timestamp.Timestamper;
import sun.security.timestamp.HttpTimestamper;
import java.net.URI;
import sun.security.util.Debug;
import sun.security.x509.X509CertInfo;
import sun.security.x509.X500Name;
import java.util.Vector;
import java.security.SignatureException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import sun.security.util.DerEncoder;
import sun.security.util.DerOutputStream;
import java.io.OutputStream;
import java.security.cert.CRLException;
import sun.security.x509.X509CRLImpl;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import sun.security.x509.X509CertImpl;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.util.DerInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.security.Principal;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import sun.security.x509.AlgorithmId;
import java.math.BigInteger;
import sun.security.util.ObjectIdentifier;

public class PKCS7
{
    private ObjectIdentifier contentType;
    private BigInteger version;
    private AlgorithmId[] digestAlgorithmIds;
    private ContentInfo contentInfo;
    private X509Certificate[] certificates;
    private X509CRL[] crls;
    private SignerInfo[] signerInfos;
    private boolean oldStyle;
    private Principal[] certIssuerNames;
    private static final String KP_TIMESTAMPING_OID = "1.3.6.1.5.5.7.3.8";
    private static final String EXTENDED_KEY_USAGE_OID = "2.5.29.37";
    
    public PKCS7(final InputStream inputStream) throws ParsingException, IOException {
        this.version = null;
        this.digestAlgorithmIds = null;
        this.contentInfo = null;
        this.certificates = null;
        this.crls = null;
        this.signerInfos = null;
        this.oldStyle = false;
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        final byte[] array = new byte[dataInputStream.available()];
        dataInputStream.readFully(array);
        this.parse(new DerInputStream(array));
    }
    
    public PKCS7(final DerInputStream derInputStream) throws ParsingException {
        this.version = null;
        this.digestAlgorithmIds = null;
        this.contentInfo = null;
        this.certificates = null;
        this.crls = null;
        this.signerInfos = null;
        this.oldStyle = false;
        this.parse(derInputStream);
    }
    
    public PKCS7(final byte[] array) throws ParsingException {
        this.version = null;
        this.digestAlgorithmIds = null;
        this.contentInfo = null;
        this.certificates = null;
        this.crls = null;
        this.signerInfos = null;
        this.oldStyle = false;
        try {
            this.parse(new DerInputStream(array));
        }
        catch (final IOException ex) {
            final ParsingException ex2 = new ParsingException("Unable to parse the encoded bytes");
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    private void parse(final DerInputStream derInputStream) throws ParsingException {
        try {
            derInputStream.mark(derInputStream.available());
            this.parse(derInputStream, false);
        }
        catch (final IOException ex) {
            try {
                derInputStream.reset();
                this.parse(derInputStream, true);
                this.oldStyle = true;
            }
            catch (final IOException ex2) {
                final ParsingException ex3 = new ParsingException(ex2.getMessage());
                ex3.initCause(ex);
                ex3.addSuppressed(ex2);
                throw ex3;
            }
        }
    }
    
    private void parse(final DerInputStream derInputStream, final boolean b) throws IOException {
        this.contentInfo = new ContentInfo(derInputStream, b);
        this.contentType = this.contentInfo.contentType;
        final DerValue content = this.contentInfo.getContent();
        if (this.contentType.equals((Object)ContentInfo.SIGNED_DATA_OID)) {
            this.parseSignedData(content);
        }
        else if (this.contentType.equals((Object)ContentInfo.OLD_SIGNED_DATA_OID)) {
            this.parseOldSignedData(content);
        }
        else {
            if (!this.contentType.equals((Object)ContentInfo.NETSCAPE_CERT_SEQUENCE_OID)) {
                throw new ParsingException("content type " + this.contentType + " not supported.");
            }
            this.parseNetscapeCertChain(content);
        }
    }
    
    public PKCS7(final AlgorithmId[] digestAlgorithmIds, final ContentInfo contentInfo, final X509Certificate[] certificates, final X509CRL[] crls, final SignerInfo[] signerInfos) {
        this.version = null;
        this.digestAlgorithmIds = null;
        this.contentInfo = null;
        this.certificates = null;
        this.crls = null;
        this.signerInfos = null;
        this.oldStyle = false;
        this.version = BigInteger.ONE;
        this.digestAlgorithmIds = digestAlgorithmIds;
        this.contentInfo = contentInfo;
        this.certificates = certificates;
        this.crls = crls;
        this.signerInfos = signerInfos;
    }
    
    public PKCS7(final AlgorithmId[] array, final ContentInfo contentInfo, final X509Certificate[] array2, final SignerInfo[] array3) {
        this(array, contentInfo, array2, null, array3);
    }
    
    private void parseNetscapeCertChain(final DerValue derValue) throws ParsingException, IOException {
        final DerValue[] sequence = new DerInputStream(derValue.toByteArray()).getSequence(2);
        this.certificates = new X509Certificate[sequence.length];
        CertificateFactory instance = null;
        try {
            instance = CertificateFactory.getInstance("X.509");
        }
        catch (final CertificateException ex) {}
        for (int i = 0; i < sequence.length; ++i) {
            InputStream inputStream = null;
            try {
                if (instance == null) {
                    this.certificates[i] = new X509CertImpl(sequence[i]);
                }
                else {
                    inputStream = new ByteArrayInputStream(sequence[i].toByteArray());
                    this.certificates[i] = (X509Certificate)instance.generateCertificate(inputStream);
                    ((ByteArrayInputStream)inputStream).close();
                    inputStream = null;
                }
            }
            catch (final CertificateException ex2) {
                final ParsingException ex3 = new ParsingException(ex2.getMessage());
                ex3.initCause(ex2);
                throw ex3;
            }
            catch (final IOException ex4) {
                final ParsingException ex5 = new ParsingException(ex4.getMessage());
                ex5.initCause(ex4);
                throw ex5;
            }
            finally {
                if (inputStream != null) {
                    ((ByteArrayInputStream)inputStream).close();
                }
            }
        }
    }
    
    private void parseSignedData(final DerValue derValue) throws ParsingException, IOException {
        final DerInputStream derInputStream = derValue.toDerInputStream();
        this.version = derInputStream.getBigInteger();
        final DerValue[] set = derInputStream.getSet(1);
        final int length = set.length;
        this.digestAlgorithmIds = new AlgorithmId[length];
        try {
            for (int i = 0; i < length; ++i) {
                this.digestAlgorithmIds[i] = AlgorithmId.parse(set[i]);
            }
        }
        catch (final IOException ex) {
            final ParsingException ex2 = new ParsingException("Error parsing digest AlgorithmId IDs: " + ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        this.contentInfo = new ContentInfo(derInputStream);
        CertificateFactory instance = null;
        try {
            instance = CertificateFactory.getInstance("X.509");
        }
        catch (final CertificateException ex3) {}
        if ((byte)derInputStream.peekByte() == -96) {
            final DerValue[] set2 = derInputStream.getSet(2, true);
            final int length2 = set2.length;
            this.certificates = new X509Certificate[length2];
            int n = 0;
            for (int j = 0; j < length2; ++j) {
                InputStream inputStream = null;
                try {
                    if (set2[j].getTag() == 48) {
                        if (instance == null) {
                            this.certificates[n] = new X509CertImpl(set2[j]);
                        }
                        else {
                            inputStream = new ByteArrayInputStream(set2[j].toByteArray());
                            this.certificates[n] = (X509Certificate)instance.generateCertificate(inputStream);
                            ((ByteArrayInputStream)inputStream).close();
                            inputStream = null;
                        }
                        ++n;
                    }
                }
                catch (final CertificateException ex4) {
                    final ParsingException ex5 = new ParsingException(ex4.getMessage());
                    ex5.initCause(ex4);
                    throw ex5;
                }
                catch (final IOException ex6) {
                    final ParsingException ex7 = new ParsingException(ex6.getMessage());
                    ex7.initCause(ex6);
                    throw ex7;
                }
                finally {
                    if (inputStream != null) {
                        ((ByteArrayInputStream)inputStream).close();
                    }
                }
            }
            if (n != length2) {
                this.certificates = Arrays.copyOf(this.certificates, n);
            }
        }
        if ((byte)derInputStream.peekByte() == -95) {
            final DerValue[] set3 = derInputStream.getSet(1, true);
            final int length3 = set3.length;
            this.crls = new X509CRL[length3];
            for (int k = 0; k < length3; ++k) {
                InputStream inputStream2 = null;
                try {
                    if (instance == null) {
                        this.crls[k] = new X509CRLImpl(set3[k]);
                    }
                    else {
                        inputStream2 = new ByteArrayInputStream(set3[k].toByteArray());
                        this.crls[k] = (X509CRL)instance.generateCRL(inputStream2);
                        ((ByteArrayInputStream)inputStream2).close();
                        inputStream2 = null;
                    }
                }
                catch (final CRLException ex8) {
                    final ParsingException ex9 = new ParsingException(ex8.getMessage());
                    ex9.initCause(ex8);
                    throw ex9;
                }
                finally {
                    if (inputStream2 != null) {
                        ((ByteArrayInputStream)inputStream2).close();
                    }
                }
            }
        }
        final DerValue[] set4 = derInputStream.getSet(1);
        final int length4 = set4.length;
        this.signerInfos = new SignerInfo[length4];
        for (int l = 0; l < length4; ++l) {
            this.signerInfos[l] = new SignerInfo(set4[l].toDerInputStream());
        }
    }
    
    private void parseOldSignedData(final DerValue derValue) throws ParsingException, IOException {
        final DerInputStream derInputStream = derValue.toDerInputStream();
        this.version = derInputStream.getBigInteger();
        final DerValue[] set = derInputStream.getSet(1);
        final int length = set.length;
        this.digestAlgorithmIds = new AlgorithmId[length];
        try {
            for (int i = 0; i < length; ++i) {
                this.digestAlgorithmIds[i] = AlgorithmId.parse(set[i]);
            }
        }
        catch (final IOException ex) {
            throw new ParsingException("Error parsing digest AlgorithmId IDs");
        }
        this.contentInfo = new ContentInfo(derInputStream, true);
        CertificateFactory instance = null;
        try {
            instance = CertificateFactory.getInstance("X.509");
        }
        catch (final CertificateException ex2) {}
        final DerValue[] set2 = derInputStream.getSet(2);
        final int length2 = set2.length;
        this.certificates = new X509Certificate[length2];
        for (int j = 0; j < length2; ++j) {
            InputStream inputStream = null;
            try {
                if (instance == null) {
                    this.certificates[j] = new X509CertImpl(set2[j]);
                }
                else {
                    inputStream = new ByteArrayInputStream(set2[j].toByteArray());
                    this.certificates[j] = (X509Certificate)instance.generateCertificate(inputStream);
                    ((ByteArrayInputStream)inputStream).close();
                    inputStream = null;
                }
            }
            catch (final CertificateException ex3) {
                final ParsingException ex4 = new ParsingException(ex3.getMessage());
                ex4.initCause(ex3);
                throw ex4;
            }
            catch (final IOException ex5) {
                final ParsingException ex6 = new ParsingException(ex5.getMessage());
                ex6.initCause(ex5);
                throw ex6;
            }
            finally {
                if (inputStream != null) {
                    ((ByteArrayInputStream)inputStream).close();
                }
            }
        }
        derInputStream.getSet(0);
        final DerValue[] set3 = derInputStream.getSet(1);
        final int length3 = set3.length;
        this.signerInfos = new SignerInfo[length3];
        for (int k = 0; k < length3; ++k) {
            this.signerInfos[k] = new SignerInfo(set3[k].toDerInputStream(), true);
        }
    }
    
    public void encodeSignedData(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        this.encodeSignedData(derOutputStream);
        outputStream.write(derOutputStream.toByteArray());
    }
    
    public void encodeSignedData(final DerOutputStream derOutputStream) throws IOException {
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putInteger(this.version);
        derOutputStream2.putOrderedSetOf((byte)49, this.digestAlgorithmIds);
        this.contentInfo.encode(derOutputStream2);
        if (this.certificates != null && this.certificates.length != 0) {
            final X509CertImpl[] array = new X509CertImpl[this.certificates.length];
            for (int i = 0; i < this.certificates.length; ++i) {
                if (this.certificates[i] instanceof X509CertImpl) {
                    array[i] = (X509CertImpl)this.certificates[i];
                }
                else {
                    try {
                        array[i] = new X509CertImpl(this.certificates[i].getEncoded());
                    }
                    catch (final CertificateException ex) {
                        throw new IOException(ex);
                    }
                }
            }
            derOutputStream2.putOrderedSetOf((byte)(-96), array);
        }
        if (this.crls != null && this.crls.length != 0) {
            final HashSet set = new HashSet(this.crls.length);
            for (final X509CRL x509CRL : this.crls) {
                if (x509CRL instanceof X509CRLImpl) {
                    set.add(x509CRL);
                }
                else {
                    try {
                        set.add(new X509CRLImpl(x509CRL.getEncoded()));
                    }
                    catch (final CRLException ex2) {
                        throw new IOException(ex2);
                    }
                }
            }
            derOutputStream2.putOrderedSetOf((byte)(-95), (DerEncoder[])set.toArray(new X509CRLImpl[set.size()]));
        }
        derOutputStream2.putOrderedSetOf((byte)49, this.signerInfos);
        new ContentInfo(ContentInfo.SIGNED_DATA_OID, new DerValue((byte)48, derOutputStream2.toByteArray())).encode(derOutputStream);
    }
    
    public SignerInfo verify(final SignerInfo signerInfo, final byte[] array) throws NoSuchAlgorithmException, SignatureException {
        return signerInfo.verify(this, array);
    }
    
    public SignerInfo[] verify(final byte[] array) throws NoSuchAlgorithmException, SignatureException {
        final Vector vector = new Vector();
        for (int i = 0; i < this.signerInfos.length; ++i) {
            final SignerInfo verify = this.verify(this.signerInfos[i], array);
            if (verify != null) {
                vector.addElement(verify);
            }
        }
        if (!vector.isEmpty()) {
            final SignerInfo[] array2 = new SignerInfo[vector.size()];
            vector.copyInto(array2);
            return array2;
        }
        return null;
    }
    
    public SignerInfo[] verify() throws NoSuchAlgorithmException, SignatureException {
        return this.verify(null);
    }
    
    public BigInteger getVersion() {
        return this.version;
    }
    
    public AlgorithmId[] getDigestAlgorithmIds() {
        return this.digestAlgorithmIds;
    }
    
    public ContentInfo getContentInfo() {
        return this.contentInfo;
    }
    
    public X509Certificate[] getCertificates() {
        if (this.certificates != null) {
            return this.certificates.clone();
        }
        return null;
    }
    
    public X509CRL[] getCRLs() {
        if (this.crls != null) {
            return this.crls.clone();
        }
        return null;
    }
    
    public SignerInfo[] getSignerInfos() {
        return this.signerInfos;
    }
    
    public X509Certificate getCertificate(final BigInteger bigInteger, final X500Name x500Name) {
        if (this.certificates != null) {
            if (this.certIssuerNames == null) {
                this.populateCertIssuerNames();
            }
            for (int i = 0; i < this.certificates.length; ++i) {
                final X509Certificate x509Certificate = this.certificates[i];
                if (bigInteger.equals(x509Certificate.getSerialNumber()) && x500Name.equals(this.certIssuerNames[i])) {
                    return x509Certificate;
                }
            }
        }
        return null;
    }
    
    private void populateCertIssuerNames() {
        if (this.certificates == null) {
            return;
        }
        this.certIssuerNames = new Principal[this.certificates.length];
        for (int i = 0; i < this.certificates.length; ++i) {
            final X509Certificate x509Certificate = this.certificates[i];
            Principal issuerDN = x509Certificate.getIssuerDN();
            if (!(issuerDN instanceof X500Name)) {
                try {
                    issuerDN = (Principal)new X509CertInfo(x509Certificate.getTBSCertificate()).get("issuer.dname");
                }
                catch (final Exception ex) {}
            }
            this.certIssuerNames[i] = issuerDN;
        }
    }
    
    @Override
    public String toString() {
        String s = "" + this.contentInfo + "\n";
        if (this.version != null) {
            s = s + "PKCS7 :: version: " + Debug.toHexString(this.version) + "\n";
        }
        if (this.digestAlgorithmIds != null) {
            s += "PKCS7 :: digest AlgorithmIds: \n";
            for (int i = 0; i < this.digestAlgorithmIds.length; ++i) {
                s = s + "\t" + this.digestAlgorithmIds[i] + "\n";
            }
        }
        if (this.certificates != null) {
            s += "PKCS7 :: certificates: \n";
            for (int j = 0; j < this.certificates.length; ++j) {
                s = s + "\t" + j + ".   " + this.certificates[j] + "\n";
            }
        }
        if (this.crls != null) {
            s += "PKCS7 :: crls: \n";
            for (int k = 0; k < this.crls.length; ++k) {
                s = s + "\t" + k + ".   " + this.crls[k] + "\n";
            }
        }
        if (this.signerInfos != null) {
            s += "PKCS7 :: signer infos: \n";
            for (int l = 0; l < this.signerInfos.length; ++l) {
                s = s + "\t" + l + ".  " + this.signerInfos[l] + "\n";
            }
        }
        return s;
    }
    
    public boolean isOldStyle() {
        return this.oldStyle;
    }
    
    public static byte[] generateSignedData(final byte[] array, final X509Certificate[] array2, final byte[] array3, final String s, final URI uri, final String s2, final String s3) throws CertificateException, IOException, NoSuchAlgorithmException {
        PKCS9Attributes pkcs9Attributes = null;
        if (uri != null) {
            pkcs9Attributes = new PKCS9Attributes(new PKCS9Attribute[] { new PKCS9Attribute("SignatureTimestampToken", generateTimestampToken(new HttpTimestamper(uri), s2, s3, array)) });
        }
        final SignerInfo signerInfo = new SignerInfo(X500Name.asX500Name(array2[0].getIssuerX500Principal()), array2[0].getSerialNumber(), AlgorithmId.get(AlgorithmId.getDigAlgFromSigAlg(s)), null, AlgorithmId.get(AlgorithmId.getEncAlgFromSigAlg(s)), array, pkcs9Attributes);
        final PKCS7 pkcs7 = new PKCS7(new AlgorithmId[] { signerInfo.getDigestAlgorithmId() }, (array3 == null) ? new ContentInfo(ContentInfo.DATA_OID, null) : new ContentInfo(array3), array2, new SignerInfo[] { signerInfo });
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        pkcs7.encodeSignedData(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    
    private static byte[] generateTimestampToken(final Timestamper timestamper, final String s, final String s2, final byte[] array) throws IOException, CertificateException {
        TSRequest tsRequest;
        try {
            tsRequest = new TSRequest(s, array, MessageDigest.getInstance(s2));
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException(ex);
        }
        BigInteger nonce = null;
        if (SecureRandomHolder.RANDOM != null) {
            nonce = new BigInteger(64, SecureRandomHolder.RANDOM);
            tsRequest.setNonce(nonce);
        }
        tsRequest.requestCertificate(true);
        final TSResponse generateTimestamp = timestamper.generateTimestamp(tsRequest);
        final int statusCode = generateTimestamp.getStatusCode();
        if (statusCode != 0 && statusCode != 1) {
            throw new IOException("Error generating timestamp: " + generateTimestamp.getStatusCodeAsText() + " " + generateTimestamp.getFailureCodeAsText());
        }
        if (s != null && !s.equals(generateTimestamp.getTimestampToken().getPolicyID())) {
            throw new IOException("TSAPolicyID changed in timestamp token");
        }
        final PKCS7 token = generateTimestamp.getToken();
        final TimestampToken timestampToken = generateTimestamp.getTimestampToken();
        try {
            if (!timestampToken.getHashAlgorithm().equals(AlgorithmId.get(s2))) {
                throw new IOException("Digest algorithm not " + s2 + " in timestamp token");
            }
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new IllegalArgumentException();
        }
        if (!MessageDigest.isEqual(timestampToken.getHashedMessage(), tsRequest.getHashedMessage())) {
            throw new IOException("Digest octets changed in timestamp token");
        }
        final BigInteger nonce2 = timestampToken.getNonce();
        if (nonce2 == null && nonce != null) {
            throw new IOException("Nonce missing in timestamp token");
        }
        if (nonce2 != null && !nonce2.equals(nonce)) {
            throw new IOException("Nonce changed in timestamp token");
        }
        final SignerInfo[] signerInfos = token.getSignerInfos();
        for (int length = signerInfos.length, i = 0; i < length; ++i) {
            final X509Certificate certificate = signerInfos[i].getCertificate(token);
            if (certificate == null) {
                throw new CertificateException("Certificate not included in timestamp token");
            }
            if (!certificate.getCriticalExtensionOIDs().contains("2.5.29.37")) {
                throw new CertificateException("Certificate is not valid for timestamping");
            }
            final List<String> extendedKeyUsage = certificate.getExtendedKeyUsage();
            if (extendedKeyUsage == null || !extendedKeyUsage.contains("1.3.6.1.5.5.7.3.8")) {
                throw new CertificateException("Certificate is not valid for timestamping");
            }
        }
        return generateTimestamp.getEncodedToken();
    }
    
    private static class SecureRandomHolder
    {
        static final SecureRandom RANDOM;
        
        static {
            SecureRandom instance = null;
            try {
                instance = SecureRandom.getInstance("SHA1PRNG");
            }
            catch (final NoSuchAlgorithmException ex) {}
            RANDOM = instance;
        }
    }
}
