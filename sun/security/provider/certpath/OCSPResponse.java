package sun.security.provider.certpath;

import java.security.PublicKey;
import java.security.cert.TrustAnchor;
import java.util.Set;
import java.security.SignatureException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.Signature;
import javax.security.auth.x500.X500Principal;
import java.util.Iterator;
import java.security.cert.CertPath;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import sun.security.x509.KeyIdentifier;
import java.security.cert.CertPathValidatorException;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
import sun.security.x509.PKIXExtensions;
import java.util.HashMap;
import sun.security.util.DerInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.misc.HexDumpEncoder;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetIntegerAction;
import java.security.cert.Extension;
import java.util.Date;
import sun.security.x509.X509CertImpl;
import java.util.List;
import sun.security.x509.AlgorithmId;
import java.util.Map;
import java.security.cert.CRLReason;
import sun.security.util.ObjectIdentifier;
import sun.security.util.Debug;

public final class OCSPResponse
{
    private static final ResponseStatus[] rsvalues;
    private static final Debug debug;
    private static final boolean dump;
    private static final ObjectIdentifier OCSP_BASIC_RESPONSE_OID;
    private static final int CERT_STATUS_GOOD = 0;
    private static final int CERT_STATUS_REVOKED = 1;
    private static final int CERT_STATUS_UNKNOWN = 2;
    private static final int NAME_TAG = 1;
    private static final int KEY_TAG = 2;
    private static final String KP_OCSP_SIGNING_OID = "1.3.6.1.5.5.7.3.9";
    private static final int DEFAULT_MAX_CLOCK_SKEW = 900000;
    private static final int MAX_CLOCK_SKEW;
    private static final CRLReason[] values;
    private final ResponseStatus responseStatus;
    private final Map<CertId, SingleResponse> singleResponseMap;
    private final AlgorithmId sigAlgId;
    private final byte[] signature;
    private final byte[] tbsResponseData;
    private final byte[] responseNonce;
    private List<X509CertImpl> certs;
    private X509CertImpl signerCert;
    private final ResponderId respId;
    private Date producedAtDate;
    private final Map<String, Extension> responseExtensions;
    
