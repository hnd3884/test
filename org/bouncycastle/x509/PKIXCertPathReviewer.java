package org.bouncycastle.x509;

import org.bouncycastle.asn1.DEROctetString;
import java.security.cert.X509CertSelector;
import java.security.cert.CertificateFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.DERIA5String;
import java.security.cert.X509CRLEntry;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.ASN1Enumerated;
import java.security.cert.X509CRL;
import java.net.InetAddress;
import org.bouncycastle.asn1.x509.qualified.MonetaryValue;
import org.bouncycastle.asn1.x509.qualified.QCStatement;
import java.security.cert.Certificate;
import java.security.cert.PKIXCertPathChecker;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1TaggedObject;
import java.util.Map;
import java.util.HashMap;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.jce.provider.PKIXPolicyNode;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.util.Collection;
import org.bouncycastle.i18n.filter.UntrustedUrlInput;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import org.bouncycastle.i18n.LocaleString;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.Extension;
import java.security.GeneralSecurityException;
import java.security.cert.CertPathValidatorException;
import java.security.SignatureException;
import java.util.Set;
import org.bouncycastle.i18n.filter.TrustedInput;
import java.math.BigInteger;
import org.bouncycastle.util.Integers;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.GeneralSubtree;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.NameConstraints;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.jce.provider.AnnotatedException;
import java.security.cert.X509Extension;
import org.bouncycastle.jce.provider.PKIXNameConstraintValidatorException;
import java.io.IOException;
import org.bouncycastle.i18n.filter.UntrustedInput;
import org.bouncycastle.asn1.ASN1Sequence;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import org.bouncycastle.jce.provider.PKIXNameConstraintValidator;
import java.util.ArrayList;
import org.bouncycastle.i18n.ErrorBundle;
import java.security.cert.PolicyNode;
import java.security.PublicKey;
import java.security.cert.TrustAnchor;
import java.util.List;
import java.util.Date;
import java.security.cert.PKIXParameters;
import java.security.cert.CertPath;

public class PKIXCertPathReviewer extends CertPathValidatorUtilities
{
    private static final String QC_STATEMENT;
    private static final String CRL_DIST_POINTS;
    private static final String AUTH_INFO_ACCESS;
    private static final String RESOURCE_NAME = "org.bouncycastle.x509.CertPathReviewerMessages";
    protected CertPath certPath;
    protected PKIXParameters pkixParams;
    protected Date validDate;
    protected List certs;
    protected int n;
    protected List[] notifications;
    protected List[] errors;
    protected TrustAnchor trustAnchor;
    protected PublicKey subjectPublicKey;
    protected PolicyNode policyTree;
    private boolean initialized;
    
    public void init(final CertPath certPath, final PKIXParameters pkixParameters) throws CertPathReviewerException {
        if (this.initialized) {
            throw new IllegalStateException("object is already initialized!");
        }
        this.initialized = true;
        if (certPath == null) {
            throw new NullPointerException("certPath was null");
        }
        this.certPath = certPath;
        this.certs = certPath.getCertificates();
        this.n = this.certs.size();
        if (this.certs.isEmpty()) {
            throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.emptyCertPath"));
        }
        this.pkixParams = (PKIXParameters)pkixParameters.clone();
        this.validDate = CertPathValidatorUtilities.getValidDate(this.pkixParams);
        this.notifications = null;
        this.errors = null;
        this.trustAnchor = null;
        this.subjectPublicKey = null;
        this.policyTree = null;
    }
    
    public PKIXCertPathReviewer(final CertPath certPath, final PKIXParameters pkixParameters) throws CertPathReviewerException {
        this.init(certPath, pkixParameters);
    }
    
    public PKIXCertPathReviewer() {
    }
    
    public CertPath getCertPath() {
        return this.certPath;
    }
    
    public int getCertPathSize() {
        return this.n;
    }
    
    public List[] getErrors() {
        this.doChecks();
        return this.errors;
    }
    
    public List getErrors(final int n) {
        this.doChecks();
        return this.errors[n + 1];
    }
    
    public List[] getNotifications() {
        this.doChecks();
        return this.notifications;
    }
    
    public List getNotifications(final int n) {
        this.doChecks();
        return this.notifications[n + 1];
    }
    
    public PolicyNode getPolicyTree() {
        this.doChecks();
        return this.policyTree;
    }
    
    public PublicKey getSubjectPublicKey() {
        this.doChecks();
        return this.subjectPublicKey;
    }
    
    public TrustAnchor getTrustAnchor() {
        this.doChecks();
        return this.trustAnchor;
    }
    
    public boolean isValidCertPath() {
        this.doChecks();
        boolean b = true;
        for (int i = 0; i < this.errors.length; ++i) {
            if (!this.errors[i].isEmpty()) {
                b = false;
                break;
            }
        }
        return b;
    }
    
    protected void addNotification(final ErrorBundle errorBundle) {
        this.notifications[0].add(errorBundle);
    }
    
    protected void addNotification(final ErrorBundle errorBundle, final int n) {
        if (n < -1 || n >= this.n) {
            throw new IndexOutOfBoundsException();
        }
        this.notifications[n + 1].add(errorBundle);
    }
    
    protected void addError(final ErrorBundle errorBundle) {
        this.errors[0].add(errorBundle);
    }
    
    protected void addError(final ErrorBundle errorBundle, final int n) {
        if (n < -1 || n >= this.n) {
            throw new IndexOutOfBoundsException();
        }
        this.errors[n + 1].add(errorBundle);
    }
    
    protected void doChecks() {
        if (!this.initialized) {
            throw new IllegalStateException("Object not initialized. Call init() first.");
        }
        if (this.notifications == null) {
            this.notifications = new List[this.n + 1];
            this.errors = new List[this.n + 1];
            for (int i = 0; i < this.notifications.length; ++i) {
                this.notifications[i] = new ArrayList();
                this.errors[i] = new ArrayList();
            }
            this.checkSignatures();
            this.checkNameConstraints();
            this.checkPathLength();
            this.checkPolicy();
            this.checkCriticalExtensions();
        }
    }
    
