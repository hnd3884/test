package org.bouncycastle.mail.smime.validator;

import org.bouncycastle.asn1.x509.TBSCertificate;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import java.security.cert.TrustAnchor;
import java.util.LinkedHashSet;
import java.util.Collection;
import java.security.cert.CertSelector;
import java.security.cert.X509CertSelector;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.Time;
import org.bouncycastle.asn1.cms.CMSAttributes;
import java.security.PublicKey;
import org.bouncycastle.i18n.filter.UntrustedInput;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.util.Integers;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import java.security.cert.CertificateEncodingException;
import java.io.IOException;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import java.util.HashSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import java.util.Iterator;
import org.bouncycastle.x509.CertPathReviewerException;
import java.security.GeneralSecurityException;
import org.bouncycastle.x509.PKIXCertPathReviewer;
import java.security.cert.CertPath;
import java.util.Set;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateExpiredException;
import org.bouncycastle.i18n.filter.TrustedInput;
import java.util.Date;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import java.security.cert.CertStoreException;
import java.security.cert.X509Certificate;
import java.util.List;
import org.bouncycastle.cms.SignerInformation;
import java.util.ArrayList;
import javax.mail.Address;
import java.util.HashMap;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import org.bouncycastle.cert.jcajce.JcaCertStoreBuilder;
import javax.mail.Part;
import org.bouncycastle.i18n.ErrorBundle;
import org.bouncycastle.mail.smime.SMIMESigned;
import javax.mail.internet.MimeMultipart;
import java.security.cert.PKIXParameters;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import org.bouncycastle.cms.SignerInformationStore;
import java.security.cert.CertStore;
import org.bouncycastle.cms.jcajce.JcaX509CertSelectorConverter;

public class SignedMailValidator
{
    private static final String RESOURCE_NAME = "org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages";
    private static final Class DEFAULT_CERT_PATH_REVIEWER;
    private static final String EXT_KEY_USAGE;
    private static final String SUBJECT_ALTERNATIVE_NAME;
    private static final int shortKeyLength = 512;
    private static final long THIRTY_YEARS_IN_MILLI_SEC = 946728000000L;
    private static final JcaX509CertSelectorConverter selectorConverter;
    private CertStore certs;
    private SignerInformationStore signers;
    private Map results;
    private String[] fromAddresses;
    private Class certPathReviewerClass;
    
    public SignedMailValidator(final MimeMessage mimeMessage, final PKIXParameters pkixParameters) throws SignedMailValidatorException {
        this(mimeMessage, pkixParameters, SignedMailValidator.DEFAULT_CERT_PATH_REVIEWER);
    }
    
