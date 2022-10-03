package sun.security.x509;

import sun.security.provider.X509Factory;
import sun.security.util.DerInputStream;
import java.util.Enumeration;
import sun.security.util.ObjectIdentifier;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Set;
import java.math.BigInteger;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.security.cert.Certificate;
import sun.misc.HexDumpEncoder;
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
import java.util.Iterator;
import sun.security.util.DerOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.security.cert.CRLException;
import sun.security.util.DerValue;
import java.util.LinkedList;
import java.util.TreeMap;
import java.security.PublicKey;
import java.util.List;
import java.security.cert.X509CRLEntry;
import java.util.Map;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import sun.security.util.DerEncoder;
import java.security.cert.X509CRL;

public class X509CRLImpl extends X509CRL implements DerEncoder
{
    private byte[] signedCRL;
    private byte[] signature;
    private byte[] tbsCertList;
    private AlgorithmId sigAlgId;
    private int version;
    private AlgorithmId infoSigAlgId;
    private X500Name issuer;
    private X500Principal issuerPrincipal;
    private Date thisUpdate;
    private Date nextUpdate;
    private Map<X509IssuerSerial, X509CRLEntry> revokedMap;
    private List<X509CRLEntry> revokedList;
    private CRLExtensions extensions;
    private static final boolean isExplicit = true;
    private boolean readOnly;
    private PublicKey verifiedPublicKey;
    private String verifiedProvider;
    
    private X509CRLImpl() {
        this.signedCRL = null;
        this.signature = null;
        this.tbsCertList = null;
        this.sigAlgId = null;
        this.issuer = null;
        this.issuerPrincipal = null;
        this.thisUpdate = null;
        this.nextUpdate = null;
        this.revokedMap = new TreeMap<X509IssuerSerial, X509CRLEntry>();
        this.revokedList = new LinkedList<X509CRLEntry>();
        this.extensions = null;
        this.readOnly = false;
    }
    
    public X509CRLImpl(final byte[] array) throws CRLException {
        this.signedCRL = null;
        this.signature = null;
        this.tbsCertList = null;
        this.sigAlgId = null;
        this.issuer = null;
        this.issuerPrincipal = null;
        this.thisUpdate = null;
        this.nextUpdate = null;
        this.revokedMap = new TreeMap<X509IssuerSerial, X509CRLEntry>();
        this.revokedList = new LinkedList<X509CRLEntry>();
        this.extensions = null;
        this.readOnly = false;
        try {
            this.parse(new DerValue(array));
        }
        catch (final IOException ex) {
            this.signedCRL = null;
            throw new CRLException("Parsing error: " + ex.getMessage());
        }
    }
    
    public X509CRLImpl(final DerValue derValue) throws CRLException {
        this.signedCRL = null;
        this.signature = null;
        this.tbsCertList = null;
        this.sigAlgId = null;
        this.issuer = null;
        this.issuerPrincipal = null;
        this.thisUpdate = null;
        this.nextUpdate = null;
        this.revokedMap = new TreeMap<X509IssuerSerial, X509CRLEntry>();
        this.revokedList = new LinkedList<X509CRLEntry>();
        this.extensions = null;
        this.readOnly = false;
        try {
            this.parse(derValue);
        }
        catch (final IOException ex) {
            this.signedCRL = null;
            throw new CRLException("Parsing error: " + ex.getMessage());
        }
    }
    
    public X509CRLImpl(final InputStream inputStream) throws CRLException {
        this.signedCRL = null;
        this.signature = null;
        this.tbsCertList = null;
        this.sigAlgId = null;
        this.issuer = null;
        this.issuerPrincipal = null;
        this.thisUpdate = null;
        this.nextUpdate = null;
        this.revokedMap = new TreeMap<X509IssuerSerial, X509CRLEntry>();
        this.revokedList = new LinkedList<X509CRLEntry>();
        this.extensions = null;
        this.readOnly = false;
        try {
            this.parse(new DerValue(inputStream));
        }
        catch (final IOException ex) {
            this.signedCRL = null;
            throw new CRLException("Parsing error: " + ex.getMessage());
        }
    }
    