    private void checkNameConstraints() {
        final PKIXNameConstraintValidator pkixNameConstraintValidator = new PKIXNameConstraintValidator();
        try {
            for (int i = this.certs.size() - 1; i > 0; --i) {
                final int n = this.n - i;
                final X509Certificate x509Certificate = this.certs.get(i);
                if (!CertPathValidatorUtilities.isSelfIssued(x509Certificate)) {
                    final X500Principal subjectPrincipal = CertPathValidatorUtilities.getSubjectPrincipal(x509Certificate);
                    final ASN1InputStream asn1InputStream = new ASN1InputStream(new ByteArrayInputStream(subjectPrincipal.getEncoded()));
                    ASN1Sequence asn1Sequence;
                    try {
                        asn1Sequence = (ASN1Sequence)asn1InputStream.readObject();
                    }
                    catch (final IOException ex) {
                        throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.ncSubjectNameError", new Object[] { new UntrustedInput(subjectPrincipal) }), ex, this.certPath, i);
                    }
                    try {
                        pkixNameConstraintValidator.checkPermittedDN(asn1Sequence);
                    }
                    catch (final PKIXNameConstraintValidatorException ex2) {
                        throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.notPermittedDN", new Object[] { new UntrustedInput(subjectPrincipal.getName()) }), ex2, this.certPath, i);
                    }
                    try {
                        pkixNameConstraintValidator.checkExcludedDN(asn1Sequence);
                    }
                    catch (final PKIXNameConstraintValidatorException ex3) {
                        throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.excludedDN", new Object[] { new UntrustedInput(subjectPrincipal.getName()) }), ex3, this.certPath, i);
                    }
                    ASN1Sequence asn1Sequence2;
                    try {
                        asn1Sequence2 = (ASN1Sequence)CertPathValidatorUtilities.getExtensionValue(x509Certificate, PKIXCertPathReviewer.SUBJECT_ALTERNATIVE_NAME);
                    }
                    catch (final AnnotatedException ex4) {
                        throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.subjAltNameExtError"), ex4, this.certPath, i);
                    }
                    if (asn1Sequence2 != null) {
                        for (int j = 0; j < asn1Sequence2.size(); ++j) {
                            final GeneralName instance = GeneralName.getInstance(asn1Sequence2.getObjectAt(j));
                            try {
                                pkixNameConstraintValidator.checkPermitted(instance);
                                pkixNameConstraintValidator.checkExcluded(instance);
                            }
                            catch (final PKIXNameConstraintValidatorException ex5) {
                                throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.notPermittedEmail", new Object[] { new UntrustedInput(instance) }), ex5, this.certPath, i);
                            }
                        }
                    }
                }
                ASN1Sequence asn1Sequence3;
                try {
                    asn1Sequence3 = (ASN1Sequence)CertPathValidatorUtilities.getExtensionValue(x509Certificate, PKIXCertPathReviewer.NAME_CONSTRAINTS);
                }
                catch (final AnnotatedException ex6) {
                    throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.ncExtError"), ex6, this.certPath, i);
                }
                if (asn1Sequence3 != null) {
                    final NameConstraints instance2 = NameConstraints.getInstance(asn1Sequence3);
                    final GeneralSubtree[] permittedSubtrees = instance2.getPermittedSubtrees();
                    if (permittedSubtrees != null) {
                        pkixNameConstraintValidator.intersectPermittedSubtree(permittedSubtrees);
                    }
                    final GeneralSubtree[] excludedSubtrees = instance2.getExcludedSubtrees();
                    if (excludedSubtrees != null) {
                        for (int k = 0; k != excludedSubtrees.length; ++k) {
                            pkixNameConstraintValidator.addExcludedSubtree(excludedSubtrees[k]);
                        }
                    }
                }
            }
        }
        catch (final CertPathReviewerException ex7) {
            this.addError(ex7.getErrorMessage(), ex7.getIndex());
        }
    }
    
