package sun.security.x509;

import java.security.MessageDigest;
import sun.security.provider.X509Factory;
import java.security.cert.Certificate;
import sun.security.util.DerInputStream;
import java.util.ArrayList;
import java.util.Collections;
import sun.security.util.ObjectIdentifier;
import java.util.Iterator;
import java.util.TreeSet;
import javax.security.auth.x500.X500Principal;
import java.security.Principal;
import java.math.BigInteger;
import sun.misc.HexDumpEncoder;
import java.util.Enumeration;
import java.security.cert.CertificateParsingException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateExpiredException;
import java.util.Date;
import sun.security.util.DerOutputStream;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.InvalidAlgorithmParameterException;
import java.security.ProviderException;
import sun.security.util.SignatureUtil;
import java.security.Signature;
import java.security.SignatureException;
import java.security.NoSuchProviderException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.io.OutputStream;
import sun.security.util.Pem;
import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import sun.security.util.DerValue;
import java.util.concurrent.ConcurrentHashMap;
import java.security.PublicKey;
import java.util.Set;
import java.util.List;
import java.util.Collection;
import sun.security.util.DerEncoder;
import java.security.cert.X509Certificate;

public class X509CertImpl extends X509Certificate implements DerEncoder
{
    private static final long serialVersionUID = -3457612960190864406L;
    private static final String DOT = ".";
    public static final String NAME = "x509";
    public static final String INFO = "info";
    public static final String ALG_ID = "algorithm";
    public static final String SIGNATURE = "signature";
    public static final String SIGNED_CERT = "signed_cert";
    public static final String SUBJECT_DN = "x509.info.subject.dname";
    public static final String ISSUER_DN = "x509.info.issuer.dname";
    public static final String SERIAL_ID = "x509.info.serialNumber.number";
    public static final String PUBLIC_KEY = "x509.info.key.value";
    public static final String VERSION = "x509.info.version.number";
    public static final String SIG_ALG = "x509.algorithm";
    public static final String SIG = "x509.signature";
    private boolean readOnly;
    private byte[] signedCert;
    protected X509CertInfo info;
    protected AlgorithmId algId;
    protected byte[] signature;
    private static final String KEY_USAGE_OID = "2.5.29.15";
    private static final String EXTENDED_KEY_USAGE_OID = "2.5.29.37";
    private static final String BASIC_CONSTRAINT_OID = "2.5.29.19";
    private static final String SUBJECT_ALT_NAME_OID = "2.5.29.17";
    private static final String ISSUER_ALT_NAME_OID = "2.5.29.18";
    private static final String AUTH_INFO_ACCESS_OID = "1.3.6.1.5.5.7.1.1";
    private static final int NUM_STANDARD_KEY_USAGE = 9;
    private Collection<List<?>> subjectAlternativeNames;
    private Collection<List<?>> issuerAlternativeNames;
    private List<String> extKeyUsage;
    private Set<AccessDescription> authInfoAccess;
    private PublicKey verifiedPublicKey;
    private String verifiedProvider;
    private boolean verificationResult;
    private ConcurrentHashMap<String, String> fingerprints;
    
    public X509CertImpl() {
        this.readOnly = false;
        this.signedCert = null;
        this.info = null;
        this.algId = null;
        this.signature = null;
        this.fingerprints = new ConcurrentHashMap<String, String>(2);
    }
    
    public X509CertImpl(final byte[] array) throws CertificateException {
        this.readOnly = false;
        this.signedCert = null;
        this.info = null;
        this.algId = null;
        this.signature = null;
        this.fingerprints = new ConcurrentHashMap<String, String>(2);
        try {
            this.parse(new DerValue(array));
        }
        catch (final IOException ex) {
            this.signedCert = null;
            throw new CertificateException("Unable to initialize, " + ex, ex);
        }
    }
    
    public X509CertImpl(final InputStream inputStream) throws CertificateException {
        this.readOnly = false;
        this.signedCert = null;
        this.info = null;
        this.algId = null;
        this.signature = null;
        this.fingerprints = new ConcurrentHashMap<String, String>(2);
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        DerValue rfc1421Cert;
        try {
            bufferedInputStream.mark(Integer.MAX_VALUE);
            rfc1421Cert = this.readRFC1421Cert(bufferedInputStream);
        }
        catch (final IOException ex) {
            try {
                bufferedInputStream.reset();
                rfc1421Cert = new DerValue(bufferedInputStream);
            }
            catch (final IOException ex2) {
                throw new CertificateException("Input stream must be either DER-encoded bytes or RFC1421 hex-encoded DER-encoded bytes: " + ex2.getMessage(), ex2);
            }
        }
        try {
            this.parse(rfc1421Cert);
        }
        catch (final IOException ex3) {
            this.signedCert = null;
            throw new CertificateException("Unable to parse DER value of certificate, " + ex3, ex3);
        }
    }
    
