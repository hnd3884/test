package sun.security.validator;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetBooleanAction;
import java.security.NoSuchAlgorithmException;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.CertPathBuilder;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509CertSelector;
import java.security.GeneralSecurityException;
import java.security.cert.CertPathParameters;
import java.security.cert.PKIXCertPathValidatorResult;
import java.util.Arrays;
import java.security.cert.CertPathValidator;
import java.util.Date;
import java.security.cert.Certificate;
import java.security.cert.CertPath;
import java.util.Collections;
import java.security.cert.PKIXCertPathChecker;
import sun.security.provider.certpath.AlgorithmChecker;
import sun.security.provider.certpath.PKIXExtendedParameters;
import java.security.Timestamp;
import java.security.AlgorithmConstraints;
import java.util.Iterator;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertSelector;
import java.security.cert.TrustAnchor;
import java.util.HashSet;
import java.util.Collection;
import sun.security.util.SecurityProperties;
import java.security.cert.CertificateFactory;
import java.security.PublicKey;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import java.util.Map;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509Certificate;
import java.util.Set;

public final class PKIXValidator extends Validator
{
    private static final boolean checkTLSRevocation;
    private static final boolean TRY_VALIDATOR = true;
    private static final boolean ALLOW_NON_CA_ANCHOR;
    private final Set<X509Certificate> trustedCerts;
    private final PKIXBuilderParameters parameterTemplate;
    private int certPathLength;
    private final Map<X500Principal, List<PublicKey>> trustedSubjects;
    private final CertificateFactory factory;
    private final boolean plugin;
    
    private static boolean allowNonCaAnchor() {
        final String privilegedGetOverridable = SecurityProperties.privilegedGetOverridable("jdk.security.allowNonCaAnchor");
        return privilegedGetOverridable != null && (privilegedGetOverridable.isEmpty() || privilegedGetOverridable.equalsIgnoreCase("true"));
    }
    
    PKIXValidator(final String defaultParameters, final Collection<X509Certificate> collection) {
        super("PKIX", defaultParameters);
        this.certPathLength = -1;
        if (collection instanceof Set) {
            this.trustedCerts = (Set<X509Certificate>)collection;
        }
        else {
            this.trustedCerts = new HashSet<X509Certificate>(collection);
        }
        final HashSet set = new HashSet();
        final Iterator<Object> iterator = (Iterator<Object>)collection.iterator();
        while (iterator.hasNext()) {
            set.add(new TrustAnchor(iterator.next(), null));
        }
        try {
            this.parameterTemplate = new PKIXBuilderParameters(set, null);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new RuntimeException("Unexpected error: " + ex.toString(), ex);
        }
        this.setDefaultParameters(defaultParameters);
        this.trustedSubjects = new HashMap<X500Principal, List<PublicKey>>();
        for (final X509Certificate x509Certificate : collection) {
            final X500Principal subjectX500Principal = x509Certificate.getSubjectX500Principal();
            List<PublicKey> list;
            if (this.trustedSubjects.containsKey(subjectX500Principal)) {
                list = this.trustedSubjects.get(subjectX500Principal);
            }
            else {
                list = new ArrayList<PublicKey>();
                this.trustedSubjects.put(subjectX500Principal, list);
            }
            list.add(x509Certificate.getPublicKey());
        }
        try {
            this.factory = CertificateFactory.getInstance("X.509");
        }
        catch (final CertificateException ex2) {
            throw new RuntimeException("Internal error", ex2);
        }
        this.plugin = defaultParameters.equals("plugin code signing");
    }
    
    PKIXValidator(final String s, final PKIXBuilderParameters parameterTemplate) {
        super("PKIX", s);
        this.certPathLength = -1;
        this.trustedCerts = new HashSet<X509Certificate>();
        final Iterator<TrustAnchor> iterator = parameterTemplate.getTrustAnchors().iterator();
        while (iterator.hasNext()) {
            final X509Certificate trustedCert = iterator.next().getTrustedCert();
            if (trustedCert != null) {
                this.trustedCerts.add(trustedCert);
            }
        }
        this.parameterTemplate = parameterTemplate;
        this.trustedSubjects = new HashMap<X500Principal, List<PublicKey>>();
        for (final X509Certificate x509Certificate : this.trustedCerts) {
            final X500Principal subjectX500Principal = x509Certificate.getSubjectX500Principal();
            List<PublicKey> list;
            if (this.trustedSubjects.containsKey(subjectX500Principal)) {
                list = this.trustedSubjects.get(subjectX500Principal);
            }
            else {
                list = new ArrayList<PublicKey>();
                this.trustedSubjects.put(subjectX500Principal, list);
            }
            list.add(x509Certificate.getPublicKey());
        }
        try {
            this.factory = CertificateFactory.getInstance("X.509");
        }
        catch (final CertificateException ex) {
            throw new RuntimeException("Internal error", ex);
        }
        this.plugin = s.equals("plugin code signing");
    }
    
