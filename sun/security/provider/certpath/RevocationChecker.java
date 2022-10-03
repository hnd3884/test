package sun.security.provider.certpath;

import sun.security.x509.AuthorityInfoAccessExtension;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.CertPathBuilder;
import sun.security.x509.AccessDescription;
import java.security.cert.PKIXBuilderParameters;
import sun.security.x509.CRLDistributionPointsExtension;
import sun.security.x509.DistributionPoint;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.GeneralName;
import sun.security.x509.X500Name;
import sun.security.x509.GeneralNames;
import java.security.cert.CertificateException;
import sun.security.x509.X509CertImpl;
import java.security.cert.X509CRLEntry;
import java.security.cert.CertificateRevokedException;
import sun.security.x509.PKIXExtensions;
import java.security.cert.CRLException;
import sun.security.x509.X509CRLEntryImpl;
import java.security.cert.CRLReason;
import java.util.Date;
import java.util.Arrays;
import java.security.cert.X509CRL;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.X509CRLSelector;
import java.util.HashSet;
import java.security.cert.CertPath;
import java.io.IOException;
import java.util.Collections;
import java.security.cert.CertStoreException;
import java.security.cert.CertSelector;
import java.security.cert.Certificate;
import java.math.BigInteger;
import javax.security.auth.x500.X500Principal;
import java.security.cert.X509CertSelector;
import java.security.AccessController;
import java.security.Security;
import java.security.PrivilegedAction;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Set;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CollectionCertStoreParameters;
import java.util.Collection;
import java.util.ArrayList;
import java.security.PublicKey;
import java.security.cert.CertPathValidatorException;
import java.util.LinkedList;
import java.security.cert.Extension;
import java.util.Map;
import java.security.cert.CertStore;
import java.util.List;
import java.security.cert.X509Certificate;
import java.net.URI;
import java.security.cert.TrustAnchor;
import sun.security.util.Debug;
import java.security.cert.PKIXRevocationChecker;

class RevocationChecker extends PKIXRevocationChecker
{
    private static final Debug debug;
    private TrustAnchor anchor;
    private PKIX.ValidatorParams params;
    private boolean onlyEE;
    private boolean softFail;
    private boolean crlDP;
    private URI responderURI;
    private X509Certificate responderCert;
    private List<CertStore> certStores;
    private Map<X509Certificate, byte[]> ocspResponses;
    private List<Extension> ocspExtensions;
    private final boolean legacy;
    private LinkedList<CertPathValidatorException> softFailExceptions;
    private OCSPResponse.IssuerInfo issuerInfo;
    private PublicKey prevPubKey;
    private boolean crlSignFlag;
    private int certIndex;
    private Mode mode;
    private static final long MAX_CLOCK_SKEW = 900000L;
    private static final String HEX_DIGITS = "0123456789ABCDEFabcdef";
    private static final boolean[] ALL_REASONS;
    private static final boolean[] CRL_SIGN_USAGE;
    
    RevocationChecker() {
        this.softFailExceptions = new LinkedList<CertPathValidatorException>();
        this.mode = Mode.PREFER_OCSP;
        this.legacy = false;
    }
    
    RevocationChecker(final TrustAnchor trustAnchor, final PKIX.ValidatorParams validatorParams) throws CertPathValidatorException {
        this.softFailExceptions = new LinkedList<CertPathValidatorException>();
        this.mode = Mode.PREFER_OCSP;
        this.legacy = true;
        this.init(trustAnchor, validatorParams);
    }
    
