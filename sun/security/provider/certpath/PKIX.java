package sun.security.provider.certpath;

import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.Certificate;
import java.security.cert.CertStoreException;
import java.util.Comparator;
import java.security.cert.X509CertSelector;
import javax.security.auth.x500.X500Principal;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.security.Timestamp;
import java.security.cert.X509Certificate;
import java.security.cert.TrustAnchor;
import java.security.cert.CertSelector;
import java.util.Set;
import java.util.Date;
import java.security.cert.CertStore;
import java.security.cert.PKIXCertPathChecker;
import java.util.List;
import java.security.cert.PKIXBuilderParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.PKIXParameters;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPath;
import java.security.interfaces.DSAPublicKey;
import java.security.PublicKey;
import sun.security.util.Debug;

class PKIX
{
    private static final Debug debug;
    
    private PKIX() {
    }
    
    static boolean isDSAPublicKeyWithoutParams(final PublicKey publicKey) {
        return publicKey instanceof DSAPublicKey && ((DSAPublicKey)publicKey).getParams() == null;
    }
    
    static ValidatorParams checkParams(final CertPath certPath, final CertPathParameters certPathParameters) throws InvalidAlgorithmParameterException {
        if (!(certPathParameters instanceof PKIXParameters)) {
            throw new InvalidAlgorithmParameterException("inappropriate params, must be an instance of PKIXParameters");
        }
        return new ValidatorParams(certPath, (PKIXParameters)certPathParameters);
    }
    
    static BuilderParams checkBuilderParams(final CertPathParameters certPathParameters) throws InvalidAlgorithmParameterException {
        if (!(certPathParameters instanceof PKIXBuilderParameters)) {
            throw new InvalidAlgorithmParameterException("inappropriate params, must be an instance of PKIXBuilderParameters");
        }
        return new BuilderParams((PKIXBuilderParameters)certPathParameters);
    }
    
    static {
        debug = Debug.getInstance("certpath");
    }
    
    static class ValidatorParams
    {
        private final PKIXParameters params;
        private CertPath certPath;
        private List<PKIXCertPathChecker> checkers;
        private List<CertStore> stores;
        private boolean gotDate;
        private Date date;
        private Set<String> policies;
        private boolean gotConstraints;
        private CertSelector constraints;
        private Set<TrustAnchor> anchors;
        private List<X509Certificate> certs;
        private Timestamp timestamp;
        private String variant;
        
        ValidatorParams(final CertPath certPath, final PKIXParameters pkixParameters) throws InvalidAlgorithmParameterException {
            this(pkixParameters);
            if (!certPath.getType().equals("X.509") && !certPath.getType().equals("X509")) {
                throw new InvalidAlgorithmParameterException("inappropriate CertPath type specified, must be X.509 or X509");
            }
            this.certPath = certPath;
        }
        