    @Override
    public Collection<X509Certificate> getTrustedCertificates() {
        return this.trustedCerts;
    }
    
    public int getCertPathLength() {
        return this.certPathLength;
    }
    
    private void setDefaultParameters(final String s) {
        if (s == "tls server" || s == "tls client") {
            this.parameterTemplate.setRevocationEnabled(PKIXValidator.checkTLSRevocation);
        }
        else {
            this.parameterTemplate.setRevocationEnabled(false);
        }
    }
    
    public PKIXBuilderParameters getParameters() {
        return this.parameterTemplate;
    }
    
    @Override
    X509Certificate[] engineValidate(final X509Certificate[] array, final Collection<X509Certificate> collection, final List<byte[]> list, final AlgorithmConstraints algorithmConstraints, final Object o) throws CertificateException {
        if (array == null || array.length == 0) {
            throw new CertificateException("null or zero-length certificate chain");
        }
        PKIXBuilderParameters pkixBuilderParameters = null;
        try {
            pkixBuilderParameters = new PKIXExtendedParameters((PKIXBuilderParameters)this.parameterTemplate.clone(), (o instanceof Timestamp) ? ((Timestamp)o) : null, this.variant);
        }
        catch (final InvalidAlgorithmParameterException ex) {}
        if (algorithmConstraints != null) {
            pkixBuilderParameters.addCertPathChecker(new AlgorithmChecker(algorithmConstraints, null, this.variant));
        }
        if (!list.isEmpty()) {
            addResponses(pkixBuilderParameters, array, list);
        }
        Object issuerX500Principal = null;
        for (int i = 0; i < array.length; ++i) {
            final X509Certificate x509Certificate = array[i];
            final X500Principal subjectX500Principal = x509Certificate.getSubjectX500Principal();
            if (i == 0) {
                if (this.trustedCerts.contains(x509Certificate)) {
                    return new X509Certificate[] { array[0] };
                }
            }
            else {
                if (!subjectX500Principal.equals(issuerX500Principal)) {
                    return this.doBuild(array, collection, pkixBuilderParameters);
                }
                if (this.trustedCerts.contains(x509Certificate) || (this.trustedSubjects.containsKey(subjectX500Principal) && this.trustedSubjects.get(subjectX500Principal).contains(x509Certificate.getPublicKey()))) {
                    final X509Certificate[] array2 = new X509Certificate[i];
                    System.arraycopy(array, 0, array2, 0, i);
                    return this.doValidate(array2, pkixBuilderParameters);
                }
            }
            issuerX500Principal = x509Certificate.getIssuerX500Principal();
        }
        final X509Certificate x509Certificate2 = array[array.length - 1];
        final X500Principal issuerX500Principal2 = x509Certificate2.getIssuerX500Principal();
        x509Certificate2.getSubjectX500Principal();
        if (this.trustedSubjects.containsKey(issuerX500Principal2) && this.isSignatureValid(this.trustedSubjects.get(issuerX500Principal2), x509Certificate2)) {
            return this.doValidate(array, pkixBuilderParameters);
        }
        if (this.plugin) {
            if (array.length > 1) {
                final X509Certificate[] array3 = new X509Certificate[array.length - 1];
                System.arraycopy(array, 0, array3, 0, array3.length);
                try {
                    pkixBuilderParameters.setTrustAnchors(Collections.singleton(new TrustAnchor(array[array.length - 1], null)));
                }
                catch (final InvalidAlgorithmParameterException ex2) {
                    throw new CertificateException(ex2);
                }
                this.doValidate(array3, pkixBuilderParameters);
            }
            throw new ValidatorException(ValidatorException.T_NO_TRUST_ANCHOR);
        }
        return this.doBuild(array, collection, pkixBuilderParameters);
    }
    
    private boolean isSignatureValid(final List<PublicKey> list, final X509Certificate x509Certificate) {
        if (this.plugin) {
            for (final PublicKey publicKey : list) {
                try {
                    x509Certificate.verify(publicKey);
                    return true;
                }
                catch (final Exception ex) {
                    continue;
                }
                break;
            }
            return false;
        }
        return true;
    }
    
    private static X509Certificate[] toArray(final CertPath certPath, final TrustAnchor trustAnchor) throws CertificateException {
        final X509Certificate trustedCert = trustAnchor.getTrustedCert();
        if (trustedCert == null) {
            throw new ValidatorException("TrustAnchor must be specified as certificate");
        }
        verifyTrustAnchor(trustedCert);
        final List<? extends Certificate> certificates = certPath.getCertificates();
        final X509Certificate[] array = new X509Certificate[certificates.size() + 1];
        certificates.toArray(array);
        array[array.length - 1] = trustedCert;
        return array;
    }
    