    void init(final TrustAnchor anchor, final PKIX.ValidatorParams params) throws CertPathValidatorException {
        final RevocationProperties revocationProperties = getRevocationProperties();
        final URI ocspResponder = this.getOcspResponder();
        this.responderURI = ((ocspResponder == null) ? toURI(revocationProperties.ocspUrl) : ocspResponder);
        final X509Certificate ocspResponderCert = this.getOcspResponderCert();
        this.responderCert = ((ocspResponderCert == null) ? getResponderCert(revocationProperties, params.trustAnchors(), params.certStores()) : ocspResponderCert);
        final Set<Option> options = this.getOptions();
        for (final Option option : options) {
            switch (option) {
                case ONLY_END_ENTITY:
                case PREFER_CRLS:
                case SOFT_FAIL:
                case NO_FALLBACK: {
                    continue;
                }
                default: {
                    throw new CertPathValidatorException("Unrecognized revocation parameter option: " + option);
                }
            }
        }
        this.softFail = options.contains(Option.SOFT_FAIL);
        if (this.legacy) {
            this.mode = (revocationProperties.ocspEnabled ? Mode.PREFER_OCSP : Mode.ONLY_CRLS);
            this.onlyEE = revocationProperties.onlyEE;
        }
        else {
            if (options.contains(Option.NO_FALLBACK)) {
                if (options.contains(Option.PREFER_CRLS)) {
                    this.mode = Mode.ONLY_CRLS;
                }
                else {
                    this.mode = Mode.ONLY_OCSP;
                }
            }
            else if (options.contains(Option.PREFER_CRLS)) {
                this.mode = Mode.PREFER_CRLS;
            }
            this.onlyEE = options.contains(Option.ONLY_END_ENTITY);
        }
        if (this.legacy) {
            this.crlDP = revocationProperties.crlDPEnabled;
        }
        else {
            this.crlDP = true;
        }
        this.ocspResponses = this.getOcspResponses();
        this.ocspExtensions = this.getOcspExtensions();
        this.anchor = anchor;
        this.params = params;
        this.certStores = new ArrayList<CertStore>(params.certStores());
        try {
            this.certStores.add(CertStore.getInstance("Collection", new CollectionCertStoreParameters(params.certificates())));
        }
        catch (final InvalidAlgorithmParameterException | NoSuchAlgorithmException ex) {
            if (RevocationChecker.debug != null) {
                RevocationChecker.debug.println("RevocationChecker: error creating Collection CertStore: " + ex);
            }
        }
    }
    
    private static URI toURI(final String s) throws CertPathValidatorException {
        try {
            if (s != null) {
                return new URI(s);
            }
            return null;
        }
        catch (final URISyntaxException ex) {
            throw new CertPathValidatorException("cannot parse ocsp.responderURL property", ex);
        }
    }
    
    private static RevocationProperties getRevocationProperties() {
        return AccessController.doPrivileged((PrivilegedAction<RevocationProperties>)new PrivilegedAction<RevocationProperties>() {
            @Override
            public RevocationProperties run() {
                final RevocationProperties revocationProperties = new RevocationProperties();
                final String property = Security.getProperty("com.sun.security.onlyCheckRevocationOfEECert");
                revocationProperties.onlyEE = (property != null && property.equalsIgnoreCase("true"));
                final String property2 = Security.getProperty("ocsp.enable");
                revocationProperties.ocspEnabled = (property2 != null && property2.equalsIgnoreCase("true"));
                revocationProperties.ocspUrl = Security.getProperty("ocsp.responderURL");
                revocationProperties.ocspSubject = Security.getProperty("ocsp.responderCertSubjectName");
                revocationProperties.ocspIssuer = Security.getProperty("ocsp.responderCertIssuerName");
                revocationProperties.ocspSerial = Security.getProperty("ocsp.responderCertSerialNumber");
                revocationProperties.crlDPEnabled = Boolean.getBoolean("com.sun.security.enableCRLDP");
                return revocationProperties;
            }
        });
    }
    
    private static X509Certificate getResponderCert(final RevocationProperties revocationProperties, final Set<TrustAnchor> set, final List<CertStore> list) throws CertPathValidatorException {
        if (revocationProperties.ocspSubject != null) {
            return getResponderCert(revocationProperties.ocspSubject, set, list);
        }
        if (revocationProperties.ocspIssuer != null && revocationProperties.ocspSerial != null) {
            return getResponderCert(revocationProperties.ocspIssuer, revocationProperties.ocspSerial, set, list);
        }
        if (revocationProperties.ocspIssuer != null || revocationProperties.ocspSerial != null) {
            throw new CertPathValidatorException("Must specify both ocsp.responderCertIssuerName and ocsp.responderCertSerialNumber properties");
        }
        return null;
    }
    
    private static X509Certificate getResponderCert(final String s, final Set<TrustAnchor> set, final List<CertStore> list) throws CertPathValidatorException {
        final X509CertSelector x509CertSelector = new X509CertSelector();
        try {
            x509CertSelector.setSubject(new X500Principal(s));
        }
        catch (final IllegalArgumentException ex) {
            throw new CertPathValidatorException("cannot parse ocsp.responderCertSubjectName property", ex);
        }
        return getResponderCert(x509CertSelector, set, list);
    }
    