    public X509CRLImpl(final X500Name issuer, final Date thisUpdate, final Date nextUpdate) {
        this.signedCRL = null;
        this.signature = null;
        this.tbsCertList = null;
        this.sigAlgId = null;
        this.issuer = null;
        this.issuerPrincipal = null;
        this.thisUpdate = null;
        this.nextUpdate = null;
        this.revokedMap = new TreeMap<X509IssuerSerial, X509CRLEntry>();
        this.revokedList = new LinkedList<X509CRLEntry>();
        this.extensions = null;
        this.readOnly = false;
        this.issuer = issuer;
        this.thisUpdate = thisUpdate;
        this.nextUpdate = nextUpdate;
    }
    
    public X509CRLImpl(final X500Name issuer, final Date thisUpdate, final Date nextUpdate, final X509CRLEntry[] array) throws CRLException {
        this.signedCRL = null;
        this.signature = null;
        this.tbsCertList = null;
        this.sigAlgId = null;
        this.issuer = null;
        this.issuerPrincipal = null;
        this.thisUpdate = null;
        this.nextUpdate = null;
        this.revokedMap = new TreeMap<X509IssuerSerial, X509CRLEntry>();
        this.revokedList = new LinkedList<X509CRLEntry>();
        this.extensions = null;
        this.readOnly = false;
        this.issuer = issuer;
        this.thisUpdate = thisUpdate;
        this.nextUpdate = nextUpdate;
        if (array != null) {
            X500Principal x500Principal2;
            final X500Principal x500Principal = x500Principal2 = this.getIssuerX500Principal();
            for (int i = 0; i < array.length; ++i) {
                final X509CRLEntryImpl x509CRLEntryImpl = (X509CRLEntryImpl)array[i];
                try {
                    x500Principal2 = this.getCertIssuer(x509CRLEntryImpl, x500Principal2);
                }
                catch (final IOException ex) {
                    throw new CRLException(ex);
                }
                x509CRLEntryImpl.setCertificateIssuer(x500Principal, x500Principal2);
                this.revokedMap.put(new X509IssuerSerial(x500Principal2, x509CRLEntryImpl.getSerialNumber()), x509CRLEntryImpl);
                this.revokedList.add(x509CRLEntryImpl);
                if (x509CRLEntryImpl.hasExtensions()) {
                    this.version = 1;
                }
            }
        }
    }
    
    public X509CRLImpl(final X500Name x500Name, final Date date, final Date date2, final X509CRLEntry[] array, final CRLExtensions extensions) throws CRLException {
        this(x500Name, date, date2, array);
        if (extensions != null) {
            this.extensions = extensions;
            this.version = 1;
        }
    }
    
    public byte[] getEncodedInternal() throws CRLException {
        if (this.signedCRL == null) {
            throw new CRLException("Null CRL to encode");
        }
        return this.signedCRL;
    }
    
    @Override
    public byte[] getEncoded() throws CRLException {
        return this.getEncodedInternal().clone();
    }
    
    public void encodeInfo(final OutputStream outputStream) throws CRLException {
        try {
            final DerOutputStream derOutputStream = new DerOutputStream();
            final DerOutputStream derOutputStream2 = new DerOutputStream();
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            if (this.version != 0) {
                derOutputStream.putInteger(this.version);
            }
            this.infoSigAlgId.encode(derOutputStream);
            if (this.version == 0 && this.issuer.toString() == null) {
                throw new CRLException("Null Issuer DN not allowed in v1 CRL");
            }
            this.issuer.encode(derOutputStream);
            if (this.thisUpdate.getTime() < 2524608000000L) {
                derOutputStream.putUTCTime(this.thisUpdate);
            }
            else {
                derOutputStream.putGeneralizedTime(this.thisUpdate);
            }
            if (this.nextUpdate != null) {
                if (this.nextUpdate.getTime() < 2524608000000L) {
                    derOutputStream.putUTCTime(this.nextUpdate);
                }
                else {
                    derOutputStream.putGeneralizedTime(this.nextUpdate);
                }
            }
            if (!this.revokedList.isEmpty()) {
                final Iterator<X509CRLEntry> iterator = this.revokedList.iterator();
                while (iterator.hasNext()) {
                    ((X509CRLEntryImpl)iterator.next()).encode(derOutputStream2);
                }
                derOutputStream.write((byte)48, derOutputStream2);
            }
            if (this.extensions != null) {
                this.extensions.encode(derOutputStream, true);
            }
            derOutputStream3.write((byte)48, derOutputStream);
            outputStream.write(this.tbsCertList = derOutputStream3.toByteArray());
        }
        catch (final IOException ex) {
            throw new CRLException("Encoding error: " + ex.getMessage());
        }
    }
    