    private static int initializeClockSkew() {
        final Integer n = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("com.sun.security.ocsp.clockSkew"));
        if (n == null || n < 0) {
            return 900000;
        }
        return n * 1000;
    }
    
    public OCSPResponse(final byte[] array) throws IOException {
        this.signerCert = null;
        this.producedAtDate = null;
        if (OCSPResponse.dump) {
            OCSPResponse.debug.println("OCSPResponse bytes...\n\n" + new HexDumpEncoder().encode(array) + "\n");
        }
        final DerValue derValue = new DerValue(array);
        if (derValue.tag != 48) {
            throw new IOException("Bad encoding in OCSP response: expected ASN.1 SEQUENCE tag.");
        }
        final DerInputStream data = derValue.getData();
        final int enumerated = data.getEnumerated();
        if (enumerated < 0 || enumerated >= OCSPResponse.rsvalues.length) {
            throw new IOException("Unknown OCSPResponse status: " + enumerated);
        }
        this.responseStatus = OCSPResponse.rsvalues[enumerated];
        if (OCSPResponse.debug != null) {
            OCSPResponse.debug.println("OCSP response status: " + this.responseStatus);
        }
        if (this.responseStatus != ResponseStatus.SUCCESSFUL) {
            this.singleResponseMap = Collections.emptyMap();
            this.certs = new ArrayList<X509CertImpl>();
            this.sigAlgId = null;
            this.signature = null;
            this.tbsResponseData = null;
            this.responseNonce = null;
            this.responseExtensions = Collections.emptyMap();
            this.respId = null;
            return;
        }
        final DerValue derValue2 = data.getDerValue();
        if (!derValue2.isContextSpecific((byte)0)) {
            throw new IOException("Bad encoding in responseBytes element of OCSP response: expected ASN.1 context specific tag 0.");
        }
        final DerValue derValue3 = derValue2.data.getDerValue();
        if (derValue3.tag != 48) {
            throw new IOException("Bad encoding in responseBytes element of OCSP response: expected ASN.1 SEQUENCE tag.");
        }
        final DerInputStream data2 = derValue3.data;
        final ObjectIdentifier oid = data2.getOID();
        if (!oid.equals((Object)OCSPResponse.OCSP_BASIC_RESPONSE_OID)) {
            if (OCSPResponse.debug != null) {
                OCSPResponse.debug.println("OCSP response type: " + oid);
            }
            throw new IOException("Unsupported OCSP response type: " + oid);
        }
        if (OCSPResponse.debug != null) {
            OCSPResponse.debug.println("OCSP response type: basic");
        }
        final DerValue[] sequence = new DerInputStream(data2.getOctetString()).getSequence(3);
        if (sequence.length < 3) {
            throw new IOException("Unexpected BasicOCSPResponse value");
        }
        final DerValue derValue4 = sequence[0];
        this.tbsResponseData = sequence[0].toByteArray();
        if (derValue4.tag != 48) {
            throw new IOException("Bad encoding in tbsResponseData element of OCSP response: expected ASN.1 SEQUENCE tag.");
        }
        final DerInputStream data3 = derValue4.data;
        DerValue derValue5 = data3.getDerValue();
        if (derValue5.isContextSpecific((byte)0) && derValue5.isConstructed() && derValue5.isContextSpecific()) {
            final DerValue derValue6 = derValue5.data.getDerValue();
            derValue6.getInteger();
            if (derValue6.data.available() != 0) {
                throw new IOException("Bad encoding in version  element of OCSP response: bad format");
            }
            derValue5 = data3.getDerValue();
        }
        this.respId = new ResponderId(derValue5.toByteArray());
        if (OCSPResponse.debug != null) {
            OCSPResponse.debug.println("Responder ID: " + this.respId);
        }
        this.producedAtDate = data3.getDerValue().getGeneralizedTime();
        if (OCSPResponse.debug != null) {
            OCSPResponse.debug.println("OCSP response produced at: " + this.producedAtDate);
        }
        final DerValue[] sequence2 = data3.getSequence(1);
        this.singleResponseMap = new HashMap<CertId, SingleResponse>(sequence2.length);
        if (OCSPResponse.debug != null) {
            OCSPResponse.debug.println("OCSP number of SingleResponses: " + sequence2.length);
        }
        final DerValue[] array2 = sequence2;
        for (int length = array2.length, i = 0; i < length; ++i) {
            final SingleResponse singleResponse = new SingleResponse(array2[i]);
            this.singleResponseMap.put(singleResponse.getCertId(), singleResponse);
        }
        Map<String, Extension> extensions = new HashMap<String, Extension>();
        if (data3.available() > 0) {
            final DerValue derValue7 = data3.getDerValue();
            if (derValue7.isContextSpecific((byte)1)) {
                extensions = parseExtensions(derValue7);
            }
        }
        this.responseExtensions = extensions;
        final sun.security.x509.Extension extension = extensions.get(PKIXExtensions.OCSPNonce_Id.toString());
        this.responseNonce = (byte[])((extension != null) ? extension.getExtensionValue() : null);
        if (OCSPResponse.debug != null && this.responseNonce != null) {
            OCSPResponse.debug.println("Response nonce: " + Arrays.toString(this.responseNonce));
        }
        this.sigAlgId = AlgorithmId.parse(sequence[1]);
        this.signature = sequence[2].getBitString();
        if (sequence.length > 3) {
            final DerValue derValue8 = sequence[3];
            if (!derValue8.isContextSpecific((byte)0)) {
                throw new IOException("Bad encoding in certs element of OCSP response: expected ASN.1 context specific tag 0.");
            }
            final DerValue[] sequence3 = derValue8.getData().getSequence(3);
            this.certs = new ArrayList<X509CertImpl>(sequence3.length);
            try {
                for (int j = 0; j < sequence3.length; ++j) {
                    final X509CertImpl x509CertImpl = new X509CertImpl(sequence3[j].toByteArray());
                    this.certs.add(x509CertImpl);
                    if (OCSPResponse.debug != null) {
                        OCSPResponse.debug.println("OCSP response cert #" + (j + 1) + ": " + x509CertImpl.getSubjectX500Principal());
                    }
                }
            }
            catch (final CertificateException ex) {
                throw new IOException("Bad encoding in X509 Certificate", ex);
            }
        }
        else {
            this.certs = new ArrayList<X509CertImpl>();
        }
    }
    
    void verify(final List<CertId> list, final IssuerInfo issuerInfo, final X509Certificate x509Certificate, final Date date, final byte[] array, final String s) throws CertPathValidatorException {
        switch (this.responseStatus) {
            case SUCCESSFUL: {
                for (final CertId certId : list) {
                    final SingleResponse singleResponse = this.getSingleResponse(certId);
                    if (singleResponse == null) {
                        if (OCSPResponse.debug != null) {
                            OCSPResponse.debug.println("No response found for CertId: " + certId);
                        }
                        throw new CertPathValidatorException("OCSP response does not include a response for a certificate supplied in the OCSP request");
                    }
                    if (OCSPResponse.debug == null) {
                        continue;
                    }
                    OCSPResponse.debug.println("Status of certificate (with serial number " + certId.getSerialNumber() + ") is: " + singleResponse.getCertStatus());
                }
                if (this.signerCert == null) {
                    try {
                        if (issuerInfo.getCertificate() != null) {
                            this.certs.add(X509CertImpl.toImpl(issuerInfo.getCertificate()));
                        }
                        if (x509Certificate != null) {
                            this.certs.add(X509CertImpl.toImpl(x509Certificate));
                        }
                    }
                    catch (final CertificateException ex) {
                        throw new CertPathValidatorException("Invalid issuer or trusted responder certificate", ex);
                    }
                    if (this.respId.getType() == ResponderId.Type.BY_NAME) {
                        final X500Principal responderName = this.respId.getResponderName();
                        for (final X509CertImpl signerCert : this.certs) {
                            if (signerCert.getSubjectX500Principal().equals(responderName)) {
                                this.signerCert = signerCert;
                                break;
                            }
                        }
                    }
                    else if (this.respId.getType() == ResponderId.Type.BY_KEY) {
                        final KeyIdentifier keyIdentifier = this.respId.getKeyIdentifier();
                        for (final X509CertImpl x509CertImpl : this.certs) {
                            KeyIdentifier subjectKeyId = x509CertImpl.getSubjectKeyId();
                            if (subjectKeyId != null && keyIdentifier.equals(subjectKeyId)) {
                                this.signerCert = x509CertImpl;
                                break;
                            }
                            try {
                                subjectKeyId = new KeyIdentifier(x509CertImpl.getPublicKey());
                            }
                            catch (final IOException ex2) {}
                            if (keyIdentifier.equals(subjectKeyId)) {
                                this.signerCert = x509CertImpl;
                                break;
                            }
                        }
                    }
                }
                if (this.signerCert != null) {
                    if (this.signerCert.getSubjectX500Principal().equals(issuerInfo.getName()) && this.signerCert.getPublicKey().equals(issuerInfo.getPublicKey())) {
                        if (OCSPResponse.debug != null) {
                            OCSPResponse.debug.println("OCSP response is signed by the target's Issuing CA");
                        }
                    }
                    else if (this.signerCert.equals(x509Certificate)) {
                        if (OCSPResponse.debug != null) {
                            OCSPResponse.debug.println("OCSP response is signed by a Trusted Responder");
                        }
                    }
                    else {
                        if (!this.signerCert.getIssuerX500Principal().equals(issuerInfo.getName())) {
                            throw new CertPathValidatorException("Responder's certificate is not authorized to sign OCSP responses");
                        }
                        try {
                            final List<String> extendedKeyUsage = this.signerCert.getExtendedKeyUsage();
                            if (extendedKeyUsage == null || !extendedKeyUsage.contains("1.3.6.1.5.5.7.3.9")) {
                                throw new CertPathValidatorException("Responder's certificate not valid for signing OCSP responses");
                            }
                        }
                        catch (final CertificateParsingException ex3) {
                            throw new CertPathValidatorException("Responder's certificate not valid for signing OCSP responses", ex3);
                        }
                        final AlgorithmChecker algorithmChecker = new AlgorithmChecker(issuerInfo.getAnchor(), date, s);
                        algorithmChecker.init(false);
                        algorithmChecker.check(this.signerCert, (Collection<String>)Collections.emptySet());
                        try {
                            if (date == null) {
                                this.signerCert.checkValidity();
                            }
                            else {
                                this.signerCert.checkValidity(date);
                            }
                        }
                        catch (final CertificateException ex4) {
                            throw new CertPathValidatorException("Responder's certificate not within the validity period", ex4);
                        }
                        if (this.signerCert.getExtension(PKIXExtensions.OCSPNoCheck_Id) != null && OCSPResponse.debug != null) {
                            OCSPResponse.debug.println("Responder's certificate includes the extension id-pkix-ocsp-nocheck.");
                        }
                        try {
                            this.signerCert.verify(issuerInfo.getPublicKey());
                            if (OCSPResponse.debug != null) {
                                OCSPResponse.debug.println("OCSP response is signed by an Authorized Responder");
                            }
                        }
                        catch (final GeneralSecurityException ex5) {
                            this.signerCert = null;
                        }
                    }
                }
                if (this.signerCert == null) {
                    throw new CertPathValidatorException("Unable to verify OCSP Response's signature");
                }
                AlgorithmChecker.check(this.signerCert.getPublicKey(), this.sigAlgId, s);
                if (!this.verifySignature(this.signerCert)) {
                    throw new CertPathValidatorException("Error verifying OCSP Response's signature");
                }
                if (array != null && this.responseNonce != null && !Arrays.equals(array, this.responseNonce)) {
                    throw new CertPathValidatorException("Nonces don't match");
                }
                final long n = (date == null) ? System.currentTimeMillis() : date.getTime();
                final Date date2 = new Date(n + OCSPResponse.MAX_CLOCK_SKEW);
                final Date date3 = new Date(n - OCSPResponse.MAX_CLOCK_SKEW);
                for (final SingleResponse singleResponse2 : this.singleResponseMap.values()) {
                    if (OCSPResponse.debug != null) {
                        String string = "";
                        if (singleResponse2.nextUpdate != null) {
                            string = " until " + singleResponse2.nextUpdate;
                        }
                        OCSPResponse.debug.println("OCSP response validity interval is from " + singleResponse2.thisUpdate + string);
                        OCSPResponse.debug.println("Checking validity of OCSP response on: " + new Date(n));
                    }
                    if (date2.before(singleResponse2.thisUpdate) || date3.after((singleResponse2.nextUpdate != null) ? singleResponse2.nextUpdate : singleResponse2.thisUpdate)) {
                        throw new CertPathValidatorException("Response is unreliable: its validity interval is out-of-date");
                    }
                }
                return;
            }
            case TRY_LATER:
            case INTERNAL_ERROR: {
                throw new CertPathValidatorException("OCSP response error: " + this.responseStatus, null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
            }
            default: {
                throw new CertPathValidatorException("OCSP response error: " + this.responseStatus);
            }
        }
    }
    
    public ResponseStatus getResponseStatus() {
        return this.responseStatus;
    }
    
    private boolean verifySignature(final X509Certificate x509Certificate) throws CertPathValidatorException {
        try {
            final Signature instance = Signature.getInstance(this.sigAlgId.getName());
            instance.initVerify(x509Certificate.getPublicKey());
            instance.update(this.tbsResponseData);
            if (instance.verify(this.signature)) {
                if (OCSPResponse.debug != null) {
                    OCSPResponse.debug.println("Verified signature of OCSP Response");
                }
                return true;
            }
            if (OCSPResponse.debug != null) {
                OCSPResponse.debug.println("Error verifying signature of OCSP Response");
            }
            return false;
        }
        catch (final InvalidKeyException | NoSuchAlgorithmException | SignatureException ex) {
            throw new CertPathValidatorException((Throwable)ex);
        }
    }
    
    public SingleResponse getSingleResponse(final CertId certId) {
        return this.singleResponseMap.get(certId);
    }
    
    public Set<CertId> getCertIds() {
        return Collections.unmodifiableSet((Set<? extends CertId>)this.singleResponseMap.keySet());
    }
    
    X509Certificate getSignerCertificate() {
        return this.signerCert;
    }
    
    public ResponderId getResponderId() {
        return this.respId;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("OCSP Response:\n");
        sb.append("Response Status: ").append(this.responseStatus).append("\n");
        sb.append("Responder ID: ").append(this.respId).append("\n");
        sb.append("Produced at: ").append(this.producedAtDate).append("\n");
        final int size = this.singleResponseMap.size();
        sb.append(size).append((size == 1) ? " response:\n" : " responses:\n");
        final Iterator<SingleResponse> iterator = this.singleResponseMap.values().iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next()).append("\n");
        }
        if (this.responseExtensions != null && this.responseExtensions.size() > 0) {
            final int size2 = this.responseExtensions.size();
            sb.append(size2).append((size2 == 1) ? " extension:\n" : " extensions:\n");
            final Iterator<String> iterator2 = this.responseExtensions.keySet().iterator();
            while (iterator2.hasNext()) {
                sb.append(this.responseExtensions.get(iterator2.next())).append("\n");
            }
        }
        return sb.toString();
    }
    
    private static Map<String, Extension> parseExtensions(final DerValue derValue) throws IOException {
        final DerValue[] sequence = derValue.data.getSequence(3);
        final HashMap hashMap = new HashMap(sequence.length);
        final DerValue[] array = sequence;
        for (int length = array.length, i = 0; i < length; ++i) {
            final sun.security.x509.Extension extension = new sun.security.x509.Extension(array[i]);
            if (OCSPResponse.debug != null) {
                OCSPResponse.debug.println("Extension: " + extension);
            }
            if (extension.isCritical()) {
                throw new IOException("Unsupported OCSP critical extension: " + extension.getExtensionId());
            }
            hashMap.put((Object)extension.getId(), (Object)extension);
        }
        return (Map<String, Extension>)hashMap;
    }
    
    static {
        rsvalues = ResponseStatus.values();
        debug = Debug.getInstance("certpath");
        dump = (OCSPResponse.debug != null && Debug.isOn("ocsp"));
        OCSP_BASIC_RESPONSE_OID = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 1, 1 });
        MAX_CLOCK_SKEW = initializeClockSkew();
        values = CRLReason.values();
    }
    
    public enum ResponseStatus
    {
        SUCCESSFUL, 
        MALFORMED_REQUEST, 
        INTERNAL_ERROR, 
        TRY_LATER, 
        UNUSED, 
        SIG_REQUIRED, 
        UNAUTHORIZED;
    }
    
    public static final class SingleResponse implements OCSP.RevocationStatus
    {
        private final CertId certId;
        private final CertStatus certStatus;
        private final Date thisUpdate;
        private final Date nextUpdate;
        private final Date revocationTime;
        private final CRLReason revocationReason;
        private final Map<String, Extension> singleExtensions;
        
        private SingleResponse(final DerValue derValue) throws IOException {
            if (derValue.tag != 48) {
                throw new IOException("Bad ASN.1 encoding in SingleResponse");
            }
            final DerInputStream data = derValue.data;
            this.certId = new CertId(data.getDerValue().data);
            final DerValue derValue2 = data.getDerValue();
            final short n = (byte)(derValue2.tag & 0x1F);
            if (n == 1) {
                this.certStatus = CertStatus.REVOKED;
                this.revocationTime = derValue2.data.getGeneralizedTime();
                if (derValue2.data.available() != 0) {
                    final DerValue derValue3 = derValue2.data.getDerValue();
                    if ((byte)(derValue3.tag & 0x1F) == 0) {
                        final int enumerated = derValue3.data.getEnumerated();
                        if (enumerated >= 0 && enumerated < OCSPResponse.values.length) {
                            this.revocationReason = OCSPResponse.values[enumerated];
                        }
                        else {
                            this.revocationReason = CRLReason.UNSPECIFIED;
                        }
                    }
                    else {
                        this.revocationReason = CRLReason.UNSPECIFIED;
                    }
                }
                else {
                    this.revocationReason = CRLReason.UNSPECIFIED;
                }
                if (OCSPResponse.debug != null) {
                    OCSPResponse.debug.println("Revocation time: " + this.revocationTime);
                    OCSPResponse.debug.println("Revocation reason: " + this.revocationReason);
                }
            }
            else {
                this.revocationTime = null;
                this.revocationReason = null;
                if (n == 0) {
                    this.certStatus = CertStatus.GOOD;
                }
                else {
                    if (n != 2) {
                        throw new IOException("Invalid certificate status");
                    }
                    this.certStatus = CertStatus.UNKNOWN;
                }
            }
            this.thisUpdate = data.getGeneralizedTime();
            if (OCSPResponse.debug != null) {
                OCSPResponse.debug.println("thisUpdate: " + this.thisUpdate);
            }
            Date generalizedTime = null;
            Map<String, Extension> access$500 = null;
            if (data.available() > 0) {
                DerValue derValue4 = data.getDerValue();
                if (derValue4.isContextSpecific((byte)0)) {
                    generalizedTime = derValue4.data.getGeneralizedTime();
                    if (OCSPResponse.debug != null) {
                        OCSPResponse.debug.println("nextUpdate: " + generalizedTime);
                    }
                    derValue4 = ((data.available() > 0) ? data.getDerValue() : null);
                }
                if (derValue4 != null) {
                    if (!derValue4.isContextSpecific((byte)1)) {
                        throw new IOException("Unsupported singleResponse item, tag = " + String.format("%02X", derValue4.tag));
                    }
                    access$500 = parseExtensions(derValue4);
                    if (data.available() > 0) {
                        throw new IOException(data.available() + " bytes of additional data in singleResponse");
                    }
                }
            }
            this.nextUpdate = generalizedTime;
            this.singleExtensions = (Map<String, Extension>)((access$500 != null) ? access$500 : Collections.emptyMap());
            if (OCSPResponse.debug != null) {
                final Iterator<Extension> iterator = this.singleExtensions.values().iterator();
                while (iterator.hasNext()) {
                    OCSPResponse.debug.println("singleExtension: " + iterator.next());
                }
            }
        }
        
        @Override
        public CertStatus getCertStatus() {
            return this.certStatus;
        }
        
        public CertId getCertId() {
            return this.certId;
        }
        
        public Date getThisUpdate() {
            return (this.thisUpdate != null) ? ((Date)this.thisUpdate.clone()) : null;
        }
        
        public Date getNextUpdate() {
            return (this.nextUpdate != null) ? ((Date)this.nextUpdate.clone()) : null;
        }
        
        @Override
        public Date getRevocationTime() {
            return (this.revocationTime != null) ? ((Date)this.revocationTime.clone()) : null;
        }
        
        @Override
        public CRLReason getRevocationReason() {
            return this.revocationReason;
        }
        
        @Override
        public Map<String, Extension> getSingleExtensions() {
            return Collections.unmodifiableMap((Map<? extends String, ? extends Extension>)this.singleExtensions);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("SingleResponse:\n");
            sb.append(this.certId);
            sb.append("\nCertStatus: ").append(this.certStatus).append("\n");
            if (this.certStatus == CertStatus.REVOKED) {
                sb.append("revocationTime is ");
                sb.append(this.revocationTime).append("\n");
                sb.append("revocationReason is ");
                sb.append(this.revocationReason).append("\n");
            }
            sb.append("thisUpdate is ").append(this.thisUpdate).append("\n");
            if (this.nextUpdate != null) {
                sb.append("nextUpdate is ").append(this.nextUpdate).append("\n");
            }
            for (final Extension extension : this.singleExtensions.values()) {
                sb.append("singleExtension: ");
                sb.append(extension.toString()).append("\n");
            }
            return sb.toString();
        }
    }
    
    static final class IssuerInfo
    {
        private final TrustAnchor anchor;
        private final X509Certificate certificate;
        private final X500Principal name;
        private final PublicKey pubKey;
        
        IssuerInfo(final TrustAnchor trustAnchor) {
            this(trustAnchor, (trustAnchor != null) ? trustAnchor.getTrustedCert() : null);
        }
        
        IssuerInfo(final X509Certificate x509Certificate) {
            this(null, x509Certificate);
        }
        
        IssuerInfo(final TrustAnchor anchor, final X509Certificate certificate) {
            if (anchor == null && certificate == null) {
                throw new NullPointerException("TrustAnchor and issuerCert cannot be null");
            }
            this.anchor = anchor;
            if (certificate != null) {
                this.name = certificate.getSubjectX500Principal();
                this.pubKey = certificate.getPublicKey();
                this.certificate = certificate;
            }
            else {
                this.name = anchor.getCA();
                this.pubKey = anchor.getCAPublicKey();
                this.certificate = anchor.getTrustedCert();
            }
        }
        
        X509Certificate getCertificate() {
            return this.certificate;
        }
        
        X500Principal getName() {
            return this.name;
        }
        
        PublicKey getPublicKey() {
            return this.pubKey;
        }
        
        TrustAnchor getAnchor() {
            return this.anchor;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Issuer Info:\n");
            sb.append("Name: ").append(this.name.toString()).append("\n");
            sb.append("Public Key:\n").append(this.pubKey.toString()).append("\n");
            return sb.toString();
        }
    }
}