    private static X509Certificate getResponderCert(final String s, final String s2, final Set<TrustAnchor> set, final List<CertStore> list) throws CertPathValidatorException {
        final X509CertSelector x509CertSelector = new X509CertSelector();
        try {
            x509CertSelector.setIssuer(new X500Principal(s));
        }
        catch (final IllegalArgumentException ex) {
            throw new CertPathValidatorException("cannot parse ocsp.responderCertIssuerName property", ex);
        }
        try {
            x509CertSelector.setSerialNumber(new BigInteger(stripOutSeparators(s2), 16));
        }
        catch (final NumberFormatException ex2) {
            throw new CertPathValidatorException("cannot parse ocsp.responderCertSerialNumber property", ex2);
        }
        return getResponderCert(x509CertSelector, set, list);
    }
    
    private static X509Certificate getResponderCert(final X509CertSelector x509CertSelector, final Set<TrustAnchor> set, final List<CertStore> list) throws CertPathValidatorException {
        final Iterator<TrustAnchor> iterator = set.iterator();
        while (iterator.hasNext()) {
            final X509Certificate trustedCert = iterator.next().getTrustedCert();
            if (trustedCert == null) {
                continue;
            }
            if (x509CertSelector.match(trustedCert)) {
                return trustedCert;
            }
        }
        for (final CertStore certStore : list) {
            try {
                final Collection<? extends Certificate> certificates = certStore.getCertificates(x509CertSelector);
                if (!certificates.isEmpty()) {
                    return certificates.iterator().next();
                }
                continue;
            }
            catch (final CertStoreException ex) {
                if (RevocationChecker.debug == null) {
                    continue;
                }
                RevocationChecker.debug.println("CertStore exception:" + ex);
            }
        }
        throw new CertPathValidatorException("Cannot find the responder's certificate (set using the OCSP security properties).");
    }
    
    @Override
    public void init(final boolean b) throws CertPathValidatorException {
        if (b) {
            throw new CertPathValidatorException("forward checking not supported");
        }
        if (this.anchor != null) {
            this.issuerInfo = new OCSPResponse.IssuerInfo(this.anchor);
            this.prevPubKey = this.issuerInfo.getPublicKey();
        }
        this.crlSignFlag = true;
        if (this.params != null && this.params.certPath() != null) {
            this.certIndex = this.params.certPath().getCertificates().size() - 1;
        }
        else {
            this.certIndex = -1;
        }
        this.softFailExceptions.clear();
    }
    
    @Override
    public boolean isForwardCheckingSupported() {
        return false;
    }
    
    @Override
    public Set<String> getSupportedExtensions() {
        return null;
    }
    
    @Override
    public List<CertPathValidatorException> getSoftFailExceptions() {
        return Collections.unmodifiableList((List<? extends CertPathValidatorException>)this.softFailExceptions);
    }
    
    @Override
    public void check(final Certificate certificate, final Collection<String> collection) throws CertPathValidatorException {
        this.check((X509Certificate)certificate, collection, this.prevPubKey, this.crlSignFlag);
    }
    
    private void check(final X509Certificate x509Certificate, final Collection<String> collection, final PublicKey publicKey, final boolean b) throws CertPathValidatorException {
        if (RevocationChecker.debug != null) {
            RevocationChecker.debug.println("RevocationChecker.check: checking cert\n  SN: " + Debug.toHexString(x509Certificate.getSerialNumber()) + "\n  Subject: " + x509Certificate.getSubjectX500Principal() + "\n  Issuer: " + x509Certificate.getIssuerX500Principal());
        }
        try {
            if (this.onlyEE && x509Certificate.getBasicConstraints() != -1) {
                if (RevocationChecker.debug != null) {
                    RevocationChecker.debug.println("Skipping revocation check; cert is not an end entity cert");
                }
                return;
            }
            switch (this.mode) {
                case PREFER_OCSP:
                case ONLY_OCSP: {
                    this.checkOCSP(x509Certificate, collection);
                    break;
                }
                case PREFER_CRLS:
                case ONLY_CRLS: {
                    this.checkCRLs(x509Certificate, collection, null, publicKey, b);
                    break;
                }
            }
        }
        catch (final CertPathValidatorException ex) {
            if (ex.getReason() == CertPathValidatorException.BasicReason.REVOKED) {
                throw ex;
            }
            final boolean softFailException = this.isSoftFailException(ex);
            if (softFailException) {
                if (this.mode == Mode.ONLY_OCSP || this.mode == Mode.ONLY_CRLS) {
                    return;
                }
            }
            else if (this.mode == Mode.ONLY_OCSP || this.mode == Mode.ONLY_CRLS) {
                throw ex;
            }
            final CertPathValidatorException ex2 = ex;
            if (RevocationChecker.debug != null) {
                RevocationChecker.debug.println("RevocationChecker.check() " + ex.getMessage());
                RevocationChecker.debug.println("RevocationChecker.check() preparing to failover");
            }
            try {
                switch (this.mode) {
                    case PREFER_OCSP: {
                        this.checkCRLs(x509Certificate, collection, null, publicKey, b);
                        break;
                    }
                    case PREFER_CRLS: {
                        this.checkOCSP(x509Certificate, collection);
                        break;
                    }
                }
            }
            catch (final CertPathValidatorException ex3) {
                if (RevocationChecker.debug != null) {
                    RevocationChecker.debug.println("RevocationChecker.check() failover failed");
                    RevocationChecker.debug.println("RevocationChecker.check() " + ex3.getMessage());
                }
                if (ex3.getReason() == CertPathValidatorException.BasicReason.REVOKED) {
                    throw ex3;
                }
                if (!this.isSoftFailException(ex3)) {
                    ex2.addSuppressed(ex3);
                    throw ex2;
                }
                if (!softFailException) {
                    throw ex2;
                }
            }
        }
        finally {
            this.updateState(x509Certificate);
        }
    }
    