    private void checkPathLength() {
        int n = this.n;
        int n2 = 0;
        for (int i = this.certs.size() - 1; i > 0; --i) {
            final int n3 = this.n - i;
            final X509Certificate x509Certificate = this.certs.get(i);
            if (!CertPathValidatorUtilities.isSelfIssued(x509Certificate)) {
                if (n <= 0) {
                    this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.pathLengthExtended"));
                }
                --n;
                ++n2;
            }
            BasicConstraints instance;
            try {
                instance = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate, PKIXCertPathReviewer.BASIC_CONSTRAINTS));
            }
            catch (final AnnotatedException ex) {
                this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.processLengthConstError"), i);
                instance = null;
            }
            if (instance != null) {
                final BigInteger pathLenConstraint = instance.getPathLenConstraint();
                if (pathLenConstraint != null) {
                    final int intValue = pathLenConstraint.intValue();
                    if (intValue < n) {
                        n = intValue;
                    }
                }
            }
        }
        this.addNotification(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.totalPathLength", new Object[] { Integers.valueOf(n2) }));
    }
    
    private void checkSignatures() {
        TrustAnchor trustAnchor = null;
        X500Principal subjectPrincipal = null;
        this.addNotification(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.certPathValidDate", new Object[] { new TrustedInput(this.validDate), new TrustedInput(new Date()) }));
        try {
            final X509Certificate x509Certificate = this.certs.get(this.certs.size() - 1);
            final Collection trustAnchors = this.getTrustAnchors(x509Certificate, this.pkixParams.getTrustAnchors());
            if (trustAnchors.size() > 1) {
                this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.conflictingTrustAnchors", new Object[] { Integers.valueOf(trustAnchors.size()), new UntrustedInput(x509Certificate.getIssuerX500Principal()) }));
            }
            else if (trustAnchors.isEmpty()) {
                this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.noTrustAnchorFound", new Object[] { new UntrustedInput(x509Certificate.getIssuerX500Principal()), Integers.valueOf(this.pkixParams.getTrustAnchors().size()) }));
            }
            else {
                trustAnchor = trustAnchors.iterator().next();
                PublicKey publicKey;
                if (trustAnchor.getTrustedCert() != null) {
                    publicKey = trustAnchor.getTrustedCert().getPublicKey();
                }
                else {
                    publicKey = trustAnchor.getCAPublicKey();
                }
                try {
                    CertPathValidatorUtilities.verifyX509Certificate(x509Certificate, publicKey, this.pkixParams.getSigProvider());
                }
                catch (final SignatureException ex) {
                    this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.trustButInvalidCert"));
                }
                catch (final Exception ex2) {}
            }
        }
        catch (final CertPathReviewerException ex3) {
            this.addError(ex3.getErrorMessage());
        }
        catch (final Throwable t) {
            this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.unknown", new Object[] { new UntrustedInput(t.getMessage()), new UntrustedInput(t) }));
        }
        if (trustAnchor != null) {
            final X509Certificate trustedCert = trustAnchor.getTrustedCert();
            try {
                if (trustedCert != null) {
                    subjectPrincipal = CertPathValidatorUtilities.getSubjectPrincipal(trustedCert);
                }
                else {
                    subjectPrincipal = new X500Principal(trustAnchor.getCAName());
                }
            }
            catch (final IllegalArgumentException ex4) {
                this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.trustDNInvalid", new Object[] { new UntrustedInput(trustAnchor.getCAName()) }));
            }
            if (trustedCert != null) {
                final boolean[] keyUsage = trustedCert.getKeyUsage();
                if (keyUsage != null && !keyUsage[5]) {
                    this.addNotification(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.trustKeyUsage"));
                }
            }
        }
        PublicKey subjectPublicKey = null;
        X500Principal subjectX500Principal = subjectPrincipal;
        X509Certificate trustedCert2 = null;
        if (trustAnchor != null) {
            trustedCert2 = trustAnchor.getTrustedCert();
            if (trustedCert2 != null) {
                subjectPublicKey = trustedCert2.getPublicKey();
            }
            else {
                subjectPublicKey = trustAnchor.getCAPublicKey();
            }
            try {
                final AlgorithmIdentifier algorithmIdentifier = CertPathValidatorUtilities.getAlgorithmIdentifier(subjectPublicKey);
                algorithmIdentifier.getAlgorithm();
                algorithmIdentifier.getParameters();
            }
            catch (final CertPathValidatorException ex5) {
                this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.trustPubKeyError"));
            }
        }
        for (int i = this.certs.size() - 1; i >= 0; --i) {
            final int n = this.n - i;
            final X509Certificate x509Certificate2 = this.certs.get(i);
            if (subjectPublicKey != null) {
                try {
                    CertPathValidatorUtilities.verifyX509Certificate(x509Certificate2, subjectPublicKey, this.pkixParams.getSigProvider());
                }
                catch (final GeneralSecurityException ex6) {
                    this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.signatureNotVerified", new Object[] { ex6.getMessage(), ex6, ex6.getClass().getName() }), i);
                }
            }
            else if (CertPathValidatorUtilities.isSelfIssued(x509Certificate2)) {
                try {
                    CertPathValidatorUtilities.verifyX509Certificate(x509Certificate2, x509Certificate2.getPublicKey(), this.pkixParams.getSigProvider());
                    this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.rootKeyIsValidButNotATrustAnchor"), i);
                }
                catch (final GeneralSecurityException ex7) {
                    this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.signatureNotVerified", new Object[] { ex7.getMessage(), ex7, ex7.getClass().getName() }), i);
                }
            }
            else {
                final ErrorBundle errorBundle = new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.NoIssuerPublicKey");
                final byte[] extensionValue = x509Certificate2.getExtensionValue(Extension.authorityKeyIdentifier.getId());
                if (extensionValue != null) {
                    final AuthorityKeyIdentifier instance = AuthorityKeyIdentifier.getInstance(ASN1OctetString.getInstance(extensionValue).getOctets());
                    final GeneralNames authorityCertIssuer = instance.getAuthorityCertIssuer();
                    if (authorityCertIssuer != null) {
                        final GeneralName generalName = authorityCertIssuer.getNames()[0];
                        final BigInteger authorityCertSerialNumber = instance.getAuthorityCertSerialNumber();
                        if (authorityCertSerialNumber != null) {
                            errorBundle.setExtraArguments(new Object[] { new LocaleString("org.bouncycastle.x509.CertPathReviewerMessages", "missingIssuer"), " \"", generalName, "\" ", new LocaleString("org.bouncycastle.x509.CertPathReviewerMessages", "missingSerial"), " ", authorityCertSerialNumber });
                        }
                    }
                }
                this.addError(errorBundle, i);
            }
            try {
                x509Certificate2.checkValidity(this.validDate);
            }
            catch (final CertificateNotYetValidException ex8) {
                this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.certificateNotYetValid", new Object[] { new TrustedInput(x509Certificate2.getNotBefore()) }), i);
            }
            catch (final CertificateExpiredException ex9) {
                this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.certificateExpired", new Object[] { new TrustedInput(x509Certificate2.getNotAfter()) }), i);
            }
            if (this.pkixParams.isRevocationEnabled()) {
                CRLDistPoint instance2 = null;
                try {
                    final ASN1Primitive extensionValue2 = CertPathValidatorUtilities.getExtensionValue(x509Certificate2, PKIXCertPathReviewer.CRL_DIST_POINTS);
                    if (extensionValue2 != null) {
                        instance2 = CRLDistPoint.getInstance(extensionValue2);
                    }
                }
                catch (final AnnotatedException ex10) {
                    this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.crlDistPtExtError"), i);
                }
                AuthorityInformationAccess instance3 = null;
                try {
                    final ASN1Primitive extensionValue3 = CertPathValidatorUtilities.getExtensionValue(x509Certificate2, PKIXCertPathReviewer.AUTH_INFO_ACCESS);
                    if (extensionValue3 != null) {
                        instance3 = AuthorityInformationAccess.getInstance(extensionValue3);
                    }
                }
                catch (final AnnotatedException ex11) {
                    this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.crlAuthInfoAccError"), i);
                }
                final Vector crlDistUrls = this.getCRLDistUrls(instance2);
                final Vector ocspUrls = this.getOCSPUrls(instance3);
                final Iterator iterator = crlDistUrls.iterator();
                while (iterator.hasNext()) {
                    this.addNotification(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.crlDistPoint", new Object[] { new UntrustedUrlInput(iterator.next()) }), i);
                }
                final Iterator iterator2 = ocspUrls.iterator();
                while (iterator2.hasNext()) {
                    this.addNotification(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.ocspLocation", new Object[] { new UntrustedUrlInput(iterator2.next()) }), i);
                }
                try {
                    this.checkRevocation(this.pkixParams, x509Certificate2, this.validDate, trustedCert2, subjectPublicKey, crlDistUrls, ocspUrls, i);
                }
                catch (final CertPathReviewerException ex12) {
                    this.addError(ex12.getErrorMessage(), i);
                }
            }
            if (subjectX500Principal != null && !x509Certificate2.getIssuerX500Principal().equals(subjectX500Principal)) {
                this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.certWrongIssuer", new Object[] { subjectX500Principal.getName(), x509Certificate2.getIssuerX500Principal().getName() }), i);
            }
            if (n != this.n) {
                if (x509Certificate2 != null && x509Certificate2.getVersion() == 1) {
                    this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.noCACert"), i);
                }
                try {
                    final BasicConstraints instance4 = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue(x509Certificate2, PKIXCertPathReviewer.BASIC_CONSTRAINTS));
                    if (instance4 != null) {
                        if (!instance4.isCA()) {
                            this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.noCACert"), i);
                        }
                    }
                    else {
                        this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.noBasicConstraints"), i);
                    }
                }
                catch (final AnnotatedException ex13) {
                    this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.errorProcesingBC"), i);
                }
                final boolean[] keyUsage2 = x509Certificate2.getKeyUsage();
                if (keyUsage2 != null && !keyUsage2[5]) {
                    this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.noCertSign"), i);
                }
            }
            trustedCert2 = x509Certificate2;
            subjectX500Principal = x509Certificate2.getSubjectX500Principal();
            try {
                subjectPublicKey = CertPathValidatorUtilities.getNextWorkingKey(this.certs, i);
                final AlgorithmIdentifier algorithmIdentifier2 = CertPathValidatorUtilities.getAlgorithmIdentifier(subjectPublicKey);
                algorithmIdentifier2.getAlgorithm();
                algorithmIdentifier2.getParameters();
            }
            catch (final CertPathValidatorException ex14) {
                this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.pubKeyError"), i);
            }
        }
        this.trustAnchor = trustAnchor;
        this.subjectPublicKey = subjectPublicKey;
    }
    
    private void checkPolicy() {
        final Set<String> initialPolicies = this.pkixParams.getInitialPolicies();
        final ArrayList[] array = new ArrayList[this.n + 1];
        for (int i = 0; i < array.length; ++i) {
            array[i] = new ArrayList();
        }
        final HashSet set = new HashSet();
        set.add("2.5.29.32.0");
        PKIXPolicyNode pkixPolicyNode = new PKIXPolicyNode(new ArrayList(), 0, set, null, new HashSet(), "2.5.29.32.0", false);
        array[0].add(pkixPolicyNode);
        int n;
        if (this.pkixParams.isExplicitPolicyRequired()) {
            n = 0;
        }
        else {
            n = this.n + 1;
        }
        int n2;
        if (this.pkixParams.isAnyPolicyInhibited()) {
            n2 = 0;
        }
        else {
            n2 = this.n + 1;
        }
        int n3;
        if (this.pkixParams.isPolicyMappingInhibited()) {
            n3 = 0;
        }
        else {
            n3 = this.n + 1;
        }
        Set set2 = null;
        X509Certificate x509Certificate = null;
        try {
            int j;
            for (j = this.certs.size() - 1; j >= 0; --j) {
                final int n4 = this.n - j;
                x509Certificate = (X509Certificate)this.certs.get(j);
                ASN1Sequence asn1Sequence;
                try {
                    asn1Sequence = (ASN1Sequence)CertPathValidatorUtilities.getExtensionValue(x509Certificate, PKIXCertPathReviewer.CERTIFICATE_POLICIES);
                }
                catch (final AnnotatedException ex) {
                    throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.policyExtError"), ex, this.certPath, j);
                }
                if (asn1Sequence != null && pkixPolicyNode != null) {
                    final Enumeration objects = asn1Sequence.getObjects();
                    final HashSet set3 = new HashSet();
                    while (objects.hasMoreElements()) {
                        final PolicyInformation instance = PolicyInformation.getInstance(objects.nextElement());
                        final ASN1ObjectIdentifier policyIdentifier = instance.getPolicyIdentifier();
                        set3.add(policyIdentifier.getId());
                        if (!"2.5.29.32.0".equals(policyIdentifier.getId())) {
                            Set qualifierSet;
                            try {
                                qualifierSet = CertPathValidatorUtilities.getQualifierSet(instance.getPolicyQualifiers());
                            }
                            catch (final CertPathValidatorException ex2) {
                                throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.policyQualifierError"), ex2, this.certPath, j);
                            }
                            if (CertPathValidatorUtilities.processCertD1i(n4, array, policyIdentifier, qualifierSet)) {
                                continue;
                            }
                            CertPathValidatorUtilities.processCertD1ii(n4, array, policyIdentifier, qualifierSet);
                        }
                    }
                    if (set2 == null || set2.contains("2.5.29.32.0")) {
                        set2 = set3;
                    }
                    else {
                        final Iterator iterator = set2.iterator();
                        final HashSet set4 = new HashSet();
                        while (iterator.hasNext()) {
                            final Object next = iterator.next();
                            if (set3.contains(next)) {
                                set4.add(next);
                            }
                        }
                        set2 = set4;
                    }
                    if (n2 > 0 || (n4 < this.n && CertPathValidatorUtilities.isSelfIssued(x509Certificate))) {
                        final Enumeration objects2 = asn1Sequence.getObjects();
                        while (objects2.hasMoreElements()) {
                            final PolicyInformation instance2 = PolicyInformation.getInstance(objects2.nextElement());
                            if ("2.5.29.32.0".equals(instance2.getPolicyIdentifier().getId())) {
                                Set qualifierSet2;
                                try {
                                    qualifierSet2 = CertPathValidatorUtilities.getQualifierSet(instance2.getPolicyQualifiers());
                                }
                                catch (final CertPathValidatorException ex3) {
                                    throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.policyQualifierError"), ex3, this.certPath, j);
                                }
                                final ArrayList list = array[n4 - 1];
                                for (int k = 0; k < list.size(); ++k) {
                                    final PKIXPolicyNode pkixPolicyNode2 = (PKIXPolicyNode)list.get(k);
                                    for (final Object next2 : pkixPolicyNode2.getExpectedPolicies()) {
                                        String id;
                                        if (next2 instanceof String) {
                                            id = (String)next2;
                                        }
                                        else {
                                            if (!(next2 instanceof ASN1ObjectIdentifier)) {
                                                continue;
                                            }
                                            id = ((ASN1ObjectIdentifier)next2).getId();
                                        }
                                        boolean b = false;
                                        final Iterator children = pkixPolicyNode2.getChildren();
                                        while (children.hasNext()) {
                                            if (id.equals(((PKIXPolicyNode)children.next()).getValidPolicy())) {
                                                b = true;
                                            }
                                        }
                                        if (!b) {
                                            final HashSet set5 = new HashSet();
                                            set5.add(id);
                                            final PKIXPolicyNode pkixPolicyNode3 = new PKIXPolicyNode(new ArrayList(), n4, set5, pkixPolicyNode2, qualifierSet2, id, false);
                                            pkixPolicyNode2.addChild(pkixPolicyNode3);
                                            array[n4].add(pkixPolicyNode3);
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    for (int l = n4 - 1; l >= 0; --l) {
                        final ArrayList list2 = array[l];
                        for (int n5 = 0; n5 < list2.size(); ++n5) {
                            final PKIXPolicyNode pkixPolicyNode4 = (PKIXPolicyNode)list2.get(n5);
                            if (!pkixPolicyNode4.hasChildren()) {
                                pkixPolicyNode = CertPathValidatorUtilities.removePolicyNode(pkixPolicyNode, array, pkixPolicyNode4);
                                if (pkixPolicyNode == null) {
                                    break;
                                }
                            }
                        }
                    }
                    final Set<String> criticalExtensionOIDs = x509Certificate.getCriticalExtensionOIDs();
                    if (criticalExtensionOIDs != null) {
                        final boolean contains = criticalExtensionOIDs.contains(PKIXCertPathReviewer.CERTIFICATE_POLICIES);
                        final ArrayList list3 = array[n4];
                        for (int n6 = 0; n6 < list3.size(); ++n6) {
                            ((PKIXPolicyNode)list3.get(n6)).setCritical(contains);
                        }
                    }
                }
                if (asn1Sequence == null) {
                    pkixPolicyNode = null;
                }
                if (n <= 0 && pkixPolicyNode == null) {
                    throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.noValidPolicyTree"));
                }
                if (n4 != this.n) {
                    ASN1Primitive extensionValue;
                    try {
                        extensionValue = CertPathValidatorUtilities.getExtensionValue(x509Certificate, PKIXCertPathReviewer.POLICY_MAPPINGS);
                    }
                    catch (final AnnotatedException ex4) {
                        throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.policyMapExtError"), ex4, this.certPath, j);
                    }
                    if (extensionValue != null) {
                        final ASN1Sequence asn1Sequence2 = (ASN1Sequence)extensionValue;
                        for (int n7 = 0; n7 < asn1Sequence2.size(); ++n7) {
                            final ASN1Sequence asn1Sequence3 = (ASN1Sequence)asn1Sequence2.getObjectAt(n7);
                            final ASN1ObjectIdentifier asn1ObjectIdentifier = (ASN1ObjectIdentifier)asn1Sequence3.getObjectAt(0);
                            final ASN1ObjectIdentifier asn1ObjectIdentifier2 = (ASN1ObjectIdentifier)asn1Sequence3.getObjectAt(1);
                            if ("2.5.29.32.0".equals(asn1ObjectIdentifier.getId())) {
                                throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.invalidPolicyMapping"), this.certPath, j);
                            }
                            if ("2.5.29.32.0".equals(asn1ObjectIdentifier2.getId())) {
                                throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.invalidPolicyMapping"), this.certPath, j);
                            }
                        }
                    }
                    if (extensionValue != null) {
                        final ASN1Sequence asn1Sequence4 = (ASN1Sequence)extensionValue;
                        final HashMap hashMap = new HashMap();
                        final HashSet set6 = new HashSet();
                        for (int n8 = 0; n8 < asn1Sequence4.size(); ++n8) {
                            final ASN1Sequence asn1Sequence5 = (ASN1Sequence)asn1Sequence4.getObjectAt(n8);
                            final String id2 = ((ASN1ObjectIdentifier)asn1Sequence5.getObjectAt(0)).getId();
                            final String id3 = ((ASN1ObjectIdentifier)asn1Sequence5.getObjectAt(1)).getId();
                            if (!hashMap.containsKey(id2)) {
                                final HashSet set7 = new HashSet();
                                set7.add(id3);
                                hashMap.put(id2, set7);
                                set6.add(id2);
                            }
                            else {
                                ((Set<String>)hashMap.get(id2)).add(id3);
                            }
                        }
                        for (final String s : set6) {
                            if (n3 > 0) {
                                try {
                                    CertPathValidatorUtilities.prepareNextCertB1(n4, array, s, hashMap, x509Certificate);
                                    continue;
                                }
                                catch (final AnnotatedException ex5) {
                                    throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.policyExtError"), ex5, this.certPath, j);
                                }
                                catch (final CertPathValidatorException ex6) {
                                    throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.policyQualifierError"), ex6, this.certPath, j);
                                }
                            }
                            if (n3 <= 0) {
                                pkixPolicyNode = CertPathValidatorUtilities.prepareNextCertB2(n4, array, s, pkixPolicyNode);
                            }
                        }
                    }
                    if (!CertPathValidatorUtilities.isSelfIssued(x509Certificate)) {
                        if (n != 0) {
                            --n;
                        }
                        if (n3 != 0) {
                            --n3;
                        }
                        if (n2 != 0) {
                            --n2;
                        }
                    }
                    try {
                        final ASN1Sequence asn1Sequence6 = (ASN1Sequence)CertPathValidatorUtilities.getExtensionValue(x509Certificate, PKIXCertPathReviewer.POLICY_CONSTRAINTS);
                        if (asn1Sequence6 != null) {
                            final Enumeration objects3 = asn1Sequence6.getObjects();
                            while (objects3.hasMoreElements()) {
                                final ASN1TaggedObject asn1TaggedObject = objects3.nextElement();
                                switch (asn1TaggedObject.getTagNo()) {
                                    case 0: {
                                        final int intValue = ASN1Integer.getInstance(asn1TaggedObject, false).getValue().intValue();
                                        if (intValue < n) {
                                            n = intValue;
                                            continue;
                                        }
                                        continue;
                                    }
                                    case 1: {
                                        final int intValue2 = ASN1Integer.getInstance(asn1TaggedObject, false).getValue().intValue();
                                        if (intValue2 < n3) {
                                            n3 = intValue2;
                                            continue;
                                        }
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                    catch (final AnnotatedException ex7) {
                        throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.policyConstExtError"), this.certPath, j);
                    }
                    try {
                        final ASN1Integer asn1Integer = (ASN1Integer)CertPathValidatorUtilities.getExtensionValue(x509Certificate, PKIXCertPathReviewer.INHIBIT_ANY_POLICY);
                        if (asn1Integer != null) {
                            final int intValue3 = asn1Integer.getValue().intValue();
                            if (intValue3 < n2) {
                                n2 = intValue3;
                            }
                        }
                    }
                    catch (final AnnotatedException ex8) {
                        throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.policyInhibitExtError"), this.certPath, j);
                    }
                }
            }
            if (!CertPathValidatorUtilities.isSelfIssued(x509Certificate) && n > 0) {
                --n;
            }
            try {
                final ASN1Sequence asn1Sequence7 = (ASN1Sequence)CertPathValidatorUtilities.getExtensionValue(x509Certificate, PKIXCertPathReviewer.POLICY_CONSTRAINTS);
                if (asn1Sequence7 != null) {
                    final Enumeration objects4 = asn1Sequence7.getObjects();
                    while (objects4.hasMoreElements()) {
                        final ASN1TaggedObject asn1TaggedObject2 = objects4.nextElement();
                        switch (asn1TaggedObject2.getTagNo()) {
                            case 0: {
                                if (ASN1Integer.getInstance(asn1TaggedObject2, false).getValue().intValue() == 0) {
                                    n = 0;
                                    continue;
                                }
                                continue;
                            }
                        }
                    }
                }
            }
            catch (final AnnotatedException ex9) {
                throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.policyConstExtError"), this.certPath, j);
            }
            PKIXPolicyNode pkixPolicyNode5;
            if (pkixPolicyNode == null) {
                if (this.pkixParams.isExplicitPolicyRequired()) {
                    throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.explicitPolicy"), this.certPath, j);
                }
                pkixPolicyNode5 = null;
            }
            else if (CertPathValidatorUtilities.isAnyPolicy(initialPolicies)) {
                if (this.pkixParams.isExplicitPolicyRequired()) {
                    if (set2.isEmpty()) {
                        throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.explicitPolicy"), this.certPath, j);
                    }
                    final HashSet set8 = new HashSet();
                    for (int n9 = 0; n9 < array.length; ++n9) {
                        final ArrayList list4 = array[n9];
                        for (int n10 = 0; n10 < list4.size(); ++n10) {
                            final PKIXPolicyNode pkixPolicyNode6 = (PKIXPolicyNode)list4.get(n10);
                            if ("2.5.29.32.0".equals(pkixPolicyNode6.getValidPolicy())) {
                                final Iterator children2 = pkixPolicyNode6.getChildren();
                                while (children2.hasNext()) {
                                    set8.add(children2.next());
                                }
                            }
                        }
                    }
                    final Iterator iterator4 = set8.iterator();
                    while (iterator4.hasNext()) {
                        if (!set2.contains(((PKIXPolicyNode)iterator4.next()).getValidPolicy())) {}
                    }
                    if (pkixPolicyNode != null) {
                        for (int n11 = this.n - 1; n11 >= 0; --n11) {
                            final ArrayList list5 = array[n11];
                            for (int n12 = 0; n12 < list5.size(); ++n12) {
                                final PKIXPolicyNode pkixPolicyNode7 = (PKIXPolicyNode)list5.get(n12);
                                if (!pkixPolicyNode7.hasChildren()) {
                                    pkixPolicyNode = CertPathValidatorUtilities.removePolicyNode(pkixPolicyNode, array, pkixPolicyNode7);
                                }
                            }
                        }
                    }
                }
                pkixPolicyNode5 = pkixPolicyNode;
            }
            else {
                final HashSet set9 = new HashSet();
                for (int n13 = 0; n13 < array.length; ++n13) {
                    final ArrayList list6 = array[n13];
                    for (int n14 = 0; n14 < list6.size(); ++n14) {
                        final PKIXPolicyNode pkixPolicyNode8 = (PKIXPolicyNode)list6.get(n14);
                        if ("2.5.29.32.0".equals(pkixPolicyNode8.getValidPolicy())) {
                            final Iterator children3 = pkixPolicyNode8.getChildren();
                            while (children3.hasNext()) {
                                final PKIXPolicyNode pkixPolicyNode9 = children3.next();
                                if (!"2.5.29.32.0".equals(pkixPolicyNode9.getValidPolicy())) {
                                    set9.add(pkixPolicyNode9);
                                }
                            }
                        }
                    }
                }
                for (final PKIXPolicyNode pkixPolicyNode10 : set9) {
                    if (!initialPolicies.contains(pkixPolicyNode10.getValidPolicy())) {
                        pkixPolicyNode = CertPathValidatorUtilities.removePolicyNode(pkixPolicyNode, array, pkixPolicyNode10);
                    }
                }
                if (pkixPolicyNode != null) {
                    for (int n15 = this.n - 1; n15 >= 0; --n15) {
                        final ArrayList list7 = array[n15];
                        for (int n16 = 0; n16 < list7.size(); ++n16) {
                            final PKIXPolicyNode pkixPolicyNode11 = (PKIXPolicyNode)list7.get(n16);
                            if (!pkixPolicyNode11.hasChildren()) {
                                pkixPolicyNode = CertPathValidatorUtilities.removePolicyNode(pkixPolicyNode, array, pkixPolicyNode11);
                            }
                        }
                    }
                }
                pkixPolicyNode5 = pkixPolicyNode;
            }
            if (n <= 0 && pkixPolicyNode5 == null) {
                throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.invalidPolicy"));
            }
        }
        catch (final CertPathReviewerException ex10) {
            this.addError(ex10.getErrorMessage(), ex10.getIndex());
        }
    }
    
    private void checkCriticalExtensions() {
        final List<PKIXCertPathChecker> certPathCheckers = this.pkixParams.getCertPathCheckers();
        final Iterator<PKIXCertPathChecker> iterator = certPathCheckers.iterator();
        try {
            try {
                while (iterator.hasNext()) {
                    iterator.next().init(false);
                }
            }
            catch (final CertPathValidatorException ex) {
                throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.certPathCheckerError", new Object[] { ex.getMessage(), ex, ex.getClass().getName() }), ex);
            }
            for (int i = this.certs.size() - 1; i >= 0; --i) {
                final X509Certificate x509Certificate = this.certs.get(i);
                final Set<String> criticalExtensionOIDs = x509Certificate.getCriticalExtensionOIDs();
                if (criticalExtensionOIDs != null) {
                    if (!criticalExtensionOIDs.isEmpty()) {
                        criticalExtensionOIDs.remove(PKIXCertPathReviewer.KEY_USAGE);
                        criticalExtensionOIDs.remove(PKIXCertPathReviewer.CERTIFICATE_POLICIES);
                        criticalExtensionOIDs.remove(PKIXCertPathReviewer.POLICY_MAPPINGS);
                        criticalExtensionOIDs.remove(PKIXCertPathReviewer.INHIBIT_ANY_POLICY);
                        criticalExtensionOIDs.remove(PKIXCertPathReviewer.ISSUING_DISTRIBUTION_POINT);
                        criticalExtensionOIDs.remove(PKIXCertPathReviewer.DELTA_CRL_INDICATOR);
                        criticalExtensionOIDs.remove(PKIXCertPathReviewer.POLICY_CONSTRAINTS);
                        criticalExtensionOIDs.remove(PKIXCertPathReviewer.BASIC_CONSTRAINTS);
                        criticalExtensionOIDs.remove(PKIXCertPathReviewer.SUBJECT_ALTERNATIVE_NAME);
                        criticalExtensionOIDs.remove(PKIXCertPathReviewer.NAME_CONSTRAINTS);
                        if (criticalExtensionOIDs.contains(PKIXCertPathReviewer.QC_STATEMENT) && this.processQcStatements(x509Certificate, i)) {
                            criticalExtensionOIDs.remove(PKIXCertPathReviewer.QC_STATEMENT);
                        }
                        final Iterator<PKIXCertPathChecker> iterator2 = certPathCheckers.iterator();
                        while (iterator2.hasNext()) {
                            try {
                                iterator2.next().check(x509Certificate, criticalExtensionOIDs);
                                continue;
                            }
                            catch (final CertPathValidatorException ex2) {
                                throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.criticalExtensionError", new Object[] { ex2.getMessage(), ex2, ex2.getClass().getName() }), ex2.getCause(), this.certPath, i);
                            }
                            break;
                        }
                        if (!criticalExtensionOIDs.isEmpty()) {
                            final Iterator iterator3 = criticalExtensionOIDs.iterator();
                            while (iterator3.hasNext()) {
                                this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.unknownCriticalExt", new Object[] { new ASN1ObjectIdentifier((String)iterator3.next()) }), i);
                            }
                        }
                    }
                }
            }
        }
        catch (final CertPathReviewerException ex3) {
            this.addError(ex3.getErrorMessage(), ex3.getIndex());
        }
    }
    
    private boolean processQcStatements(final X509Certificate x509Certificate, final int n) {
        try {
            boolean b = false;
            final ASN1Sequence asn1Sequence = (ASN1Sequence)CertPathValidatorUtilities.getExtensionValue(x509Certificate, PKIXCertPathReviewer.QC_STATEMENT);
            for (int i = 0; i < asn1Sequence.size(); ++i) {
                final QCStatement instance = QCStatement.getInstance(asn1Sequence.getObjectAt(i));
                if (QCStatement.id_etsi_qcs_QcCompliance.equals(instance.getStatementId())) {
                    this.addNotification(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.QcEuCompliance"), n);
                }
                else if (!QCStatement.id_qcs_pkixQCSyntax_v1.equals(instance.getStatementId())) {
                    if (QCStatement.id_etsi_qcs_QcSSCD.equals(instance.getStatementId())) {
                        this.addNotification(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.QcSSCD"), n);
                    }
                    else if (QCStatement.id_etsi_qcs_LimiteValue.equals(instance.getStatementId())) {
                        final MonetaryValue instance2 = MonetaryValue.getInstance(instance.getStatementInfo());
                        instance2.getCurrency();
                        final double n2 = instance2.getAmount().doubleValue() * Math.pow(10.0, instance2.getExponent().doubleValue());
                        ErrorBundle errorBundle;
                        if (instance2.getCurrency().isAlphabetic()) {
                            errorBundle = new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.QcLimitValueAlpha", new Object[] { instance2.getCurrency().getAlphabetic(), new TrustedInput(new Double(n2)), instance2 });
                        }
                        else {
                            errorBundle = new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.QcLimitValueNum", new Object[] { Integers.valueOf(instance2.getCurrency().getNumeric()), new TrustedInput(new Double(n2)), instance2 });
                        }
                        this.addNotification(errorBundle, n);
                    }
                    else {
                        this.addNotification(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.QcUnknownStatement", new Object[] { instance.getStatementId(), new UntrustedInput(instance) }), n);
                        b = true;
                    }
                }
            }
            return !b;
        }
        catch (final AnnotatedException ex) {
            this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.QcStatementExtError"), n);
            return false;
        }
    }
    
    private String IPtoString(final byte[] array) {
        String s;
        try {
            s = InetAddress.getByAddress(array).getHostAddress();
        }
        catch (final Exception ex) {
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i != array.length; ++i) {
                sb.append(Integer.toHexString(array[i] & 0xFF));
                sb.append(' ');
            }
            s = sb.toString();
        }
        return s;
    }
    
    protected void checkRevocation(final PKIXParameters pkixParameters, final X509Certificate x509Certificate, final Date date, final X509Certificate x509Certificate2, final PublicKey publicKey, final Vector vector, final Vector vector2, final int n) throws CertPathReviewerException {
        this.checkCRLs(pkixParameters, x509Certificate, date, x509Certificate2, publicKey, vector, n);
    }
    
    protected void checkCRLs(final PKIXParameters pkixParameters, final X509Certificate certificateChecking, final Date date, final X509Certificate x509Certificate, final PublicKey publicKey, final Vector vector, final int n) throws CertPathReviewerException {
        final X509CRLStoreSelector x509CRLStoreSelector = new X509CRLStoreSelector();
        try {
            x509CRLStoreSelector.addIssuerName(CertPathValidatorUtilities.getEncodedIssuerPrincipal(certificateChecking).getEncoded());
        }
        catch (final IOException ex) {
            throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.crlIssuerException"), ex);
        }
        x509CRLStoreSelector.setCertificateChecking(certificateChecking);
        Iterator iterator;
        try {
            final Set crLs = PKIXCertPathReviewer.CRL_UTIL.findCRLs(x509CRLStoreSelector, pkixParameters);
            iterator = crLs.iterator();
            if (crLs.isEmpty()) {
                final Iterator iterator2 = PKIXCertPathReviewer.CRL_UTIL.findCRLs(new X509CRLStoreSelector(), pkixParameters).iterator();
                final ArrayList list = new ArrayList();
                while (iterator2.hasNext()) {
                    list.add(((X509CRL)iterator2.next()).getIssuerX500Principal());
                }
                this.addNotification(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.noCrlInCertstore", new Object[] { new UntrustedInput(x509CRLStoreSelector.getIssuerNames()), new UntrustedInput(list), Integers.valueOf(list.size()) }), n);
            }
        }
        catch (final AnnotatedException ex2) {
            this.addError(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.crlExtractionError", new Object[] { ex2.getCause().getMessage(), ex2.getCause(), ex2.getCause().getClass().getName() }), n);
            iterator = new ArrayList().iterator();
        }
        int n2 = 0;
        X509CRL x509CRL = null;
        while (iterator.hasNext()) {
            x509CRL = (X509CRL)iterator.next();
            if (x509CRL.getNextUpdate() == null || pkixParameters.getDate().before(x509CRL.getNextUpdate())) {
                n2 = 1;
                this.addNotification(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.localValidCRL", new Object[] { new TrustedInput(x509CRL.getThisUpdate()), new TrustedInput(x509CRL.getNextUpdate()) }), n);
                break;
            }
            this.addNotification(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.localInvalidCRL", new Object[] { new TrustedInput(x509CRL.getThisUpdate()), new TrustedInput(x509CRL.getNextUpdate()) }), n);
        }
        if (n2 == 0) {
            final Iterator iterator3 = vector.iterator();
            while (iterator3.hasNext()) {
                try {
                    final String s = (String)iterator3.next();
                    final X509CRL crl = this.getCRL(s);
                    if (crl == null) {
                        continue;
                    }
                    if (!certificateChecking.getIssuerX500Principal().equals(crl.getIssuerX500Principal())) {
                        this.addNotification(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.onlineCRLWrongCA", new Object[] { new UntrustedInput(crl.getIssuerX500Principal().getName()), new UntrustedInput(certificateChecking.getIssuerX500Principal().getName()), new UntrustedUrlInput(s) }), n);
                    }
                    else {
                        if (crl.getNextUpdate() == null || this.pkixParams.getDate().before(crl.getNextUpdate())) {
                            n2 = 1;
                            this.addNotification(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.onlineValidCRL", new Object[] { new TrustedInput(crl.getThisUpdate()), new TrustedInput(crl.getNextUpdate()), new UntrustedUrlInput(s) }), n);
                            x509CRL = crl;
                            break;
                        }
                        this.addNotification(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.onlineInvalidCRL", new Object[] { new TrustedInput(crl.getThisUpdate()), new TrustedInput(crl.getNextUpdate()), new UntrustedUrlInput(s) }), n);
                    }
                }
                catch (final CertPathReviewerException ex3) {
                    this.addNotification(ex3.getErrorMessage(), n);
                }
            }
        }
        Label_1818: {
            if (x509CRL != null) {
                if (x509Certificate != null) {
                    final boolean[] keyUsage = x509Certificate.getKeyUsage();
                    if (keyUsage != null && (keyUsage.length < 7 || !keyUsage[6])) {
                        throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.noCrlSigningPermited"));
                    }
                }
                if (publicKey != null) {
                    Label_0938: {
                        try {
                            x509CRL.verify(publicKey, "BC");
                            break Label_0938;
                        }
                        catch (final Exception ex4) {
                            throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.crlVerifyFailed"), ex4);
                        }
                        throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.crlNoIssuerPublicKey"));
                    }
                    final X509CRLEntry revokedCertificate = x509CRL.getRevokedCertificate(certificateChecking.getSerialNumber());
                    if (revokedCertificate != null) {
                        String s2 = null;
                        if (revokedCertificate.hasExtensions()) {
                            ASN1Enumerated instance;
                            try {
                                instance = ASN1Enumerated.getInstance(CertPathValidatorUtilities.getExtensionValue(revokedCertificate, Extension.reasonCode.getId()));
                            }
                            catch (final AnnotatedException ex5) {
                                throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.crlReasonExtError"), ex5);
                            }
                            if (instance != null) {
                                s2 = PKIXCertPathReviewer.crlReasons[instance.getValue().intValue()];
                            }
                        }
                        if (s2 == null) {
                            s2 = PKIXCertPathReviewer.crlReasons[7];
                        }
                        final LocaleString localeString = new LocaleString("org.bouncycastle.x509.CertPathReviewerMessages", s2);
                        if (!date.before(revokedCertificate.getRevocationDate())) {
                            throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.certRevoked", new Object[] { new TrustedInput(revokedCertificate.getRevocationDate()), localeString }));
                        }
                        this.addNotification(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.revokedAfterValidation", new Object[] { new TrustedInput(revokedCertificate.getRevocationDate()), localeString }), n);
                    }
                    else {
                        this.addNotification(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.notRevoked"), n);
                    }
                    if (x509CRL.getNextUpdate() != null && x509CRL.getNextUpdate().before(this.pkixParams.getDate())) {
                        this.addNotification(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.crlUpdateAvailable", new Object[] { new TrustedInput(x509CRL.getNextUpdate()) }), n);
                    }
                    ASN1Primitive extensionValue;
                    try {
                        extensionValue = CertPathValidatorUtilities.getExtensionValue(x509CRL, PKIXCertPathReviewer.ISSUING_DISTRIBUTION_POINT);
                    }
                    catch (final AnnotatedException ex6) {
                        throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.distrPtExtError"));
                    }
                    ASN1Primitive extensionValue2;
                    try {
                        extensionValue2 = CertPathValidatorUtilities.getExtensionValue(x509CRL, PKIXCertPathReviewer.DELTA_CRL_INDICATOR);
                    }
                    catch (final AnnotatedException ex7) {
                        throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.deltaCrlExtError"));
                    }
                    if (extensionValue2 != null) {
                        final X509CRLStoreSelector x509CRLStoreSelector2 = new X509CRLStoreSelector();
                        try {
                            x509CRLStoreSelector2.addIssuerName(CertPathValidatorUtilities.getIssuerPrincipal(x509CRL).getEncoded());
                        }
                        catch (final IOException ex8) {
                            throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.crlIssuerException"), ex8);
                        }
                        x509CRLStoreSelector2.setMinCRLNumber(((ASN1Integer)extensionValue2).getPositiveValue());
                        try {
                            x509CRLStoreSelector2.setMaxCRLNumber(((ASN1Integer)CertPathValidatorUtilities.getExtensionValue(x509CRL, PKIXCertPathReviewer.CRL_NUMBER)).getPositiveValue().subtract(BigInteger.valueOf(1L)));
                        }
                        catch (final AnnotatedException ex9) {
                            throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.crlNbrExtError"), ex9);
                        }
                        boolean b = false;
                        Iterator iterator4;
                        try {
                            iterator4 = PKIXCertPathReviewer.CRL_UTIL.findCRLs(x509CRLStoreSelector2, pkixParameters).iterator();
                        }
                        catch (final AnnotatedException ex10) {
                            throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.crlExtractionError"), ex10);
                        }
                        while (iterator4.hasNext()) {
                            final X509CRL x509CRL2 = (X509CRL)iterator4.next();
                            ASN1Primitive extensionValue3;
                            try {
                                extensionValue3 = CertPathValidatorUtilities.getExtensionValue(x509CRL2, PKIXCertPathReviewer.ISSUING_DISTRIBUTION_POINT);
                            }
                            catch (final AnnotatedException ex11) {
                                throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.distrPtExtError"), ex11);
                            }
                            if (extensionValue == null) {
                                if (extensionValue3 == null) {
                                    b = true;
                                    break;
                                }
                                continue;
                            }
                            else {
                                if (extensionValue.equals(extensionValue3)) {
                                    b = true;
                                    break;
                                }
                                continue;
                            }
                        }
                        if (!b) {
                            throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.noBaseCRL"));
                        }
                    }
                    if (extensionValue == null) {
                        break Label_1818;
                    }
                    final IssuingDistributionPoint instance2 = IssuingDistributionPoint.getInstance(extensionValue);
                    BasicConstraints instance3;
                    try {
                        instance3 = BasicConstraints.getInstance(CertPathValidatorUtilities.getExtensionValue(certificateChecking, PKIXCertPathReviewer.BASIC_CONSTRAINTS));
                    }
                    catch (final AnnotatedException ex12) {
                        throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.crlBCExtError"), ex12);
                    }
                    if (instance2.onlyContainsUserCerts() && instance3 != null && instance3.isCA()) {
                        throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.crlOnlyUserCert"));
                    }
                    if (instance2.onlyContainsCACerts() && (instance3 == null || !instance3.isCA())) {
                        throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.crlOnlyCaCert"));
                    }
                    if (instance2.onlyContainsAttributeCerts()) {
                        throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.crlOnlyAttrCert"));
                    }
                    break Label_1818;
                }
                throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.crlNoIssuerPublicKey"));
            }
        }
        if (n2 == 0) {
            throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.noValidCrlFound"));
        }
    }
    
    protected Vector getCRLDistUrls(final CRLDistPoint crlDistPoint) {
        final Vector vector = new Vector();
        if (crlDistPoint != null) {
            final DistributionPoint[] distributionPoints = crlDistPoint.getDistributionPoints();
            for (int i = 0; i < distributionPoints.length; ++i) {
                final DistributionPointName distributionPoint = distributionPoints[i].getDistributionPoint();
                if (distributionPoint.getType() == 0) {
                    final GeneralName[] names = GeneralNames.getInstance(distributionPoint.getName()).getNames();
                    for (int j = 0; j < names.length; ++j) {
                        if (names[j].getTagNo() == 6) {
                            vector.add(((DERIA5String)names[j].getName()).getString());
                        }
                    }
                }
            }
        }
        return vector;
    }
    
    protected Vector getOCSPUrls(final AuthorityInformationAccess authorityInformationAccess) {
        final Vector vector = new Vector();
        if (authorityInformationAccess != null) {
            final AccessDescription[] accessDescriptions = authorityInformationAccess.getAccessDescriptions();
            for (int i = 0; i < accessDescriptions.length; ++i) {
                if (accessDescriptions[i].getAccessMethod().equals(AccessDescription.id_ad_ocsp)) {
                    final GeneralName accessLocation = accessDescriptions[i].getAccessLocation();
                    if (accessLocation.getTagNo() == 6) {
                        vector.add(((DERIA5String)accessLocation.getName()).getString());
                    }
                }
            }
        }
        return vector;
    }
    
    private X509CRL getCRL(final String s) throws CertPathReviewerException {
        X509CRL x509CRL = null;
        try {
            final URL url = new URL(s);
            if (url.getProtocol().equals("http") || url.getProtocol().equals("https")) {
                final HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() != 200) {
                    throw new Exception(httpURLConnection.getResponseMessage());
                }
                x509CRL = (X509CRL)CertificateFactory.getInstance("X.509", "BC").generateCRL(httpURLConnection.getInputStream());
            }
        }
        catch (final Exception ex) {
            throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.loadCrlDistPointError", new Object[] { new UntrustedInput(s), ex.getMessage(), ex, ex.getClass().getName() }));
        }
        return x509CRL;
    }
    
    protected Collection getTrustAnchors(final X509Certificate x509Certificate, final Set set) throws CertPathReviewerException {
        final ArrayList list = new ArrayList();
        final Iterator iterator = set.iterator();
        final X509CertSelector x509CertSelector = new X509CertSelector();
        try {
            x509CertSelector.setSubject(CertPathValidatorUtilities.getEncodedIssuerPrincipal(x509Certificate).getEncoded());
            final byte[] extensionValue = x509Certificate.getExtensionValue(Extension.authorityKeyIdentifier.getId());
            if (extensionValue != null) {
                final AuthorityKeyIdentifier instance = AuthorityKeyIdentifier.getInstance(ASN1Primitive.fromByteArray(((ASN1OctetString)ASN1Primitive.fromByteArray(extensionValue)).getOctets()));
                x509CertSelector.setSerialNumber(instance.getAuthorityCertSerialNumber());
                final byte[] keyIdentifier = instance.getKeyIdentifier();
                if (keyIdentifier != null) {
                    x509CertSelector.setSubjectKeyIdentifier(new DEROctetString(keyIdentifier).getEncoded());
                }
            }
        }
        catch (final IOException ex) {
            throw new CertPathReviewerException(new ErrorBundle("org.bouncycastle.x509.CertPathReviewerMessages", "CertPathReviewer.trustAnchorIssuerError"));
        }
        while (iterator.hasNext()) {
            final TrustAnchor trustAnchor = (TrustAnchor)iterator.next();
            if (trustAnchor.getTrustedCert() != null) {
                if (!x509CertSelector.match(trustAnchor.getTrustedCert())) {
                    continue;
                }
                list.add(trustAnchor);
            }
            else {
                if (trustAnchor.getCAName() == null || trustAnchor.getCAPublicKey() == null || !CertPathValidatorUtilities.getEncodedIssuerPrincipal(x509Certificate).equals(new X500Principal(trustAnchor.getCAName()))) {
                    continue;
                }
                list.add(trustAnchor);
            }
        }
        return list;
    }
    
    static {
        QC_STATEMENT = Extension.qCStatements.getId();
        CRL_DIST_POINTS = Extension.cRLDistributionPoints.getId();
        AUTH_INFO_ACCESS = Extension.authorityInfoAccess.getId();
    }
}