    @Override
    public void verify(final PublicKey publicKey) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        this.verify(publicKey, "");
    }
    
    @Override
    public synchronized void verify(final PublicKey verifiedPublicKey, String verifiedProvider) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        if (verifiedProvider == null) {
            verifiedProvider = "";
        }
        if (this.verifiedPublicKey != null && this.verifiedPublicKey.equals(verifiedPublicKey) && verifiedProvider.equals(this.verifiedProvider)) {
            return;
        }
        if (this.signedCRL == null) {
            throw new CRLException("Uninitialized CRL");
        }
        final String name = this.sigAlgId.getName();
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
            throw new CRLException(ex.getMessage(), ex.getCause());
        }
        catch (final InvalidAlgorithmParameterException ex2) {
            throw new CRLException(ex2);
        }
        if (this.tbsCertList == null) {
            throw new CRLException("Uninitialized CRL");
        }
        signature.update(this.tbsCertList, 0, this.tbsCertList.length);
        if (!signature.verify(this.signature)) {
            throw new SignatureException("Signature does not match.");
        }
        this.verifiedPublicKey = verifiedPublicKey;
        this.verifiedProvider = verifiedProvider;
    }
    
    @Override
    public synchronized void verify(final PublicKey verifiedPublicKey, final Provider provider) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        if (this.signedCRL == null) {
            throw new CRLException("Uninitialized CRL");
        }
        final String name = this.sigAlgId.getName();
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
            throw new CRLException(ex.getMessage(), ex.getCause());
        }
        catch (final InvalidAlgorithmParameterException ex2) {
            throw new CRLException(ex2);
        }
        if (this.tbsCertList == null) {
            throw new CRLException("Uninitialized CRL");
        }
        signature.update(this.tbsCertList, 0, this.tbsCertList.length);
        if (!signature.verify(this.signature)) {
            throw new SignatureException("Signature does not match.");
        }
        this.verifiedPublicKey = verifiedPublicKey;
    }
    
    public void sign(final PrivateKey privateKey, final String s) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        this.sign(privateKey, s, null);
    }
    
    public void sign(final PrivateKey privateKey, final String s, final String s2) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
        try {
            if (this.readOnly) {
                throw new CRLException("cannot over-write existing CRL");
            }
            Signature signature;
            if (s2 == null || s2.length() == 0) {
                signature = Signature.getInstance(s);
            }
            else {
                signature = Signature.getInstance(s, s2);
            }
            signature.initSign(privateKey);
            this.sigAlgId = AlgorithmId.get(signature.getAlgorithm());
            this.infoSigAlgId = this.sigAlgId;
            final DerOutputStream derOutputStream = new DerOutputStream();
            final DerOutputStream derOutputStream2 = new DerOutputStream();
            this.encodeInfo(derOutputStream2);
            this.sigAlgId.encode(derOutputStream2);
            signature.update(this.tbsCertList, 0, this.tbsCertList.length);
            derOutputStream2.putBitString(this.signature = signature.sign());
            derOutputStream.write((byte)48, derOutputStream2);
            this.signedCRL = derOutputStream.toByteArray();
            this.readOnly = true;
        }
        catch (final IOException ex) {
            throw new CRLException("Error while encoding data: " + ex.getMessage());
        }
    }
    
    @Override
    public String toString() {
        return this.toStringWithAlgName("" + this.sigAlgId);
    }
    
    public String toStringWithAlgName(final String s) {
        final StringBuffer sb = new StringBuffer();
        sb.append("X.509 CRL v" + (this.version + 1) + "\n");
        if (this.sigAlgId != null) {
            sb.append("Signature Algorithm: " + s.toString() + ", OID=" + this.sigAlgId.getOID().toString() + "\n");
        }
        if (this.issuer != null) {
            sb.append("Issuer: " + this.issuer.toString() + "\n");
        }
        if (this.thisUpdate != null) {
            sb.append("\nThis Update: " + this.thisUpdate.toString() + "\n");
        }
        if (this.nextUpdate != null) {
            sb.append("Next Update: " + this.nextUpdate.toString() + "\n");
        }
        if (this.revokedList.isEmpty()) {
            sb.append("\nNO certificates have been revoked\n");
        }
        else {
            sb.append("\nRevoked Certificates: " + this.revokedList.size());
            int n = 1;
            final Iterator<X509CRLEntry> iterator = this.revokedList.iterator();
            while (iterator.hasNext()) {
                sb.append("\n[" + n++ + "] " + iterator.next().toString());
            }
        }
        if (this.extensions != null) {
            final Object[] array = this.extensions.getAllExtensions().toArray();
            sb.append("\nCRL Extensions: " + array.length);
            for (int i = 0; i < array.length; ++i) {
                sb.append("\n[" + (i + 1) + "]: ");
                final Extension extension = (Extension)array[i];
                try {
                    if (OIDMap.getClass(extension.getExtensionId()) == null) {
                        sb.append(extension.toString());
                        final byte[] extensionValue = extension.getExtensionValue();
                        if (extensionValue != null) {
                            final DerOutputStream derOutputStream = new DerOutputStream();
                            derOutputStream.putOctetString(extensionValue);
                            sb.append("Extension unknown: DER encoded OCTET string =\n" + new HexDumpEncoder().encodeBuffer(derOutputStream.toByteArray()) + "\n");
                        }
                    }
                    else {
                        sb.append(extension.toString());
                    }
                }
                catch (final Exception ex) {
                    sb.append(", Error parsing this extension");
                }
            }
        }
        if (this.signature != null) {
            sb.append("\nSignature:\n" + new HexDumpEncoder().encodeBuffer(this.signature) + "\n");
        }
        else {
            sb.append("NOT signed yet\n");
        }
        return sb.toString();
    }
    
    @Override
    public boolean isRevoked(final Certificate certificate) {
        return !this.revokedMap.isEmpty() && certificate instanceof X509Certificate && this.revokedMap.containsKey(new X509IssuerSerial((X509Certificate)certificate));
    }
    
    @Override
    public int getVersion() {
        return this.version + 1;
    }
    
    @Override
    public Principal getIssuerDN() {
        return this.issuer;
    }
    
    @Override
    public X500Principal getIssuerX500Principal() {
        if (this.issuerPrincipal == null) {
            this.issuerPrincipal = this.issuer.asX500Principal();
        }
        return this.issuerPrincipal;
    }
    
    @Override
    public Date getThisUpdate() {
        return new Date(this.thisUpdate.getTime());
    }
    
    @Override
    public Date getNextUpdate() {
        if (this.nextUpdate == null) {
            return null;
        }
        return new Date(this.nextUpdate.getTime());
    }
    
    @Override
    public X509CRLEntry getRevokedCertificate(final BigInteger bigInteger) {
        if (this.revokedMap.isEmpty()) {
            return null;
        }
        return this.revokedMap.get(new X509IssuerSerial(this.getIssuerX500Principal(), bigInteger));
    }
    
    @Override
    public X509CRLEntry getRevokedCertificate(final X509Certificate x509Certificate) {
        if (this.revokedMap.isEmpty()) {
            return null;
        }
        return this.revokedMap.get(new X509IssuerSerial(x509Certificate));
    }
    
    @Override
    public Set<X509CRLEntry> getRevokedCertificates() {
        if (this.revokedList.isEmpty()) {
            return null;
        }
        return new TreeSet<X509CRLEntry>(this.revokedList);
    }
    
    @Override
    public byte[] getTBSCertList() throws CRLException {
        if (this.tbsCertList == null) {
            throw new CRLException("Uninitialized CRL");
        }
        return this.tbsCertList.clone();
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
        if (this.sigAlgId == null) {
            return null;
        }
        return this.sigAlgId.getName();
    }
    
    @Override
    public String getSigAlgOID() {
        if (this.sigAlgId == null) {
            return null;
        }
        return this.sigAlgId.getOID().toString();
    }
    
    @Override
    public byte[] getSigAlgParams() {
        if (this.sigAlgId == null) {
            return null;
        }
        try {
            return this.sigAlgId.getEncodedParams();
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    public AlgorithmId getSigAlgId() {
        return this.sigAlgId;
    }
    
    public KeyIdentifier getAuthKeyId() throws IOException {
        final AuthorityKeyIdentifierExtension authKeyIdExtension = this.getAuthKeyIdExtension();
        if (authKeyIdExtension != null) {
            return (KeyIdentifier)authKeyIdExtension.get("key_id");
        }
        return null;
    }
    
    public AuthorityKeyIdentifierExtension getAuthKeyIdExtension() throws IOException {
        return (AuthorityKeyIdentifierExtension)this.getExtension(PKIXExtensions.AuthorityKey_Id);
    }
    
    public CRLNumberExtension getCRLNumberExtension() throws IOException {
        return (CRLNumberExtension)this.getExtension(PKIXExtensions.CRLNumber_Id);
    }
    
    public BigInteger getCRLNumber() throws IOException {
        final CRLNumberExtension crlNumberExtension = this.getCRLNumberExtension();
        if (crlNumberExtension != null) {
            return crlNumberExtension.get("value");
        }
        return null;
    }
    
    public DeltaCRLIndicatorExtension getDeltaCRLIndicatorExtension() throws IOException {
        return (DeltaCRLIndicatorExtension)this.getExtension(PKIXExtensions.DeltaCRLIndicator_Id);
    }
    
    public BigInteger getBaseCRLNumber() throws IOException {
        final DeltaCRLIndicatorExtension deltaCRLIndicatorExtension = this.getDeltaCRLIndicatorExtension();
        if (deltaCRLIndicatorExtension != null) {
            return deltaCRLIndicatorExtension.get("value");
        }
        return null;
    }
    
    public IssuerAlternativeNameExtension getIssuerAltNameExtension() throws IOException {
        return (IssuerAlternativeNameExtension)this.getExtension(PKIXExtensions.IssuerAlternativeName_Id);
    }
    
    public IssuingDistributionPointExtension getIssuingDistributionPointExtension() throws IOException {
        return (IssuingDistributionPointExtension)this.getExtension(PKIXExtensions.IssuingDistributionPoint_Id);
    }
    
    @Override
    public boolean hasUnsupportedCriticalExtension() {
        return this.extensions != null && this.extensions.hasUnsupportedCriticalExtension();
    }
    
    @Override
    public Set<String> getCriticalExtensionOIDs() {
        if (this.extensions == null) {
            return null;
        }
        final TreeSet set = new TreeSet();
        for (final Extension extension : this.extensions.getAllExtensions()) {
            if (extension.isCritical()) {
                set.add(extension.getExtensionId().toString());
            }
        }
        return set;
    }
    
    @Override
    public Set<String> getNonCriticalExtensionOIDs() {
        if (this.extensions == null) {
            return null;
        }
        final TreeSet set = new TreeSet();
        for (final Extension extension : this.extensions.getAllExtensions()) {
            if (!extension.isCritical()) {
                set.add(extension.getExtensionId().toString());
            }
        }
        return set;
    }
    
    @Override
    public byte[] getExtensionValue(final String s) {
        if (this.extensions == null) {
            return null;
        }
        try {
            final String name = OIDMap.getName(new ObjectIdentifier(s));
            Extension value = null;
            if (name == null) {
                final ObjectIdentifier objectIdentifier = new ObjectIdentifier(s);
                final Enumeration<Extension> elements = this.extensions.getElements();
                while (elements.hasMoreElements()) {
                    final Extension extension = elements.nextElement();
                    if (extension.getExtensionId().equals((Object)objectIdentifier)) {
                        value = extension;
                        break;
                    }
                }
            }
            else {
                value = this.extensions.get(name);
            }
            if (value == null) {
                return null;
            }
            final byte[] extensionValue = value.getExtensionValue();
            if (extensionValue == null) {
                return null;
            }
            final DerOutputStream derOutputStream = new DerOutputStream();
            derOutputStream.putOctetString(extensionValue);
            return derOutputStream.toByteArray();
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public Object getExtension(final ObjectIdentifier objectIdentifier) {
        if (this.extensions == null) {
            return null;
        }
        return this.extensions.get(OIDMap.getName(objectIdentifier));
    }
    
    private void parse(final DerValue derValue) throws CRLException, IOException {
        if (this.readOnly) {
            throw new CRLException("cannot over-write existing CRL");
        }
        if (derValue.getData() == null || derValue.tag != 48) {
            throw new CRLException("Invalid DER-encoded CRL data");
        }
        this.signedCRL = derValue.toByteArray();
        final DerValue[] array = { derValue.data.getDerValue(), derValue.data.getDerValue(), derValue.data.getDerValue() };
        if (derValue.data.available() != 0) {
            throw new CRLException("signed overrun, bytes = " + derValue.data.available());
        }
        if (array[0].tag != 48) {
            throw new CRLException("signed CRL fields invalid");
        }
        this.sigAlgId = AlgorithmId.parse(array[1]);
        this.signature = array[2].getBitString();
        if (array[1].data.available() != 0) {
            throw new CRLException("AlgorithmId field overrun");
        }
        if (array[2].data.available() != 0) {
            throw new CRLException("Signature field overrun");
        }
        this.tbsCertList = array[0].toByteArray();
        final DerInputStream data = array[0].data;
        this.version = 0;
        if ((byte)data.peekByte() == 2) {
            this.version = data.getInteger();
            if (this.version != 1) {
                throw new CRLException("Invalid version");
            }
        }
        final AlgorithmId parse = AlgorithmId.parse(data.getDerValue());
        if (!parse.equals(this.sigAlgId)) {
            throw new CRLException("Signature algorithm mismatch");
        }
        this.infoSigAlgId = parse;
        this.issuer = new X500Name(data);
        if (this.issuer.isEmpty()) {
            throw new CRLException("Empty issuer DN not allowed in X509CRLs");
        }
        final byte b = (byte)data.peekByte();
        if (b == 23) {
            this.thisUpdate = data.getUTCTime();
        }
        else {
            if (b != 24) {
                throw new CRLException("Invalid encoding for thisUpdate (tag=" + b + ")");
            }
            this.thisUpdate = data.getGeneralizedTime();
        }
        if (data.available() == 0) {
            return;
        }
        final byte b2 = (byte)data.peekByte();
        if (b2 == 23) {
            this.nextUpdate = data.getUTCTime();
        }
        else if (b2 == 24) {
            this.nextUpdate = data.getGeneralizedTime();
        }
        if (data.available() == 0) {
            return;
        }
        final byte b3 = (byte)data.peekByte();
        if (b3 == 48 && (b3 & 0xC0) != 0x80) {
            final DerValue[] sequence = data.getSequence(4);
            X500Principal x500Principal2;
            final X500Principal x500Principal = x500Principal2 = this.getIssuerX500Principal();
            for (int i = 0; i < sequence.length; ++i) {
                final X509CRLEntryImpl x509CRLEntryImpl = new X509CRLEntryImpl(sequence[i]);
                x500Principal2 = this.getCertIssuer(x509CRLEntryImpl, x500Principal2);
                x509CRLEntryImpl.setCertificateIssuer(x500Principal, x500Principal2);
                this.revokedMap.put(new X509IssuerSerial(x500Principal2, x509CRLEntryImpl.getSerialNumber()), x509CRLEntryImpl);
                this.revokedList.add(x509CRLEntryImpl);
            }
        }
        if (data.available() == 0) {
            return;
        }
        final DerValue derValue2 = data.getDerValue();
        if (derValue2.isConstructed() && derValue2.isContextSpecific((byte)0)) {
            this.extensions = new CRLExtensions(derValue2.data);
        }
        this.readOnly = true;
    }
    
    public static X500Principal getIssuerX500Principal(final X509CRL x509CRL) {
        try {
            final DerInputStream data = new DerInputStream(x509CRL.getEncoded()).getSequence(3)[0].data;
            if ((byte)data.peekByte() == 2) {
                data.getDerValue();
            }
            data.getDerValue();
            return new X500Principal(data.getDerValue().toByteArray());
        }
        catch (final Exception ex) {
            throw new RuntimeException("Could not parse issuer", ex);
        }
    }
    
    public static byte[] getEncodedInternal(final X509CRL x509CRL) throws CRLException {
        if (x509CRL instanceof X509CRLImpl) {
            return ((X509CRLImpl)x509CRL).getEncodedInternal();
        }
        return x509CRL.getEncoded();
    }
    
    public static X509CRLImpl toImpl(final X509CRL x509CRL) throws CRLException {
        if (x509CRL instanceof X509CRLImpl) {
            return (X509CRLImpl)x509CRL;
        }
        return X509Factory.intern(x509CRL);
    }
    
    private X500Principal getCertIssuer(final X509CRLEntryImpl x509CRLEntryImpl, final X500Principal x500Principal) throws IOException {
        final CertificateIssuerExtension certificateIssuerExtension = x509CRLEntryImpl.getCertificateIssuerExtension();
        if (certificateIssuerExtension != null) {
            return ((X500Name)certificateIssuerExtension.get("issuer").get(0).getName()).asX500Principal();
        }
        return x500Principal;
    }
    
    @Override
    public void derEncode(final OutputStream outputStream) throws IOException {
        if (this.signedCRL == null) {
            throw new IOException("Null CRL to encode");
        }
        outputStream.write(this.signedCRL.clone());
    }
    
    private static final class X509IssuerSerial implements Comparable<X509IssuerSerial>
    {
        final X500Principal issuer;
        final BigInteger serial;
        volatile int hashcode;
        
        X509IssuerSerial(final X500Principal issuer, final BigInteger serial) {
            this.hashcode = 0;
            this.issuer = issuer;
            this.serial = serial;
        }
        
        X509IssuerSerial(final X509Certificate x509Certificate) {
            this(x509Certificate.getIssuerX500Principal(), x509Certificate.getSerialNumber());
        }
        
        X500Principal getIssuer() {
            return this.issuer;
        }
        
        BigInteger getSerial() {
            return this.serial;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof X509IssuerSerial)) {
                return false;
            }
            final X509IssuerSerial x509IssuerSerial = (X509IssuerSerial)o;
            return this.serial.equals(x509IssuerSerial.getSerial()) && this.issuer.equals(x509IssuerSerial.getIssuer());
        }
        
        @Override
        public int hashCode() {
            if (this.hashcode == 0) {
                this.hashcode = 37 * (37 * 17 + this.issuer.hashCode()) + this.serial.hashCode();
            }
            return this.hashcode;
        }
        
        @Override
        public int compareTo(final X509IssuerSerial x509IssuerSerial) {
            final int compareTo = this.issuer.toString().compareTo(x509IssuerSerial.issuer.toString());
            if (compareTo != 0) {
                return compareTo;
            }
            return this.serial.compareTo(x509IssuerSerial.serial);
        }
    }
}