    private void setDate(final PKIXBuilderParameters pkixBuilderParameters) {
        final Date validationDate = this.validationDate;
        if (validationDate != null) {
            pkixBuilderParameters.setDate(validationDate);
        }
    }
    
    private X509Certificate[] doValidate(final X509Certificate[] array, final PKIXBuilderParameters date) throws CertificateException {
        try {
            this.setDate(date);
            final CertPathValidator instance = CertPathValidator.getInstance("PKIX");
            final CertPath generateCertPath = this.factory.generateCertPath(Arrays.asList(array));
            this.certPathLength = array.length;
            return toArray(generateCertPath, ((PKIXCertPathValidatorResult)instance.validate(generateCertPath, date)).getTrustAnchor());
        }
        catch (final GeneralSecurityException ex) {
            throw new ValidatorException("PKIX path validation failed: " + ex.toString(), ex);
        }
    }
    
    private static void verifyTrustAnchor(final X509Certificate x509Certificate) throws ValidatorException {
        if (PKIXValidator.ALLOW_NON_CA_ANCHOR) {
            return;
        }
        if (x509Certificate.getVersion() < 3) {
            return;
        }
        if (x509Certificate.getBasicConstraints() == -1) {
            throw new ValidatorException("TrustAnchor with subject \"" + x509Certificate.getSubjectX500Principal() + "\" is not a CA certificate");
        }
        final boolean[] keyUsage = x509Certificate.getKeyUsage();
        if (keyUsage != null && !keyUsage[5]) {
            throw new ValidatorException("TrustAnchor with subject \"" + x509Certificate.getSubjectX500Principal() + "\" does not have keyCertSign bit set in KeyUsage extension");
        }
    }
    
    private X509Certificate[] doBuild(final X509Certificate[] array, final Collection<X509Certificate> collection, final PKIXBuilderParameters date) throws CertificateException {
        try {
            this.setDate(date);
            final X509CertSelector targetCertConstraints = new X509CertSelector();
            targetCertConstraints.setCertificate(array[0]);
            date.setTargetCertConstraints(targetCertConstraints);
            final ArrayList list = new ArrayList();
            list.addAll(Arrays.asList(array));
            if (collection != null) {
                list.addAll(collection);
            }
            date.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(list)));
            final PKIXCertPathBuilderResult pkixCertPathBuilderResult = (PKIXCertPathBuilderResult)CertPathBuilder.getInstance("PKIX").build(date);
            return toArray(pkixCertPathBuilderResult.getCertPath(), pkixCertPathBuilderResult.getTrustAnchor());
        }
        catch (final GeneralSecurityException ex) {
            throw new ValidatorException("PKIX path building failed: " + ex.toString(), ex);
        }
    }
    
    private static void addResponses(final PKIXBuilderParameters pkixBuilderParameters, final X509Certificate[] array, final List<byte[]> list) {
        if (pkixBuilderParameters.isRevocationEnabled()) {
            try {
                PKIXRevocationChecker pkixRevocationChecker = null;
                final ArrayList certPathCheckers = new ArrayList((Collection<? extends E>)pkixBuilderParameters.getCertPathCheckers());
                for (final PKIXCertPathChecker pkixCertPathChecker : certPathCheckers) {
                    if (pkixCertPathChecker instanceof PKIXRevocationChecker) {
                        pkixRevocationChecker = (PKIXRevocationChecker)pkixCertPathChecker;
                        break;
                    }
                }
                if (pkixRevocationChecker == null) {
                    pkixRevocationChecker = (PKIXRevocationChecker)CertPathValidator.getInstance("PKIX").getRevocationChecker();
                    certPathCheckers.add(pkixRevocationChecker);
                }
                final Map<X509Certificate, byte[]> ocspResponses = pkixRevocationChecker.getOcspResponses();
                for (int min = Integer.min(array.length, list.size()), i = 0; i < min; ++i) {
                    final byte[] array2 = list.get(i);
                    if (array2 != null && array2.length > 0 && !ocspResponses.containsKey(array[i])) {
                        ocspResponses.put(array[i], array2);
                    }
                }
                pkixRevocationChecker.setOcspResponses(ocspResponses);
                pkixBuilderParameters.setCertPathCheckers(certPathCheckers);
            }
            catch (final NoSuchAlgorithmException ex) {}
        }
    }
    
    static {
        checkTLSRevocation = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("com.sun.net.ssl.checkRevocation"));
        ALLOW_NON_CA_ANCHOR = allowNonCaAnchor();
    }
}