    private boolean isSoftFailException(final CertPathValidatorException ex) {
        if (this.softFail && ex.getReason() == CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS) {
            this.softFailExceptions.addFirst(new CertPathValidatorException(ex.getMessage(), ex.getCause(), this.params.certPath(), this.certIndex, ex.getReason()));
            return true;
        }
        return false;
    }
    
    private void updateState(final X509Certificate x509Certificate) throws CertPathValidatorException {
        this.issuerInfo = new OCSPResponse.IssuerInfo(this.anchor, x509Certificate);
        PublicKey prevPubKey = x509Certificate.getPublicKey();
        if (PKIX.isDSAPublicKeyWithoutParams(prevPubKey)) {
            prevPubKey = BasicChecker.makeInheritedParamsKey(prevPubKey, this.prevPubKey);
        }
        this.prevPubKey = prevPubKey;
        this.crlSignFlag = certCanSignCrl(x509Certificate);
        if (this.certIndex > 0) {
            --this.certIndex;
        }
    }
    
    private void checkCRLs(final X509Certificate x509Certificate, final Collection<String> collection, final Set<X509Certificate> set, final PublicKey publicKey, final boolean b) throws CertPathValidatorException {
        this.checkCRLs(x509Certificate, publicKey, null, b, true, set, this.params.trustAnchors());
    }
    
    static boolean isCausedByNetworkIssue(final String s, final CertStoreException ex) {
        final Throwable cause = ex.getCause();
        boolean b = false;
        switch (s) {
            case "LDAP": {
                if (cause != null) {
                    final String name = cause.getClass().getName();
                    b = (name.equals("javax.naming.ServiceUnavailableException") || name.equals("javax.naming.CommunicationException"));
                    break;
                }
                b = false;
                break;
            }
            case "SSLServer": {
                b = (cause != null && cause instanceof IOException);
                break;
            }
            case "URI": {
                b = (cause != null && cause instanceof IOException);
                break;
            }
            default: {
                return false;
            }
        }
        return b;
    }
    