    private DerValue readRFC1421Cert(final InputStream inputStream) throws IOException {
        DerValue derValue = null;
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "ASCII"));
        String line;
        try {
            line = bufferedReader.readLine();
        }
        catch (final IOException ex) {
            throw new IOException("Unable to read InputStream: " + ex.getMessage());
        }
        if (line.equals("-----BEGIN CERTIFICATE-----")) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                String line2;
                while ((line2 = bufferedReader.readLine()) != null) {
                    if (line2.equals("-----END CERTIFICATE-----")) {
                        derValue = new DerValue(byteArrayOutputStream.toByteArray());
                        break;
                    }
                    byteArrayOutputStream.write(Pem.decode(line2));
                }
            }
            catch (final IOException ex2) {
                throw new IOException("Unable to read InputStream: " + ex2.getMessage());
            }
            return derValue;
        }
        throw new IOException("InputStream is not RFC1421 hex-encoded DER bytes");
    }
    
    public X509CertImpl(final X509CertInfo info) {
        this.readOnly = false;
        this.signedCert = null;
        this.info = null;
        this.algId = null;
        this.signature = null;
        this.fingerprints = new ConcurrentHashMap<String, String>(2);
        this.info = info;
    }
    
    public X509CertImpl(final DerValue derValue) throws CertificateException {
        this.readOnly = false;
        this.signedCert = null;
        this.info = null;
        this.algId = null;
        this.signature = null;
        this.fingerprints = new ConcurrentHashMap<String, String>(2);
        try {
            this.parse(derValue);
        }
        catch (final IOException ex) {
            this.signedCert = null;
            throw new CertificateException("Unable to initialize, " + ex, ex);
        }
    }
    
    public void encode(final OutputStream outputStream) throws CertificateEncodingException {
        if (this.signedCert == null) {
            throw new CertificateEncodingException("Null certificate to encode");
        }
        try {
            outputStream.write(this.signedCert.clone());
        }
        catch (final IOException ex) {
            throw new CertificateEncodingException(ex.toString());
        }
    }
    
    @Override
    public void derEncode(final OutputStream outputStream) throws IOException {
        if (this.signedCert == null) {
            throw new IOException("Null certificate to encode");
        }
        outputStream.write(this.signedCert.clone());
    }
    
    @Override
    public byte[] getEncoded() throws CertificateEncodingException {
        return this.getEncodedInternal().clone();
    }
    
    public byte[] getEncodedInternal() throws CertificateEncodingException {
        if (this.signedCert == null) {
            throw new CertificateEncodingException("Null certificate to encode");
        }
        return this.signedCert;
    }
    
    @Override
    public void verify(final PublicKey publicKey) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        this.verify(publicKey, "");
    }
    
    @Override
    public synchronized void verify(final PublicKey verifiedPublicKey, String verifiedProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        if (verifiedProvider == null) {
            verifiedProvider = "";
        }
        if (this.verifiedPublicKey != null && this.verifiedPublicKey.equals(verifiedPublicKey) && verifiedProvider.equals(this.verifiedProvider)) {
            if (this.verificationResult) {
                return;
            }
            throw new SignatureException("Signature does not match.");
        }
        else {
            if (this.signedCert == null) {
                throw new CertificateEncodingException("Uninitialized certificate");
            }
            final String name = this.algId.getName();
            Signature signature;
            if (verifiedProvider.length() == 0) {
                signature = Signature.getInstance(name);
            }
            else {
                signature = Signature.getInstance(name, verifiedProvider);
            }
            try {
                SignatureUtil.initVerifyWithParam(signature, verifiedPublicKey, SignatureUtil.getParamSpec(name, this.getSigAlgParams()));
            }
            catch (final ProviderException ex) {
                throw new CertificateException(ex.getMessage(), ex.getCause());
            }
            catch (final InvalidAlgorithmParameterException ex2) {
                throw new CertificateException(ex2);
            }
            final byte[] encodedInfo = this.info.getEncodedInfo();
            signature.update(encodedInfo, 0, encodedInfo.length);
            this.verificationResult = signature.verify(this.signature);
            this.verifiedPublicKey = verifiedPublicKey;
            this.verifiedProvider = verifiedProvider;
            if (!this.verificationResult) {
                throw new SignatureException("Signature does not match.");
            }
        }
    }
    
    @Override
    public synchronized void verify(final PublicKey verifiedPublicKey, final Provider provider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        if (this.signedCert == null) {
            throw new CertificateEncodingException("Uninitialized certificate");
        }
        final String name = this.algId.getName();
        Signature signature;
        if (provider == null) {
            signature = Signature.getInstance(name);
        }
        else {
            signature = Signature.getInstance(name, provider);
        }
        try {
            SignatureUtil.initVerifyWithParam(signature, verifiedPublicKey, SignatureUtil.getParamSpec(name, this.getSigAlgParams()));
        }
        catch (final ProviderException ex) {
            throw new CertificateException(ex.getMessage(), ex.getCause());
        }
        catch (final InvalidAlgorithmParameterException ex2) {
            throw new CertificateException(ex2);
        }
        final byte[] encodedInfo = this.info.getEncodedInfo();
        signature.update(encodedInfo, 0, encodedInfo.length);
        this.verificationResult = signature.verify(this.signature);
        this.verifiedPublicKey = verifiedPublicKey;
        if (!this.verificationResult) {
            throw new SignatureException("Signature does not match.");
        }
    }
    
    public void sign(final PrivateKey privateKey, final String s) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        this.sign(privateKey, s, null);
    }
    
    public void sign(final PrivateKey privateKey, final String s, final String s2) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        try {
            this.sign(privateKey, null, s, s2);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new SignatureException(ex);
        }
    }
    
    public void sign(final PrivateKey privateKey, final AlgorithmParameterSpec algorithmParameterSpec, final String s, final String s2) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchProviderException, SignatureException {
        try {
            if (this.readOnly) {
                throw new CertificateEncodingException("cannot over-write existing certificate");
            }
            Signature signature;
            if (s2 == null || s2.length() == 0) {
                signature = Signature.getInstance(s);
            }
            else {
                signature = Signature.getInstance(s, s2);
            }
            SignatureUtil.initSignWithParam(signature, privateKey, algorithmParameterSpec, null);
            if (algorithmParameterSpec != null) {
                this.algId = AlgorithmId.get(signature.getParameters());
            }
            else {
                this.algId = AlgorithmId.get(s);
            }
            final DerOutputStream derOutputStream = new DerOutputStream();
            final DerOutputStream derOutputStream2 = new DerOutputStream();
            this.info.encode(derOutputStream2);
            final byte[] byteArray = derOutputStream2.toByteArray();
            this.algId.encode(derOutputStream2);
            signature.update(byteArray, 0, byteArray.length);
            derOutputStream2.putBitString(this.signature = signature.sign());
            derOutputStream.write((byte)48, derOutputStream2);
            this.signedCert = derOutputStream.toByteArray();
            this.readOnly = true;
        }
        catch (final IOException ex) {
            throw new CertificateEncodingException(ex.toString());
        }
    }
    
    @Override
    public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
        this.checkValidity(new Date());
    }
    
    @Override
    public void checkValidity(final Date date) throws CertificateExpiredException, CertificateNotYetValidException {
        CertificateValidity certificateValidity;
        try {
            certificateValidity = (CertificateValidity)this.info.get("validity");
        }
        catch (final Exception ex) {
            throw new CertificateNotYetValidException("Incorrect validity period");
        }
        if (certificateValidity == null) {
            throw new CertificateNotYetValidException("Null validity period");
        }
        certificateValidity.valid(date);
    }
    
    public Object get(final String s) throws CertificateParsingException {
        final X509AttributeName x509AttributeName = new X509AttributeName(s);
        final String prefix = x509AttributeName.getPrefix();
        if (!prefix.equalsIgnoreCase("x509")) {
            throw new CertificateParsingException("Invalid root of attribute name, expected [x509], received [" + prefix + "]");
        }
        final X509AttributeName x509AttributeName2 = new X509AttributeName(x509AttributeName.getSuffix());
        final String prefix2 = x509AttributeName2.getPrefix();
        if (prefix2.equalsIgnoreCase("info")) {
            if (this.info == null) {
                return null;
            }
            if (x509AttributeName2.getSuffix() != null) {
                try {
                    return this.info.get(x509AttributeName2.getSuffix());
                }
                catch (final IOException ex) {
                    throw new CertificateParsingException(ex.toString());
                }
                catch (final CertificateException ex2) {
                    throw new CertificateParsingException(ex2.toString());
                }
            }
            return this.info;
        }
        else {
            if (prefix2.equalsIgnoreCase("algorithm")) {
                return this.algId;
            }
            if (prefix2.equalsIgnoreCase("signature")) {
                if (this.signature != null) {
                    return this.signature.clone();
                }
                return null;
            }
            else {
                if (!prefix2.equalsIgnoreCase("signed_cert")) {
                    throw new CertificateParsingException("Attribute name not recognized or get() not allowed for the same: " + prefix2);
                }
                if (this.signedCert != null) {
                    return this.signedCert.clone();
                }
                return null;
            }
        }
    }
    
    public void set(final String s, final Object o) throws CertificateException, IOException {
        if (this.readOnly) {
            throw new CertificateException("cannot over-write existing certificate");
        }
        final X509AttributeName x509AttributeName = new X509AttributeName(s);
        final String prefix = x509AttributeName.getPrefix();
        if (!prefix.equalsIgnoreCase("x509")) {
            throw new CertificateException("Invalid root of attribute name, expected [x509], received " + prefix);
        }
        final X509AttributeName x509AttributeName2 = new X509AttributeName(x509AttributeName.getSuffix());
        final String prefix2 = x509AttributeName2.getPrefix();
        if (prefix2.equalsIgnoreCase("info")) {
            if (x509AttributeName2.getSuffix() == null) {
                if (!(o instanceof X509CertInfo)) {
                    throw new CertificateException("Attribute value should be of type X509CertInfo.");
                }
                this.info = (X509CertInfo)o;
                this.signedCert = null;
            }
            else {
                this.info.set(x509AttributeName2.getSuffix(), o);
                this.signedCert = null;
            }
            return;
        }
        throw new CertificateException("Attribute name not recognized or set() not allowed for the same: " + prefix2);
    }
    
    public void delete(final String s) throws CertificateException, IOException {
        if (this.readOnly) {
            throw new CertificateException("cannot over-write existing certificate");
        }
        final X509AttributeName x509AttributeName = new X509AttributeName(s);
        final String prefix = x509AttributeName.getPrefix();
        if (!prefix.equalsIgnoreCase("x509")) {
            throw new CertificateException("Invalid root of attribute name, expected [x509], received " + prefix);
        }
        final X509AttributeName x509AttributeName2 = new X509AttributeName(x509AttributeName.getSuffix());
        final String prefix2 = x509AttributeName2.getPrefix();
        if (prefix2.equalsIgnoreCase("info")) {
            if (x509AttributeName2.getSuffix() != null) {
                this.info = null;
            }
            else {
                this.info.delete(x509AttributeName2.getSuffix());
            }
        }
        else if (prefix2.equalsIgnoreCase("algorithm")) {
            this.algId = null;
        }
        else if (prefix2.equalsIgnoreCase("signature")) {
            this.signature = null;
        }
        else {
            if (!prefix2.equalsIgnoreCase("signed_cert")) {
                throw new CertificateException("Attribute name not recognized or delete() not allowed for the same: " + prefix2);
            }
            this.signedCert = null;
        }
    }
    
    public Enumeration<String> getElements() {
        final AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
        attributeNameEnumeration.addElement("x509.info");
        attributeNameEnumeration.addElement("x509.algorithm");
        attributeNameEnumeration.addElement("x509.signature");
        attributeNameEnumeration.addElement("x509.signed_cert");
        return attributeNameEnumeration.elements();
    }
    
    public String getName() {
        return "x509";
    }
    
    @Override
    public String toString() {
        if (this.info == null || this.algId == null || this.signature == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        sb.append(this.info.toString() + "\n");
        sb.append("  Algorithm: [" + this.algId.toString() + "]\n");
        sb.append("  Signature:\n" + new HexDumpEncoder().encodeBuffer(this.signature));
        sb.append("\n]");
        return sb.toString();
    }
    
    @Override
    public PublicKey getPublicKey() {
        if (this.info == null) {
            return null;
        }
        try {
            return (PublicKey)this.info.get("key.value");
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    @Override
    public int getVersion() {
        if (this.info == null) {
            return -1;
        }
        try {
            return (int)this.info.get("version.number") + 1;
        }
        catch (final Exception ex) {
            return -1;
        }
    }
    
    @Override
    public BigInteger getSerialNumber() {
        final SerialNumber serialNumberObject = this.getSerialNumberObject();
        return (serialNumberObject != null) ? serialNumberObject.getNumber() : null;
    }
    
    public SerialNumber getSerialNumberObject() {
        if (this.info == null) {
            return null;
        }
        try {
            return (SerialNumber)this.info.get("serialNumber.number");
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    @Override
    public Principal getSubjectDN() {
        if (this.info == null) {
            return null;
        }
        try {
            return (Principal)this.info.get("subject.dname");
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    @Override
    public X500Principal getSubjectX500Principal() {
        if (this.info == null) {
            return null;
        }
        try {
            return (X500Principal)this.info.get("subject.x500principal");
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    @Override
    public Principal getIssuerDN() {
        if (this.info == null) {
            return null;
        }
        try {
            return (Principal)this.info.get("issuer.dname");
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    @Override
    public X500Principal getIssuerX500Principal() {
        if (this.info == null) {
            return null;
        }
        try {
            return (X500Principal)this.info.get("issuer.x500principal");
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    @Override
    public Date getNotBefore() {
        if (this.info == null) {
            return null;
        }
        try {
            return (Date)this.info.get("validity.notBefore");
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    @Override
    public Date getNotAfter() {
        if (this.info == null) {
            return null;
        }
        try {
            return (Date)this.info.get("validity.notAfter");
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    @Override
    public byte[] getTBSCertificate() throws CertificateEncodingException {
        if (this.info != null) {
            return this.info.getEncodedInfo();
        }
        throw new CertificateEncodingException("Uninitialized certificate");
    }
    
    @Override
    public byte[] getSignature() {
        if (this.signature == null) {
            return null;
        }
        return this.signature.clone();
    }
    
    @Override
    public String getSigAlgName() {
        if (this.algId == null) {
            return null;
        }
        return this.algId.getName();
    }
    
    @Override
    public String getSigAlgOID() {
        if (this.algId == null) {
            return null;
        }
        return this.algId.getOID().toString();
    }
    
    @Override
    public byte[] getSigAlgParams() {
        if (this.algId == null) {
            return null;
        }
        try {
            return this.algId.getEncodedParams();
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    @Override
    public boolean[] getIssuerUniqueID() {
        if (this.info == null) {
            return null;
        }
        try {
            final UniqueIdentity uniqueIdentity = (UniqueIdentity)this.info.get("issuerID");
            if (uniqueIdentity == null) {
                return null;
            }
            return uniqueIdentity.getId();
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    @Override
    public boolean[] getSubjectUniqueID() {
        if (this.info == null) {
            return null;
        }
        try {
            final UniqueIdentity uniqueIdentity = (UniqueIdentity)this.info.get("subjectID");
            if (uniqueIdentity == null) {
                return null;
            }
            return uniqueIdentity.getId();
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public KeyIdentifier getAuthKeyId() {
        final AuthorityKeyIdentifierExtension authorityKeyIdentifierExtension = this.getAuthorityKeyIdentifierExtension();
        if (authorityKeyIdentifierExtension != null) {
            try {
                return (KeyIdentifier)authorityKeyIdentifierExtension.get("key_id");
            }
            catch (final IOException ex) {}
        }
        return null;
    }
    
    public KeyIdentifier getSubjectKeyId() {
        final SubjectKeyIdentifierExtension subjectKeyIdentifierExtension = this.getSubjectKeyIdentifierExtension();
        if (subjectKeyIdentifierExtension != null) {
            try {
                return subjectKeyIdentifierExtension.get("key_id");
            }
            catch (final IOException ex) {}
        }
        return null;
    }
    
    public AuthorityKeyIdentifierExtension getAuthorityKeyIdentifierExtension() {
        return (AuthorityKeyIdentifierExtension)this.getExtension(PKIXExtensions.AuthorityKey_Id);
    }
    
    public BasicConstraintsExtension getBasicConstraintsExtension() {
        return (BasicConstraintsExtension)this.getExtension(PKIXExtensions.BasicConstraints_Id);
    }
    
    public CertificatePoliciesExtension getCertificatePoliciesExtension() {
        return (CertificatePoliciesExtension)this.getExtension(PKIXExtensions.CertificatePolicies_Id);
    }
    
    public ExtendedKeyUsageExtension getExtendedKeyUsageExtension() {
        return (ExtendedKeyUsageExtension)this.getExtension(PKIXExtensions.ExtendedKeyUsage_Id);
    }
    
    public IssuerAlternativeNameExtension getIssuerAlternativeNameExtension() {
        return (IssuerAlternativeNameExtension)this.getExtension(PKIXExtensions.IssuerAlternativeName_Id);
    }
    
    public NameConstraintsExtension getNameConstraintsExtension() {
        return (NameConstraintsExtension)this.getExtension(PKIXExtensions.NameConstraints_Id);
    }
    
    public PolicyConstraintsExtension getPolicyConstraintsExtension() {
        return (PolicyConstraintsExtension)this.getExtension(PKIXExtensions.PolicyConstraints_Id);
    }
    
    public PolicyMappingsExtension getPolicyMappingsExtension() {
        return (PolicyMappingsExtension)this.getExtension(PKIXExtensions.PolicyMappings_Id);
    }
    
    public PrivateKeyUsageExtension getPrivateKeyUsageExtension() {
        return (PrivateKeyUsageExtension)this.getExtension(PKIXExtensions.PrivateKeyUsage_Id);
    }
    
    public SubjectAlternativeNameExtension getSubjectAlternativeNameExtension() {
        return (SubjectAlternativeNameExtension)this.getExtension(PKIXExtensions.SubjectAlternativeName_Id);
    }
    
    public SubjectKeyIdentifierExtension getSubjectKeyIdentifierExtension() {
        return (SubjectKeyIdentifierExtension)this.getExtension(PKIXExtensions.SubjectKey_Id);
    }
    
    public CRLDistributionPointsExtension getCRLDistributionPointsExtension() {
        return (CRLDistributionPointsExtension)this.getExtension(PKIXExtensions.CRLDistributionPoints_Id);
    }
    
    @Override
    public boolean hasUnsupportedCriticalExtension() {
        if (this.info == null) {
            return false;
        }
        try {
            final CertificateExtensions certificateExtensions = (CertificateExtensions)this.info.get("extensions");
            return certificateExtensions != null && certificateExtensions.hasUnsupportedCriticalExtension();
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    @Override
    public Set<String> getCriticalExtensionOIDs() {
        if (this.info == null) {
            return null;
        }
        try {
            final CertificateExtensions certificateExtensions = (CertificateExtensions)this.info.get("extensions");
            if (certificateExtensions == null) {
                return null;
            }
            final TreeSet set = new TreeSet();
            for (final Extension extension : certificateExtensions.getAllExtensions()) {
                if (extension.isCritical()) {
                    set.add(extension.getExtensionId().toString());
                }
            }
            return set;
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    @Override
    public Set<String> getNonCriticalExtensionOIDs() {
        if (this.info == null) {
            return null;
        }
        try {
            final CertificateExtensions certificateExtensions = (CertificateExtensions)this.info.get("extensions");
            if (certificateExtensions == null) {
                return null;
            }
            final TreeSet set = new TreeSet();
            for (final Extension extension : certificateExtensions.getAllExtensions()) {
                if (!extension.isCritical()) {
                    set.add(extension.getExtensionId().toString());
                }
            }
            set.addAll(certificateExtensions.getUnparseableExtensions().keySet());
            return set;
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public Extension getExtension(final ObjectIdentifier objectIdentifier) {
        if (this.info == null) {
            return null;
        }
        try {
            CertificateExtensions certificateExtensions;
            try {
                certificateExtensions = (CertificateExtensions)this.info.get("extensions");
            }
            catch (final CertificateException ex) {
                return null;
            }
            if (certificateExtensions == null) {
                return null;
            }
            final Extension extension = certificateExtensions.getExtension(objectIdentifier.toString());
            if (extension != null) {
                return extension;
            }
            for (final Extension extension2 : certificateExtensions.getAllExtensions()) {
                if (extension2.getExtensionId().equals((Object)objectIdentifier)) {
                    return extension2;
                }
            }
            return null;
        }
        catch (final IOException ex2) {
            return null;
        }
    }
    
    public Extension getUnparseableExtension(final ObjectIdentifier objectIdentifier) {
        if (this.info == null) {
            return null;
        }
        try {
            CertificateExtensions certificateExtensions;
            try {
                certificateExtensions = (CertificateExtensions)this.info.get("extensions");
            }
            catch (final CertificateException ex) {
                return null;
            }
            if (certificateExtensions == null) {
                return null;
            }
            return certificateExtensions.getUnparseableExtensions().get(objectIdentifier.toString());
        }
        catch (final IOException ex2) {
            return null;
        }
    }
    
    @Override
    public byte[] getExtensionValue(final String s) {
        try {
            final ObjectIdentifier objectIdentifier = new ObjectIdentifier(s);
            final String name = OIDMap.getName(objectIdentifier);
            Extension extension = null;
            final CertificateExtensions certificateExtensions = (CertificateExtensions)this.info.get("extensions");
            if (name == null) {
                if (certificateExtensions == null) {
                    return null;
                }
                for (final Extension extension2 : certificateExtensions.getAllExtensions()) {
                    if (extension2.getExtensionId().equals((Object)objectIdentifier)) {
                        extension = extension2;
                        break;
                    }
                }
            }
            else {
                try {
                    extension = (Extension)this.get(name);
                }
                catch (final CertificateException ex) {}
            }
            if (extension == null) {
                if (certificateExtensions != null) {
                    extension = certificateExtensions.getUnparseableExtensions().get(s);
                }
                if (extension == null) {
                    return null;
                }
            }
            final byte[] extensionValue = extension.getExtensionValue();
            if (extensionValue == null) {
                return null;
            }
            final DerOutputStream derOutputStream = new DerOutputStream();
            derOutputStream.putOctetString(extensionValue);
            return derOutputStream.toByteArray();
        }
        catch (final Exception ex2) {
            return null;
        }
    }
    
    @Override
    public boolean[] getKeyUsage() {
        try {
            final String name = OIDMap.getName(PKIXExtensions.KeyUsage_Id);
            if (name == null) {
                return null;
            }
            final KeyUsageExtension keyUsageExtension = (KeyUsageExtension)this.get(name);
            if (keyUsageExtension == null) {
                return null;
            }
            boolean[] bits = keyUsageExtension.getBits();
            if (bits.length < 9) {
                final boolean[] array = new boolean[9];
                System.arraycopy(bits, 0, array, 0, bits.length);
                bits = array;
            }
            return bits;
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    @Override
    public synchronized List<String> getExtendedKeyUsage() throws CertificateParsingException {
        if (this.readOnly && this.extKeyUsage != null) {
            return this.extKeyUsage;
        }
        final ExtendedKeyUsageExtension extendedKeyUsageExtension = this.getExtendedKeyUsageExtension();
        if (extendedKeyUsageExtension == null) {
            return null;
        }
        return this.extKeyUsage = (List<String>)Collections.unmodifiableList((List<?>)extendedKeyUsageExtension.getExtendedKeyUsage());
    }
    
    public static List<String> getExtendedKeyUsage(final X509Certificate x509Certificate) throws CertificateParsingException {
        try {
            final byte[] extensionValue = x509Certificate.getExtensionValue("2.5.29.37");
            if (extensionValue == null) {
                return null;
            }
            return (List<String>)Collections.unmodifiableList((List<?>)new ExtendedKeyUsageExtension(Boolean.FALSE, new DerValue(extensionValue).getOctetString()).getExtendedKeyUsage());
        }
        catch (final IOException ex) {
            throw new CertificateParsingException(ex);
        }
    }
    
    @Override
    public int getBasicConstraints() {
        try {
            final String name = OIDMap.getName(PKIXExtensions.BasicConstraints_Id);
            if (name == null) {
                return -1;
            }
            final BasicConstraintsExtension basicConstraintsExtension = (BasicConstraintsExtension)this.get(name);
            if (basicConstraintsExtension == null) {
                return -1;
            }
            if (basicConstraintsExtension.get("is_ca")) {
                return (int)basicConstraintsExtension.get("path_len");
            }
            return -1;
        }
        catch (final Exception ex) {
            return -1;
        }
    }
    
    private static Collection<List<?>> makeAltNames(final GeneralNames generalNames) {
        if (generalNames.isEmpty()) {
            return (Collection<List<?>>)Collections.emptySet();
        }
        final ArrayList list = new ArrayList();
        final Iterator<GeneralName> iterator = generalNames.names().iterator();
        while (iterator.hasNext()) {
            final GeneralNameInterface name = iterator.next().getName();
            final ArrayList list2 = new ArrayList(2);
            list2.add(name.getType());
            switch (name.getType()) {
                case 1: {
                    list2.add(((RFC822Name)name).getName());
                    break;
                }
                case 2: {
                    list2.add(((DNSName)name).getName());
                    break;
                }
                case 4: {
                    list2.add(((X500Name)name).getRFC2253Name());
                    break;
                }
                case 6: {
                    list2.add(((URIName)name).getName());
                    break;
                }
                case 7: {
                    try {
                        list2.add(((IPAddressName)name).getName());
                        break;
                    }
                    catch (final IOException ex) {
                        throw new RuntimeException("IPAddress cannot be parsed", ex);
                    }
                }
                case 8: {
                    list2.add(((OIDName)name).getOID().toString());
                    break;
                }
                default: {
                    final DerOutputStream derOutputStream = new DerOutputStream();
                    try {
                        name.encode(derOutputStream);
                    }
                    catch (final IOException ex2) {
                        throw new RuntimeException("name cannot be encoded", ex2);
                    }
                    list2.add(derOutputStream.toByteArray());
                    break;
                }
            }
            list.add(Collections.unmodifiableList((List<?>)list2));
        }
        return (Collection<List<?>>)Collections.unmodifiableCollection((Collection<?>)list);
    }
    
    private static Collection<List<?>> cloneAltNames(final Collection<List<?>> collection) {
        boolean b = false;
        final Iterator<List<?>> iterator = collection.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().get(1) instanceof byte[]) {
                b = true;
            }
        }
        if (b) {
            final ArrayList list = new ArrayList();
            for (final List list2 : collection) {
                final Object value = list2.get(1);
                if (value instanceof byte[]) {
                    final ArrayList list3 = new ArrayList(list2);
                    list3.set(1, (Object)((byte[])value).clone());
                    list.add(Collections.unmodifiableList((List<?>)list3));
                }
                else {
                    list.add(list2);
                }
            }
            return (Collection<List<?>>)Collections.unmodifiableCollection((Collection<?>)list);
        }
        return collection;
    }
    
    @Override
    public synchronized Collection<List<?>> getSubjectAlternativeNames() throws CertificateParsingException {
        if (this.readOnly && this.subjectAlternativeNames != null) {
            return cloneAltNames(this.subjectAlternativeNames);
        }
        final SubjectAlternativeNameExtension subjectAlternativeNameExtension = this.getSubjectAlternativeNameExtension();
        if (subjectAlternativeNameExtension == null) {
            return null;
        }
        GeneralNames value;
        try {
            value = subjectAlternativeNameExtension.get("subject_name");
        }
        catch (final IOException ex) {
            return (Collection<List<?>>)Collections.emptySet();
        }
        return this.subjectAlternativeNames = makeAltNames(value);
    }
    
    public static Collection<List<?>> getSubjectAlternativeNames(final X509Certificate x509Certificate) throws CertificateParsingException {
        try {
            final byte[] extensionValue = x509Certificate.getExtensionValue("2.5.29.17");
            if (extensionValue == null) {
                return null;
            }
            final SubjectAlternativeNameExtension subjectAlternativeNameExtension = new SubjectAlternativeNameExtension(Boolean.FALSE, new DerValue(extensionValue).getOctetString());
            GeneralNames value;
            try {
                value = subjectAlternativeNameExtension.get("subject_name");
            }
            catch (final IOException ex) {
                return (Collection<List<?>>)Collections.emptySet();
            }
            return makeAltNames(value);
        }
        catch (final IOException ex2) {
            throw new CertificateParsingException(ex2);
        }
    }
    
    @Override
    public synchronized Collection<List<?>> getIssuerAlternativeNames() throws CertificateParsingException {
        if (this.readOnly && this.issuerAlternativeNames != null) {
            return cloneAltNames(this.issuerAlternativeNames);
        }
        final IssuerAlternativeNameExtension issuerAlternativeNameExtension = this.getIssuerAlternativeNameExtension();
        if (issuerAlternativeNameExtension == null) {
            return null;
        }
        GeneralNames value;
        try {
            value = issuerAlternativeNameExtension.get("issuer_name");
        }
        catch (final IOException ex) {
            return (Collection<List<?>>)Collections.emptySet();
        }
        return this.issuerAlternativeNames = makeAltNames(value);
    }
    
    public static Collection<List<?>> getIssuerAlternativeNames(final X509Certificate x509Certificate) throws CertificateParsingException {
        try {
            final byte[] extensionValue = x509Certificate.getExtensionValue("2.5.29.18");
            if (extensionValue == null) {
                return null;
            }
            final IssuerAlternativeNameExtension issuerAlternativeNameExtension = new IssuerAlternativeNameExtension(Boolean.FALSE, new DerValue(extensionValue).getOctetString());
            GeneralNames value;
            try {
                value = issuerAlternativeNameExtension.get("issuer_name");
            }
            catch (final IOException ex) {
                return (Collection<List<?>>)Collections.emptySet();
            }
            return makeAltNames(value);
        }
        catch (final IOException ex2) {
            throw new CertificateParsingException(ex2);
        }
    }
    
    public AuthorityInfoAccessExtension getAuthorityInfoAccessExtension() {
        return (AuthorityInfoAccessExtension)this.getExtension(PKIXExtensions.AuthInfoAccess_Id);
    }
    
    private void parse(final DerValue derValue) throws CertificateException, IOException {
        if (this.readOnly) {
            throw new CertificateParsingException("cannot over-write existing certificate");
        }
        if (derValue.data == null || derValue.tag != 48) {
            throw new CertificateParsingException("invalid DER-encoded certificate data");
        }
        this.signedCert = derValue.toByteArray();
        final DerValue[] array = { derValue.data.getDerValue(), derValue.data.getDerValue(), derValue.data.getDerValue() };
        if (derValue.data.available() != 0) {
            throw new CertificateParsingException("signed overrun, bytes = " + derValue.data.available());
        }
        if (array[0].tag != 48) {
            throw new CertificateParsingException("signed fields invalid");
        }
        this.algId = AlgorithmId.parse(array[1]);
        this.signature = array[2].getBitString();
        if (array[1].data.available() != 0) {
            throw new CertificateParsingException("algid field overrun");
        }
        if (array[2].data.available() != 0) {
            throw new CertificateParsingException("signed fields overrun");
        }
        this.info = new X509CertInfo(array[0]);
        if (!this.algId.equals((AlgorithmId)this.info.get("algorithmID.algorithm"))) {
            throw new CertificateException("Signature algorithm mismatch");
        }
        this.readOnly = true;
    }
    
    private static X500Principal getX500Principal(final X509Certificate x509Certificate, final boolean b) throws Exception {
        final DerInputStream data = new DerInputStream(x509Certificate.getEncoded()).getSequence(3)[0].data;
        if (data.getDerValue().isContextSpecific((byte)0)) {
            data.getDerValue();
        }
        data.getDerValue();
        DerValue derValue = data.getDerValue();
        if (!b) {
            data.getDerValue();
            derValue = data.getDerValue();
        }
        return new X500Principal(derValue.toByteArray());
    }
    
    public static X500Principal getSubjectX500Principal(final X509Certificate x509Certificate) {
        try {
            return getX500Principal(x509Certificate, false);
        }
        catch (final Exception ex) {
            throw new RuntimeException("Could not parse subject", ex);
        }
    }
    
    public static X500Principal getIssuerX500Principal(final X509Certificate x509Certificate) {
        try {
            return getX500Principal(x509Certificate, true);
        }
        catch (final Exception ex) {
            throw new RuntimeException("Could not parse issuer", ex);
        }
    }
    
    public static byte[] getEncodedInternal(final Certificate certificate) throws CertificateEncodingException {
        if (certificate instanceof X509CertImpl) {
            return ((X509CertImpl)certificate).getEncodedInternal();
        }
        return certificate.getEncoded();
    }
    
    public static X509CertImpl toImpl(final X509Certificate x509Certificate) throws CertificateException {
        if (x509Certificate instanceof X509CertImpl) {
            return (X509CertImpl)x509Certificate;
        }
        return X509Factory.intern(x509Certificate);
    }
    
    public static boolean isSelfIssued(final X509Certificate x509Certificate) {
        return x509Certificate.getSubjectX500Principal().equals(x509Certificate.getIssuerX500Principal());
    }
    
    public static boolean isSelfSigned(final X509Certificate x509Certificate, final String s) {
        if (isSelfIssued(x509Certificate)) {
            try {
                if (s == null) {
                    x509Certificate.verify(x509Certificate.getPublicKey());
                }
                else {
                    x509Certificate.verify(x509Certificate.getPublicKey(), s);
                }
                return true;
            }
            catch (final Exception ex) {}
        }
        return false;
    }
    
    public String getFingerprint(final String s) {
        return this.fingerprints.computeIfAbsent(s, s2 -> getFingerprint(s2, this));
    }
    
    public static String getFingerprint(final String s, final X509Certificate x509Certificate) {
        String string = "";
        try {
            final byte[] digest = MessageDigest.getInstance(s).digest(x509Certificate.getEncoded());
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < digest.length; ++i) {
                byte2hex(digest[i], sb);
            }
            string = sb.toString();
        }
        catch (final NoSuchAlgorithmException | CertificateEncodingException ex) {}
        return string;
    }
    
    private static void byte2hex(final byte b, final StringBuffer sb) {
        final char[] array = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        final int n = (b & 0xF0) >> 4;
        final int n2 = b & 0xF;
        sb.append(array[n]);
        sb.append(array[n2]);
    }
}