        ValidatorParams(final PKIXParameters params) throws InvalidAlgorithmParameterException {
            if (params instanceof PKIXExtendedParameters) {
                this.timestamp = ((PKIXExtendedParameters)params).getTimestamp();
                this.variant = ((PKIXExtendedParameters)params).getVariant();
            }
            this.anchors = params.getTrustAnchors();
            final Iterator<TrustAnchor> iterator = this.anchors.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getNameConstraints() != null) {
                    throw new InvalidAlgorithmParameterException("name constraints in trust anchor not supported");
                }
            }
            this.params = params;
        }
        
        CertPath certPath() {
            return this.certPath;
        }
        
        void setCertPath(final CertPath certPath) {
            this.certPath = certPath;
        }
        
        List<X509Certificate> certificates() {
            if (this.certs == null) {
                if (this.certPath == null) {
                    this.certs = Collections.emptyList();
                }
                else {
                    final ArrayList certs = new ArrayList((Collection<? extends E>)this.certPath.getCertificates());
                    Collections.reverse(certs);
                    this.certs = certs;
                }
            }
            return this.certs;
        }
        
        List<PKIXCertPathChecker> certPathCheckers() {
            if (this.checkers == null) {
                this.checkers = this.params.getCertPathCheckers();
            }
            return this.checkers;
        }
        
        List<CertStore> certStores() {
            if (this.stores == null) {
                this.stores = this.params.getCertStores();
            }
            return this.stores;
        }
        
        Date date() {
            if (!this.gotDate) {
                this.date = this.params.getDate();
                if (this.date == null) {
                    this.date = new Date();
                }
                this.gotDate = true;
            }
            return this.date;
        }
        
        Set<String> initialPolicies() {
            if (this.policies == null) {
                this.policies = this.params.getInitialPolicies();
            }
            return this.policies;
        }
        
        CertSelector targetCertConstraints() {
            if (!this.gotConstraints) {
                this.constraints = this.params.getTargetCertConstraints();
                this.gotConstraints = true;
            }
            return this.constraints;
        }
        
        Set<TrustAnchor> trustAnchors() {
            return this.anchors;
        }
        
        boolean revocationEnabled() {
            return this.params.isRevocationEnabled();
        }
        
        boolean policyMappingInhibited() {
            return this.params.isPolicyMappingInhibited();
        }
        
        boolean explicitPolicyRequired() {
            return this.params.isExplicitPolicyRequired();
        }
        
        boolean policyQualifiersRejected() {
            return this.params.getPolicyQualifiersRejected();
        }
        
        String sigProvider() {
            return this.params.getSigProvider();
        }
        
        boolean anyPolicyInhibited() {
            return this.params.isAnyPolicyInhibited();
        }
        
        PKIXParameters getPKIXParameters() {
            return this.params;
        }
        
        Timestamp timestamp() {
            return this.timestamp;
        }
        
        String variant() {
            return this.variant;
        }
    }
    
    static class BuilderParams extends ValidatorParams
    {
        private PKIXBuilderParameters params;
        private List<CertStore> stores;
        private X500Principal targetSubject;
        
        BuilderParams(final PKIXBuilderParameters pkixBuilderParameters) throws InvalidAlgorithmParameterException {
            super(pkixBuilderParameters);
            this.checkParams(pkixBuilderParameters);
        }
        
        private void checkParams(final PKIXBuilderParameters params) throws InvalidAlgorithmParameterException {
            if (!(this.targetCertConstraints() instanceof X509CertSelector)) {
                throw new InvalidAlgorithmParameterException("the targetCertConstraints parameter must be an X509CertSelector");
            }
            this.params = params;
            this.targetSubject = getTargetSubject(this.certStores(), (X509CertSelector)this.targetCertConstraints());
        }
        
        @Override
        List<CertStore> certStores() {
            if (this.stores == null) {
                Collections.sort(this.stores = new ArrayList<CertStore>(this.params.getCertStores()), new CertStoreComparator());
            }
            return this.stores;
        }
        
        int maxPathLength() {
            return this.params.getMaxPathLength();
        }
        
        PKIXBuilderParameters params() {
            return this.params;
        }
        
        X500Principal targetSubject() {
            return this.targetSubject;
        }
        
        private static X500Principal getTargetSubject(final List<CertStore> list, final X509CertSelector x509CertSelector) throws InvalidAlgorithmParameterException {
            X500Principal x500Principal = x509CertSelector.getSubject();
            if (x500Principal != null) {
                return x500Principal;
            }
            final X509Certificate certificate = x509CertSelector.getCertificate();
            if (certificate != null) {
                x500Principal = certificate.getSubjectX500Principal();
            }
            if (x500Principal != null) {
                return x500Principal;
            }
            for (final CertStore certStore : list) {
                try {
                    final Collection<? extends Certificate> certificates = certStore.getCertificates(x509CertSelector);
                    if (!certificates.isEmpty()) {
                        return certificates.iterator().next().getSubjectX500Principal();
                    }
                    continue;
                }
                catch (final CertStoreException ex) {
                    if (PKIX.debug == null) {
                        continue;
                    }
                    PKIX.debug.println("BuilderParams.getTargetSubjectDN: non-fatal exception retrieving certs: " + ex);
                    ex.printStackTrace();
                }
            }
            throw new InvalidAlgorithmParameterException("Could not determine unique target subject");
        }
    }
    
    static class CertStoreTypeException extends CertStoreException
    {
        private static final long serialVersionUID = 7463352639238322556L;
        private final String type;
        
        CertStoreTypeException(final String type, final CertStoreException ex) {
            super(ex.getMessage(), ex.getCause());
            this.type = type;
        }
        
        String getType() {
            return this.type;
        }
    }
    
    private static class CertStoreComparator implements Comparator<CertStore>
    {
        @Override
        public int compare(final CertStore certStore, final CertStore certStore2) {
            if (certStore.getType().equals("Collection") || certStore.getCertStoreParameters() instanceof CollectionCertStoreParameters) {
                return -1;
            }
            return 1;
        }
    }
}