    private void checkCRLs(final X509Certificate certificateChecking, final PublicKey publicKey, final X509Certificate x509Certificate, final boolean b, final boolean b2, final Set<X509Certificate> set, final Set<TrustAnchor> set2) throws CertPathValidatorException {
        if (RevocationChecker.debug != null) {
            RevocationChecker.debug.println("RevocationChecker.checkCRLs() ---checking revocation status ...");
        }
        if (set != null && set.contains(certificateChecking)) {
            if (RevocationChecker.debug != null) {
                RevocationChecker.debug.println("RevocationChecker.checkCRLs() circular dependency");
            }
            throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
        }
        final HashSet set3 = new HashSet();
        final HashSet set4 = new HashSet();
        final X509CRLSelector x509CRLSelector = new X509CRLSelector();
        x509CRLSelector.setCertificateChecking(certificateChecking);
        CertPathHelper.setDateAndTime(x509CRLSelector, this.params.date(), 900000L);
        Object o = null;
        for (final CertStore certStore : this.certStores) {
            try {
                final Iterator<? extends CRL> iterator2 = certStore.getCRLs(x509CRLSelector).iterator();
                while (iterator2.hasNext()) {
                    set3.add(iterator2.next());
                }
            }
            catch (final CertStoreException ex) {
                if (RevocationChecker.debug != null) {
                    RevocationChecker.debug.println("RevocationChecker.checkCRLs() CertStoreException: " + ex.getMessage());
                }
                if (o != null || !isCausedByNetworkIssue(certStore.getType(), ex)) {
                    continue;
                }
                o = new CertPathValidatorException("Unable to determine revocation status due to network error", ex, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
            }
        }
        if (RevocationChecker.debug != null) {
            RevocationChecker.debug.println("RevocationChecker.checkCRLs() possible crls.size() = " + set3.size());
        }
        final boolean[] array = new boolean[9];
        if (!set3.isEmpty()) {
            set4.addAll(this.verifyPossibleCRLs(set3, certificateChecking, publicKey, b, array, set2));
        }
        if (RevocationChecker.debug != null) {
            RevocationChecker.debug.println("RevocationChecker.checkCRLs() approved crls.size() = " + set4.size());
        }
        if (!set4.isEmpty() && Arrays.equals(array, RevocationChecker.ALL_REASONS)) {
            this.checkApprovedCRLs(certificateChecking, set4);
        }
        else {
            try {
                if (this.crlDP) {
                    set4.addAll(DistributionPointFetcher.getCRLs(x509CRLSelector, b, publicKey, x509Certificate, this.params.sigProvider(), this.certStores, array, set2, null, this.params.variant()));
                }
            }
            catch (final CertStoreException ex2) {
                if (ex2 instanceof PKIX.CertStoreTypeException && isCausedByNetworkIssue(((PKIX.CertStoreTypeException)ex2).getType(), ex2)) {
                    throw new CertPathValidatorException("Unable to determine revocation status due to network error", ex2, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
                }
                throw new CertPathValidatorException(ex2);
            }
            if (!set4.isEmpty() && Arrays.equals(array, RevocationChecker.ALL_REASONS)) {
                this.checkApprovedCRLs(certificateChecking, set4);
            }
            else {
                if (b2) {
                    try {
                        this.verifyWithSeparateSigningKey(certificateChecking, publicKey, b, set);
                        return;
                    }
                    catch (final CertPathValidatorException ex3) {
                        if (o != null) {
                            throw o;
                        }
                        throw ex3;
                    }
                }
                if (o != null) {
                    throw o;
                }
                throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
            }
        }
    }
    
    private void checkApprovedCRLs(final X509Certificate x509Certificate, final Set<X509CRL> set) throws CertPathValidatorException {
        if (RevocationChecker.debug != null) {
            final BigInteger serialNumber = x509Certificate.getSerialNumber();
            RevocationChecker.debug.println("RevocationChecker.checkApprovedCRLs() starting the final sweep...");
            RevocationChecker.debug.println("RevocationChecker.checkApprovedCRLs() cert SN: " + serialNumber.toString());
        }
        final CRLReason unspecified = CRLReason.UNSPECIFIED;
        for (final X509CRL x509CRL : set) {
            final X509CRLEntry revokedCertificate = x509CRL.getRevokedCertificate(x509Certificate);
            if (revokedCertificate != null) {
                X509CRLEntryImpl impl;
                try {
                    impl = X509CRLEntryImpl.toImpl(revokedCertificate);
                }
                catch (final CRLException ex) {
                    throw new CertPathValidatorException(ex);
                }
                if (RevocationChecker.debug != null) {
                    RevocationChecker.debug.println("RevocationChecker.checkApprovedCRLs() CRL entry: " + impl.toString());
                }
                final Set<String> criticalExtensionOIDs = impl.getCriticalExtensionOIDs();
                if (criticalExtensionOIDs != null && !criticalExtensionOIDs.isEmpty()) {
                    criticalExtensionOIDs.remove(PKIXExtensions.ReasonCode_Id.toString());
                    criticalExtensionOIDs.remove(PKIXExtensions.CertificateIssuer_Id.toString());
                    if (!criticalExtensionOIDs.isEmpty()) {
                        throw new CertPathValidatorException("Unrecognized critical extension(s) in revoked CRL entry");
                    }
                }
                CRLReason crlReason = impl.getRevocationReason();
                if (crlReason == null) {
                    crlReason = CRLReason.UNSPECIFIED;
                }
                final Date revocationDate = impl.getRevocationDate();
                if (revocationDate.before(this.params.date())) {
                    final CertificateRevokedException ex2 = new CertificateRevokedException(revocationDate, crlReason, x509CRL.getIssuerX500Principal(), impl.getExtensions());
                    throw new CertPathValidatorException(ex2.getMessage(), ex2, null, -1, CertPathValidatorException.BasicReason.REVOKED);
                }
                continue;
            }
        }
    }
    
    private void checkOCSP(final X509Certificate x509Certificate, final Collection<String> collection) throws CertPathValidatorException {
        X509CertImpl impl;
        try {
            impl = X509CertImpl.toImpl(x509Certificate);
        }
        catch (final CertificateException ex) {
            throw new CertPathValidatorException(ex);
        }
        CertId certId;
        OCSPResponse check;
        try {
            certId = new CertId(this.issuerInfo.getName(), this.issuerInfo.getPublicKey(), impl.getSerialNumberObject());
            final byte[] array = this.ocspResponses.get(x509Certificate);
            if (array != null) {
                if (RevocationChecker.debug != null) {
                    RevocationChecker.debug.println("Found cached OCSP response");
                }
                check = new OCSPResponse(array);
                byte[] value = null;
                for (final Extension extension : this.ocspExtensions) {
                    if (extension.getId().equals("1.3.6.1.5.5.7.48.1.2")) {
                        value = extension.getValue();
                    }
                }
                check.verify(Collections.singletonList(certId), this.issuerInfo, this.responderCert, this.params.date(), value, this.params.variant());
            }
            else {
                final URI uri = (this.responderURI != null) ? this.responderURI : OCSP.getResponderURI(impl);
                if (uri == null) {
                    throw new CertPathValidatorException("Certificate does not specify OCSP responder", null, null, -1);
                }
                check = OCSP.check(Collections.singletonList(certId), uri, this.issuerInfo, this.responderCert, null, this.ocspExtensions, this.params.variant());
            }
        }
        catch (final IOException ex2) {
            throw new CertPathValidatorException("Unable to determine revocation status due to network error", ex2, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
        }
        final OCSPResponse.SingleResponse singleResponse = check.getSingleResponse(certId);
        final OCSP.RevocationStatus.CertStatus certStatus = singleResponse.getCertStatus();
        if (certStatus == OCSP.RevocationStatus.CertStatus.REVOKED) {
            final Date revocationTime = singleResponse.getRevocationTime();
            if (revocationTime.before(this.params.date())) {
                final CertificateRevokedException ex3 = new CertificateRevokedException(revocationTime, singleResponse.getRevocationReason(), check.getSignerCertificate().getSubjectX500Principal(), singleResponse.getSingleExtensions());
                throw new CertPathValidatorException(ex3.getMessage(), ex3, null, -1, CertPathValidatorException.BasicReason.REVOKED);
            }
        }
        else if (certStatus == OCSP.RevocationStatus.CertStatus.UNKNOWN) {
            throw new CertPathValidatorException("Certificate's revocation status is unknown", null, this.params.certPath(), -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
        }
    }
    
    private static String stripOutSeparators(final String s) {
        final char[] charArray = s.toCharArray();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < charArray.length; ++i) {
            if ("0123456789ABCDEFabcdef".indexOf(charArray[i]) != -1) {
                sb.append(charArray[i]);
            }
        }
        return sb.toString();
    }
    
    static boolean certCanSignCrl(final X509Certificate x509Certificate) {
        final boolean[] keyUsage = x509Certificate.getKeyUsage();
        return keyUsage != null && keyUsage[6];
    }
    
    private Collection<X509CRL> verifyPossibleCRLs(final Set<X509CRL> set, final X509Certificate x509Certificate, final PublicKey publicKey, final boolean b, final boolean[] array, final Set<TrustAnchor> set2) throws CertPathValidatorException {
        try {
            final X509CertImpl impl = X509CertImpl.toImpl(x509Certificate);
            if (RevocationChecker.debug != null) {
                RevocationChecker.debug.println("RevocationChecker.verifyPossibleCRLs: Checking CRLDPs for " + impl.getSubjectX500Principal());
            }
            final CRLDistributionPointsExtension crlDistributionPointsExtension = impl.getCRLDistributionPointsExtension();
            List<DistributionPoint> list;
            if (crlDistributionPointsExtension == null) {
                list = Collections.singletonList(new DistributionPoint(new GeneralNames().add(new GeneralName((GeneralNameInterface)impl.getIssuerDN())), null, null));
            }
            else {
                list = crlDistributionPointsExtension.get("points");
            }
            final HashSet set3 = new HashSet();
            for (final DistributionPoint distributionPoint : list) {
                for (final X509CRL x509CRL : set) {
                    if (DistributionPointFetcher.verifyCRL(impl, distributionPoint, x509CRL, array, b, publicKey, null, this.params.sigProvider(), set2, this.certStores, this.params.date(), this.params.variant())) {
                        set3.add(x509CRL);
                    }
                }
                if (Arrays.equals(array, RevocationChecker.ALL_REASONS)) {
                    break;
                }
            }
            return set3;
        }
        catch (final CertificateException | CRLException | IOException ex) {
            if (RevocationChecker.debug != null) {
                RevocationChecker.debug.println("Exception while verifying CRL: " + ((Throwable)ex).getMessage());
                ((Exception)ex).printStackTrace();
            }
            return (Collection<X509CRL>)Collections.emptySet();
        }
    }
    
    private void verifyWithSeparateSigningKey(final X509Certificate x509Certificate, final PublicKey publicKey, final boolean b, final Set<X509Certificate> set) throws CertPathValidatorException {
        final String s = "revocation status";
        if (RevocationChecker.debug != null) {
            RevocationChecker.debug.println("RevocationChecker.verifyWithSeparateSigningKey() ---checking " + s + "...");
        }
        if (set != null && set.contains(x509Certificate)) {
            if (RevocationChecker.debug != null) {
                RevocationChecker.debug.println("RevocationChecker.verifyWithSeparateSigningKey() circular dependency");
            }
            throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
        }
        if (!b) {
            this.buildToNewKey(x509Certificate, null, set);
        }
        else {
            this.buildToNewKey(x509Certificate, publicKey, set);
        }
    }
    
    private void buildToNewKey(final X509Certificate x509Certificate, final PublicKey publicKey, Set<X509Certificate> set) throws CertPathValidatorException {
        if (RevocationChecker.debug != null) {
            RevocationChecker.debug.println("RevocationChecker.buildToNewKey() starting work");
        }
        final HashSet set2 = new HashSet();
        if (publicKey != null) {
            set2.add(publicKey);
        }
        final RejectKeySelector rejectKeySelector = new RejectKeySelector(set2);
        rejectKeySelector.setSubject(x509Certificate.getIssuerX500Principal());
        rejectKeySelector.setKeyUsage(RevocationChecker.CRL_SIGN_USAGE);
        final Set<TrustAnchor> set3 = (this.anchor == null) ? this.params.trustAnchors() : Collections.singleton(this.anchor);
        PKIXBuilderParameters pkixBuilderParameters;
        try {
            pkixBuilderParameters = new PKIXBuilderParameters(set3, rejectKeySelector);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new RuntimeException(ex);
        }
        pkixBuilderParameters.setInitialPolicies(this.params.initialPolicies());
        pkixBuilderParameters.setCertStores(this.certStores);
        pkixBuilderParameters.setExplicitPolicyRequired(this.params.explicitPolicyRequired());
        pkixBuilderParameters.setPolicyMappingInhibited(this.params.policyMappingInhibited());
        pkixBuilderParameters.setAnyPolicyInhibited(this.params.anyPolicyInhibited());
        pkixBuilderParameters.setDate(this.params.date());
        pkixBuilderParameters.setCertPathCheckers(this.params.certPathCheckers());
        pkixBuilderParameters.setSigProvider(this.params.sigProvider());
        pkixBuilderParameters.setRevocationEnabled(false);
        if (Builder.USE_AIA) {
            X509CertImpl impl = null;
            try {
                impl = X509CertImpl.toImpl(x509Certificate);
            }
            catch (final CertificateException ex2) {
                if (RevocationChecker.debug != null) {
                    RevocationChecker.debug.println("RevocationChecker.buildToNewKey: error decoding cert: " + ex2);
                }
            }
            AuthorityInfoAccessExtension authorityInfoAccessExtension = null;
            if (impl != null) {
                authorityInfoAccessExtension = impl.getAuthorityInfoAccessExtension();
            }
            if (authorityInfoAccessExtension != null) {
                final List<AccessDescription> accessDescriptions = authorityInfoAccessExtension.getAccessDescriptions();
                if (accessDescriptions != null) {
                    final Iterator<AccessDescription> iterator = accessDescriptions.iterator();
                    while (iterator.hasNext()) {
                        final CertStore instance = URICertStore.getInstance(iterator.next());
                        if (instance != null) {
                            if (RevocationChecker.debug != null) {
                                RevocationChecker.debug.println("adding AIAext CertStore");
                            }
                            pkixBuilderParameters.addCertStore(instance);
                        }
                    }
                }
            }
        }
        Label_0398: {
            CertPathBuilder instance2;
            try {
                instance2 = CertPathBuilder.getInstance("PKIX");
                break Label_0398;
            }
            catch (final NoSuchAlgorithmException ex3) {
                throw new CertPathValidatorException(ex3);
            }
            try {
                while (true) {
                    if (RevocationChecker.debug != null) {
                        RevocationChecker.debug.println("RevocationChecker.buildToNewKey() about to try build ...");
                    }
                    final PKIXCertPathBuilderResult pkixCertPathBuilderResult = (PKIXCertPathBuilderResult)instance2.build(pkixBuilderParameters);
                    if (RevocationChecker.debug != null) {
                        RevocationChecker.debug.println("RevocationChecker.buildToNewKey() about to check revocation ...");
                    }
                    if (set == null) {
                        set = new HashSet<X509Certificate>();
                    }
                    set.add(x509Certificate);
                    final TrustAnchor trustAnchor = pkixCertPathBuilderResult.getTrustAnchor();
                    PublicKey publicKey2 = trustAnchor.getCAPublicKey();
                    if (publicKey2 == null) {
                        publicKey2 = trustAnchor.getTrustedCert().getPublicKey();
                    }
                    boolean certCanSignCrl = true;
                    final List<? extends Certificate> certificates = pkixCertPathBuilderResult.getCertPath().getCertificates();
                    try {
                        for (int i = certificates.size() - 1; i >= 0; --i) {
                            final X509Certificate x509Certificate2 = certificates.get(i);
                            if (RevocationChecker.debug != null) {
                                RevocationChecker.debug.println("RevocationChecker.buildToNewKey() index " + i + " checking " + x509Certificate2);
                            }
                            this.checkCRLs(x509Certificate2, publicKey2, null, certCanSignCrl, true, set, set3);
                            certCanSignCrl = certCanSignCrl(x509Certificate2);
                            publicKey2 = x509Certificate2.getPublicKey();
                        }
                    }
                    catch (final CertPathValidatorException ex4) {
                        set2.add(pkixCertPathBuilderResult.getPublicKey());
                        continue;
                    }
                    if (RevocationChecker.debug != null) {
                        RevocationChecker.debug.println("RevocationChecker.buildToNewKey() got key " + pkixCertPathBuilderResult.getPublicKey());
                    }
                    final PublicKey publicKey3 = pkixCertPathBuilderResult.getPublicKey();
                    final X509Certificate x509Certificate3 = certificates.isEmpty() ? null : certificates.get(0);
                    try {
                        this.checkCRLs(x509Certificate, publicKey3, x509Certificate3, true, false, null, this.params.trustAnchors());
                    }
                    catch (final CertPathValidatorException ex5) {
                        if (ex5.getReason() == CertPathValidatorException.BasicReason.REVOKED) {
                            throw ex5;
                        }
                        set2.add(publicKey3);
                        continue;
                    }
                }
            }
            catch (final InvalidAlgorithmParameterException ex6) {
                throw new CertPathValidatorException(ex6);
            }
            catch (final CertPathBuilderException ex7) {
                throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
            }
        }
    }
    
    static {
        debug = Debug.getInstance("certpath");
        ALL_REASONS = new boolean[] { true, true, true, true, true, true, true, true, true };
        CRL_SIGN_USAGE = new boolean[] { false, false, false, false, false, false, true };
    }
    
    private enum Mode
    {
        PREFER_OCSP, 
        PREFER_CRLS, 
        ONLY_CRLS, 
        ONLY_OCSP;
    }
    
    private static class RevocationProperties
    {
        boolean onlyEE;
        boolean ocspEnabled;
        boolean crlDPEnabled;
        String ocspUrl;
        String ocspSubject;
        String ocspIssuer;
        String ocspSerial;
    }
    
    private static class RejectKeySelector extends X509CertSelector
    {
        private final Set<PublicKey> badKeySet;
        
        RejectKeySelector(final Set<PublicKey> badKeySet) {
            this.badKeySet = badKeySet;
        }
        
        @Override
        public boolean match(final Certificate certificate) {
            if (!super.match(certificate)) {
                return false;
            }
            if (this.badKeySet.contains(certificate.getPublicKey())) {
                if (RevocationChecker.debug != null) {
                    RevocationChecker.debug.println("RejectKeySelector.match: bad key");
                }
                return false;
            }
            if (RevocationChecker.debug != null) {
                RevocationChecker.debug.println("RejectKeySelector.match: returning true");
            }
            return true;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("RejectKeySelector: [\n");
            sb.append(super.toString());
            sb.append(this.badKeySet);
            sb.append("]");
            return sb.toString();
        }
    }
}