    public SignedMailValidator(final MimeMessage mimeMessage, final PKIXParameters pkixParameters, final Class certPathReviewerClass) throws SignedMailValidatorException {
        this.certPathReviewerClass = certPathReviewerClass;
        if (!SignedMailValidator.DEFAULT_CERT_PATH_REVIEWER.isAssignableFrom(certPathReviewerClass)) {
            throw new IllegalArgumentException("certPathReviewerClass is not a subclass of " + SignedMailValidator.DEFAULT_CERT_PATH_REVIEWER.getName());
        }
        try {
            SMIMESigned smimeSigned;
            if (mimeMessage.isMimeType("multipart/signed")) {
                smimeSigned = new SMIMESigned((MimeMultipart)mimeMessage.getContent());
            }
            else {
                if (!mimeMessage.isMimeType("application/pkcs7-mime") && !mimeMessage.isMimeType("application/x-pkcs7-mime")) {
                    throw new SignedMailValidatorException(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.noSignedMessage"));
                }
                smimeSigned = new SMIMESigned((Part)mimeMessage);
            }
            this.certs = new JcaCertStoreBuilder().addCertificates(smimeSigned.getCertificates()).addCRLs(smimeSigned.getCRLs()).setProvider("BC").build();
            this.signers = smimeSigned.getSignerInfos();
            final Address[] from = mimeMessage.getFrom();
            InternetAddress internetAddress = null;
            try {
                if (mimeMessage.getHeader("Sender") != null) {
                    internetAddress = new InternetAddress(mimeMessage.getHeader("Sender")[0]);
                }
            }
            catch (final MessagingException ex) {}
            final int n = (from != null) ? from.length : 0;
            this.fromAddresses = new String[n + ((internetAddress != null) ? 1 : 0)];
            for (int i = 0; i < n; ++i) {
                this.fromAddresses[i] = ((InternetAddress)from[i]).getAddress();
            }
            if (internetAddress != null) {
                this.fromAddresses[n] = internetAddress.getAddress();
            }
            this.results = new HashMap();
        }
        catch (final Exception ex2) {
            if (ex2 instanceof SignedMailValidatorException) {
                throw (SignedMailValidatorException)ex2;
            }
            throw new SignedMailValidatorException(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.exceptionReadingMessage", new Object[] { ex2.getMessage(), ex2, ex2.getClass().getName() }), ex2);
        }
        this.validateSignatures(pkixParameters);
    }
    
    protected void validateSignatures(final PKIXParameters pkixParameters) {
        final PKIXParameters pkixParameters2 = (PKIXParameters)pkixParameters.clone();
        pkixParameters2.addCertStore(this.certs);
        final Iterator iterator = this.signers.getSigners().iterator();
        while (iterator.hasNext()) {
            final ArrayList list = new ArrayList();
            final ArrayList list2 = new ArrayList();
            final SignerInformation signerInformation = (SignerInformation)iterator.next();
            X509Certificate x509Certificate = null;
            try {
                final Iterator iterator2 = findCerts(pkixParameters2.getCertStores(), SignedMailValidator.selectorConverter.getCertSelector(signerInformation.getSID())).iterator();
                if (iterator2.hasNext()) {
                    x509Certificate = (X509Certificate)iterator2.next();
                }
            }
            catch (final CertStoreException ex) {
                list.add(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.exceptionRetrievingSignerCert", new Object[] { ex.getMessage(), ex, ex.getClass().getName() }));
            }
            if (x509Certificate != null) {
                boolean verify = false;
                try {
                    verify = signerInformation.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(x509Certificate.getPublicKey()));
                    if (!verify) {
                        list.add(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.signatureNotVerified"));
                    }
                }
                catch (final Exception ex2) {
                    list.add(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.exceptionVerifyingSignature", new Object[] { ex2.getMessage(), ex2, ex2.getClass().getName() }));
                }
                this.checkSignerCert(x509Certificate, list, list2);
                final AttributeTable signedAttributes = signerInformation.getSignedAttributes();
                if (signedAttributes != null && signedAttributes.get(PKCSObjectIdentifiers.id_aa_receiptRequest) != null) {
                    list2.add(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.signedReceiptRequest"));
                }
                Date date = getSignatureTime(signerInformation);
                if (date == null) {
                    list2.add(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.noSigningTime"));
                    date = pkixParameters.getDate();
                    if (date == null) {
                        date = new Date();
                    }
                }
                else {
                    try {
                        x509Certificate.checkValidity(date);
                    }
                    catch (final CertificateExpiredException ex3) {
                        list.add(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.certExpired", new Object[] { new TrustedInput((Object)date), new TrustedInput((Object)x509Certificate.getNotAfter()) }));
                    }
                    catch (final CertificateNotYetValidException ex4) {
                        list.add(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.certNotYetValid", new Object[] { new TrustedInput((Object)date), new TrustedInput((Object)x509Certificate.getNotBefore()) }));
                    }
                }
                pkixParameters2.setDate(date);
                try {
                    final ArrayList list3 = new ArrayList();
                    list3.add(this.certs);
                    final Object[] certPath = createCertPath(x509Certificate, pkixParameters2.getTrustAnchors(), pkixParameters.getCertStores(), list3);
                    final CertPath certPath2 = (CertPath)certPath[0];
                    final List list4 = (List)certPath[1];
                    PKIXCertPathReviewer pkixCertPathReviewer;
                    try {
                        pkixCertPathReviewer = this.certPathReviewerClass.newInstance();
                    }
                    catch (final IllegalAccessException ex5) {
                        throw new IllegalArgumentException("Cannot instantiate object of type " + this.certPathReviewerClass.getName() + ": " + ex5.getMessage());
                    }
                    catch (final InstantiationException ex6) {
                        throw new IllegalArgumentException("Cannot instantiate object of type " + this.certPathReviewerClass.getName() + ": " + ex6.getMessage());
                    }
                    pkixCertPathReviewer.init(certPath2, pkixParameters2);
                    if (!pkixCertPathReviewer.isValidCertPath()) {
                        list.add(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.certPathInvalid"));
                    }
                    this.results.put(signerInformation, new ValidationResult(pkixCertPathReviewer, verify, list, list2, list4));
                }
                catch (final GeneralSecurityException ex7) {
                    list.add(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.exceptionCreateCertPath", new Object[] { ex7.getMessage(), ex7, ex7.getClass().getName() }));
                    this.results.put(signerInformation, new ValidationResult(null, verify, list, list2, null));
                }
                catch (final CertPathReviewerException ex8) {
                    list.add(ex8.getErrorMessage());
                    this.results.put(signerInformation, new ValidationResult(null, verify, list, list2, null));
                }
            }
            else {
                list.add(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.noSignerCert"));
                this.results.put(signerInformation, new ValidationResult(null, false, list, list2, null));
            }
        }
    }
    
    public static Set getEmailAddresses(final X509Certificate x509Certificate) throws IOException, CertificateEncodingException {
        final HashSet set = new HashSet();
        final RDN[] rdNs = getTBSCert(x509Certificate).getSubject().getRDNs(PKCSObjectIdentifiers.pkcs_9_at_emailAddress);
        for (int i = 0; i < rdNs.length; ++i) {
            final AttributeTypeAndValue[] typesAndValues = rdNs[i].getTypesAndValues();
            for (int j = 0; j != typesAndValues.length; ++j) {
                if (typesAndValues[j].getType().equals((Object)PKCSObjectIdentifiers.pkcs_9_at_emailAddress)) {
                    set.add(((ASN1String)typesAndValues[j].getValue()).getString().toLowerCase());
                }
            }
        }
        final byte[] extensionValue = x509Certificate.getExtensionValue(SignedMailValidator.SUBJECT_ALTERNATIVE_NAME);
        if (extensionValue != null) {
            final ASN1Sequence instance = ASN1Sequence.getInstance((Object)getObject(extensionValue));
            for (int k = 0; k < instance.size(); ++k) {
                final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)instance.getObjectAt(k);
                if (asn1TaggedObject.getTagNo() == 1) {
                    set.add(DERIA5String.getInstance(asn1TaggedObject, false).getString().toLowerCase());
                }
            }
        }
        return set;
    }
    
    private static ASN1Primitive getObject(final byte[] array) throws IOException {
        return new ASN1InputStream(((ASN1OctetString)new ASN1InputStream(array).readObject()).getOctets()).readObject();
    }
    
    protected void checkSignerCert(final X509Certificate x509Certificate, final List list, final List list2) {
        final PublicKey publicKey = x509Certificate.getPublicKey();
        int n = -1;
        if (publicKey instanceof RSAPublicKey) {
            n = ((RSAPublicKey)publicKey).getModulus().bitLength();
        }
        else if (publicKey instanceof DSAPublicKey) {
            n = ((DSAPublicKey)publicKey).getParams().getP().bitLength();
        }
        if (n != -1 && n <= 512) {
            list2.add(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.shortSigningKey", new Object[] { Integers.valueOf(n) }));
        }
        if (x509Certificate.getNotAfter().getTime() - x509Certificate.getNotBefore().getTime() > 946728000000L) {
            list2.add(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.longValidity", new Object[] { new TrustedInput((Object)x509Certificate.getNotBefore()), new TrustedInput((Object)x509Certificate.getNotAfter()) }));
        }
        final boolean[] keyUsage = x509Certificate.getKeyUsage();
        if (keyUsage != null && !keyUsage[0] && !keyUsage[1]) {
            list.add(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.signingNotPermitted"));
        }
        try {
            final byte[] extensionValue = x509Certificate.getExtensionValue(SignedMailValidator.EXT_KEY_USAGE);
            if (extensionValue != null) {
                final ExtendedKeyUsage instance = ExtendedKeyUsage.getInstance((Object)getObject(extensionValue));
                if (!instance.hasKeyPurposeId(KeyPurposeId.anyExtendedKeyUsage) && !instance.hasKeyPurposeId(KeyPurposeId.id_kp_emailProtection)) {
                    list.add(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.extKeyUsageNotPermitted"));
                }
            }
        }
        catch (final Exception ex) {
            list.add(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.extKeyUsageError", new Object[] { ex.getMessage(), ex, ex.getClass().getName() }));
        }
        try {
            final Set emailAddresses = getEmailAddresses(x509Certificate);
            if (emailAddresses.isEmpty()) {
                list.add(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.noEmailInCert"));
            }
            else {
                boolean b = false;
                for (int i = 0; i < this.fromAddresses.length; ++i) {
                    if (emailAddresses.contains(this.fromAddresses[i].toLowerCase())) {
                        b = true;
                        break;
                    }
                }
                if (!b) {
                    list.add(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.emailFromCertMismatch", new Object[] { new UntrustedInput((Object)addressesToString(this.fromAddresses)), new UntrustedInput((Object)emailAddresses) }));
                }
            }
        }
        catch (final Exception ex2) {
            list.add(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.certGetEmailError", new Object[] { ex2.getMessage(), ex2, ex2.getClass().getName() }));
        }
    }
    
    static String addressesToString(final Object[] array) {
        if (array == null) {
            return "null";
        }
        final StringBuffer sb = new StringBuffer();
        sb.append('[');
        for (int i = 0; i != array.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(String.valueOf(array[i]));
        }
        return sb.append(']').toString();
    }
    
    public static Date getSignatureTime(final SignerInformation signerInformation) {
        final AttributeTable signedAttributes = signerInformation.getSignedAttributes();
        Date date = null;
        if (signedAttributes != null) {
            final Attribute value = signedAttributes.get(CMSAttributes.signingTime);
            if (value != null) {
                date = Time.getInstance((Object)value.getAttrValues().getObjectAt(0).toASN1Primitive()).getDate();
            }
        }
        return date;
    }
    
    private static List findCerts(final List list, final X509CertSelector x509CertSelector) throws CertStoreException {
        final ArrayList list2 = new ArrayList();
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            list2.addAll(((CertStore)iterator.next()).getCertificates(x509CertSelector));
        }
        return list2;
    }
    
    private static X509Certificate findNextCert(final List list, final X509CertSelector x509CertSelector, final Set set) throws CertStoreException {
        final Iterator iterator = findCerts(list, x509CertSelector).iterator();
        boolean b = false;
        X509Certificate x509Certificate = null;
        while (iterator.hasNext()) {
            x509Certificate = (X509Certificate)iterator.next();
            if (!set.contains(x509Certificate)) {
                b = true;
                break;
            }
        }
        return b ? x509Certificate : null;
    }
    
    public static CertPath createCertPath(final X509Certificate x509Certificate, final Set set, final List list) throws GeneralSecurityException {
        return (CertPath)createCertPath(x509Certificate, set, list, null)[0];
    }
    
    public static Object[] createCertPath(final X509Certificate x509Certificate, final Set set, final List list, final List list2) throws GeneralSecurityException {
        final LinkedHashSet set2 = new LinkedHashSet();
        final ArrayList list3 = new ArrayList();
        X509Certificate x509Certificate2 = x509Certificate;
        set2.add(x509Certificate2);
        list3.add(new Boolean(true));
        int n = 0;
        X509Certificate x509Certificate3 = null;
        while (x509Certificate2 != null && n == 0) {
            for (final TrustAnchor trustAnchor : set) {
                final X509Certificate trustedCert = trustAnchor.getTrustedCert();
                if (trustedCert != null) {
                    if (!trustedCert.getSubjectX500Principal().equals(x509Certificate2.getIssuerX500Principal())) {
                        continue;
                    }
                    try {
                        x509Certificate2.verify(trustedCert.getPublicKey(), "BC");
                        n = 1;
                        x509Certificate3 = trustedCert;
                        break;
                    }
                    catch (final Exception ex) {
                        continue;
                    }
                }
                if (trustAnchor.getCAName().equals(x509Certificate2.getIssuerX500Principal().getName())) {
                    try {
                        x509Certificate2.verify(trustAnchor.getCAPublicKey(), "BC");
                        n = 1;
                        break;
                    }
                    catch (final Exception ex2) {}
                }
            }
            if (n == 0) {
                final X509CertSelector x509CertSelector = new X509CertSelector();
                try {
                    x509CertSelector.setSubject(x509Certificate2.getIssuerX500Principal().getEncoded());
                }
                catch (final IOException ex3) {
                    throw new IllegalStateException(ex3.toString());
                }
                final byte[] extensionValue = x509Certificate2.getExtensionValue(Extension.authorityKeyIdentifier.getId());
                if (extensionValue != null) {
                    try {
                        final AuthorityKeyIdentifier instance = AuthorityKeyIdentifier.getInstance((Object)getObject(extensionValue));
                        if (instance.getKeyIdentifier() != null) {
                            x509CertSelector.setSubjectKeyIdentifier(new DEROctetString(instance.getKeyIdentifier()).getEncoded("DER"));
                        }
                    }
                    catch (final IOException ex4) {}
                }
                boolean b = false;
                x509Certificate2 = findNextCert(list, x509CertSelector, set2);
                if (x509Certificate2 == null && list2 != null) {
                    b = true;
                    x509Certificate2 = findNextCert(list2, x509CertSelector, set2);
                }
                if (x509Certificate2 == null) {
                    continue;
                }
                set2.add(x509Certificate2);
                list3.add(new Boolean(b));
            }
        }
        if (n != 0) {
            if (x509Certificate3 != null && x509Certificate3.getSubjectX500Principal().equals(x509Certificate3.getIssuerX500Principal())) {
                set2.add(x509Certificate3);
                list3.add(new Boolean(false));
            }
            else {
                final X509CertSelector x509CertSelector2 = new X509CertSelector();
                try {
                    x509CertSelector2.setSubject(x509Certificate2.getIssuerX500Principal().getEncoded());
                    x509CertSelector2.setIssuer(x509Certificate2.getIssuerX500Principal().getEncoded());
                }
                catch (final IOException ex5) {
                    throw new IllegalStateException(ex5.toString());
                }
                boolean b2 = false;
                X509Certificate x509Certificate4 = findNextCert(list, x509CertSelector2, set2);
                if (x509Certificate4 == null && list2 != null) {
                    b2 = true;
                    x509Certificate4 = findNextCert(list2, x509CertSelector2, set2);
                }
                if (x509Certificate4 != null) {
                    try {
                        x509Certificate2.verify(x509Certificate4.getPublicKey(), "BC");
                        set2.add(x509Certificate4);
                        list3.add(new Boolean(b2));
                    }
                    catch (final GeneralSecurityException ex6) {}
                }
            }
        }
        return new Object[] { CertificateFactory.getInstance("X.509", "BC").generateCertPath(new ArrayList<Certificate>(set2)), list3 };
    }
    
    public CertStore getCertsAndCRLs() {
        return this.certs;
    }
    
    public SignerInformationStore getSignerInformationStore() {
        return this.signers;
    }
    
    public ValidationResult getValidationResult(final SignerInformation signerInformation) throws SignedMailValidatorException {
        if (this.signers.getSigners(signerInformation.getSID()).isEmpty()) {
            throw new SignedMailValidatorException(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.wrongSigner"));
        }
        return this.results.get(signerInformation);
    }
    
    private static TBSCertificate getTBSCert(final X509Certificate x509Certificate) throws CertificateEncodingException {
        return TBSCertificate.getInstance((Object)x509Certificate.getTBSCertificate());
    }
    
    static {
        DEFAULT_CERT_PATH_REVIEWER = PKIXCertPathReviewer.class;
        EXT_KEY_USAGE = Extension.extendedKeyUsage.getId();
        SUBJECT_ALTERNATIVE_NAME = Extension.subjectAlternativeName.getId();
        selectorConverter = new JcaX509CertSelectorConverter();
    }
    
    public class ValidationResult
    {
        private PKIXCertPathReviewer review;
        private List errors;
        private List notifications;
        private List userProvidedCerts;
        private boolean signVerified;
        
        ValidationResult(final PKIXCertPathReviewer review, final boolean signVerified, final List errors, final List notifications, final List userProvidedCerts) {
            this.review = review;
            this.errors = errors;
            this.notifications = notifications;
            this.signVerified = signVerified;
            this.userProvidedCerts = userProvidedCerts;
        }
        
        public List getErrors() {
            return this.errors;
        }
        
        public List getNotifications() {
            return this.notifications;
        }
        
        public PKIXCertPathReviewer getCertPathReview() {
            return this.review;
        }
        
        public CertPath getCertPath() {
            return (this.review != null) ? this.review.getCertPath() : null;
        }
        
        public List getUserProvidedCerts() {
            return this.userProvidedCerts;
        }
        
        public boolean isVerifiedSignature() {
            return this.signVerified;
        }
        
        public boolean isValidSignature() {
            return this.review != null && this.signVerified && this.review.isValidCertPath() && this.errors.isEmpty();
        }
    }
}
