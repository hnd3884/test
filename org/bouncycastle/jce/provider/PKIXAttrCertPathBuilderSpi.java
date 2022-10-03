package org.bouncycastle.jce.provider;

import org.bouncycastle.util.StoreException;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import java.security.cert.CertPath;
import org.bouncycastle.jcajce.PKIXCertStore;
import java.security.cert.CertificateParsingException;
import org.bouncycastle.asn1.x509.Extension;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateFactory;
import java.security.cert.Certificate;
import java.security.Principal;
import java.util.Iterator;
import java.util.Collection;
import java.io.IOException;
import java.util.List;
import java.security.cert.CertSelector;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import javax.security.auth.x500.X500Principal;
import java.util.HashSet;
import org.bouncycastle.x509.X509CertStoreSelector;
import org.bouncycastle.x509.X509AttributeCertificate;
import org.bouncycastle.jce.exception.ExtCertPathBuilderException;
import java.security.cert.CertPathBuilderException;
import org.bouncycastle.x509.X509AttributeCertStoreSelector;
import java.security.cert.X509Certificate;
import java.util.Set;
import org.bouncycastle.x509.ExtendedPKIXParameters;
import java.util.ArrayList;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.x509.ExtendedPKIXBuilderParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathBuilderSpi;

public class PKIXAttrCertPathBuilderSpi extends CertPathBuilderSpi
{
    private Exception certPathException;
    
    @Override
    public CertPathBuilderResult engineBuild(final CertPathParameters certPathParameters) throws CertPathBuilderException, InvalidAlgorithmParameterException {
        if (!(certPathParameters instanceof PKIXBuilderParameters) && !(certPathParameters instanceof ExtendedPKIXBuilderParameters) && !(certPathParameters instanceof PKIXExtendedBuilderParameters)) {
            throw new InvalidAlgorithmParameterException("Parameters must be an instance of " + PKIXBuilderParameters.class.getName() + " or " + PKIXExtendedBuilderParameters.class.getName() + ".");
        }
        List stores = new ArrayList();
        PKIXExtendedBuilderParameters build;
        if (certPathParameters instanceof PKIXBuilderParameters) {
            final PKIXExtendedBuilderParameters.Builder builder = new PKIXExtendedBuilderParameters.Builder((PKIXBuilderParameters)certPathParameters);
            if (certPathParameters instanceof ExtendedPKIXParameters) {
                final ExtendedPKIXBuilderParameters extendedPKIXBuilderParameters = (ExtendedPKIXBuilderParameters)certPathParameters;
                builder.addExcludedCerts(extendedPKIXBuilderParameters.getExcludedCerts());
                builder.setMaxPathLength(extendedPKIXBuilderParameters.getMaxPathLength());
                stores = extendedPKIXBuilderParameters.getStores();
            }
            build = builder.build();
        }
        else {
            build = (PKIXExtendedBuilderParameters)certPathParameters;
        }
        final ArrayList list = new ArrayList();
        final PKIXCertStoreSelector targetConstraints = build.getBaseParameters().getTargetConstraints();
        if (!(targetConstraints instanceof X509AttributeCertStoreSelector)) {
            throw new CertPathBuilderException("TargetConstraints must be an instance of " + X509AttributeCertStoreSelector.class.getName() + " for " + this.getClass().getName() + " class.");
        }
        Collection certificates;
        try {
            certificates = findCertificates((X509AttributeCertStoreSelector)targetConstraints, stores);
        }
        catch (final AnnotatedException ex) {
            throw new ExtCertPathBuilderException("Error finding target attribute certificate.", ex);
        }
        if (certificates.isEmpty()) {
            throw new CertPathBuilderException("No attribute certificate found matching targetContraints.");
        }
        CertPathBuilderResult build2 = null;
        final Iterator iterator = certificates.iterator();
        while (iterator.hasNext() && build2 == null) {
            final X509AttributeCertificate x509AttributeCertificate = (X509AttributeCertificate)iterator.next();
            final X509CertStoreSelector x509CertStoreSelector = new X509CertStoreSelector();
            final Principal[] principals = x509AttributeCertificate.getIssuer().getPrincipals();
            final HashSet set = new HashSet();
            for (int i = 0; i < principals.length; ++i) {
                try {
                    if (principals[i] instanceof X500Principal) {
                        x509CertStoreSelector.setSubject(((X500Principal)principals[i]).getEncoded());
                    }
                    final PKIXCertStoreSelector<? extends Certificate> build3 = new PKIXCertStoreSelector.Builder(x509CertStoreSelector).build();
                    set.addAll(CertPathValidatorUtilities.findCertificates(build3, build.getBaseParameters().getCertStores()));
                    set.addAll(CertPathValidatorUtilities.findCertificates(build3, build.getBaseParameters().getCertificateStores()));
                }
                catch (final AnnotatedException ex2) {
                    throw new ExtCertPathBuilderException("Public key certificate for attribute certificate cannot be searched.", ex2);
                }
                catch (final IOException ex3) {
                    throw new ExtCertPathBuilderException("cannot encode X500Principal.", ex3);
                }
            }
            if (set.isEmpty()) {
                throw new CertPathBuilderException("Public key certificate for attribute certificate cannot be found.");
            }
            for (Iterator iterator2 = set.iterator(); iterator2.hasNext() && build2 == null; build2 = this.build(x509AttributeCertificate, (X509Certificate)iterator2.next(), build, list)) {}
        }
        if (build2 == null && this.certPathException != null) {
            throw new ExtCertPathBuilderException("Possible certificate chain could not be validated.", this.certPathException);
        }
        if (build2 == null && this.certPathException == null) {
            throw new CertPathBuilderException("Unable to find certificate chain.");
        }
        return build2;
    }
    
    private CertPathBuilderResult build(final X509AttributeCertificate x509AttributeCertificate, final X509Certificate x509Certificate, final PKIXExtendedBuilderParameters pkixExtendedBuilderParameters, final List list) {
        if (list.contains(x509Certificate)) {
            return null;
        }
        if (pkixExtendedBuilderParameters.getExcludedCerts().contains(x509Certificate)) {
            return null;
        }
        if (pkixExtendedBuilderParameters.getMaxPathLength() != -1 && list.size() - 1 > pkixExtendedBuilderParameters.getMaxPathLength()) {
            return null;
        }
        list.add(x509Certificate);
        CertPathBuilderResult build = null;
        CertificateFactory instance;
        CertPathValidator instance2;
        try {
            instance = CertificateFactory.getInstance("X.509", "BC");
            instance2 = CertPathValidator.getInstance("RFC3281", "BC");
        }
        catch (final Exception ex) {
            throw new RuntimeException("Exception creating support classes.");
        }
        try {
            if (CertPathValidatorUtilities.isIssuerTrustAnchor(x509Certificate, pkixExtendedBuilderParameters.getBaseParameters().getTrustAnchors(), pkixExtendedBuilderParameters.getBaseParameters().getSigProvider())) {
                CertPath generateCertPath;
                try {
                    generateCertPath = instance.generateCertPath(list);
                }
                catch (final Exception ex2) {
                    throw new AnnotatedException("Certification path could not be constructed from certificate list.", ex2);
                }
                PKIXCertPathValidatorResult pkixCertPathValidatorResult;
                try {
                    pkixCertPathValidatorResult = (PKIXCertPathValidatorResult)instance2.validate(generateCertPath, pkixExtendedBuilderParameters);
                }
                catch (final Exception ex3) {
                    throw new AnnotatedException("Certification path could not be validated.", ex3);
                }
                return new PKIXCertPathBuilderResult(generateCertPath, pkixCertPathValidatorResult.getTrustAnchor(), pkixCertPathValidatorResult.getPolicyTree(), pkixCertPathValidatorResult.getPublicKey());
            }
            final ArrayList list2 = new ArrayList();
            list2.addAll(pkixExtendedBuilderParameters.getBaseParameters().getCertificateStores());
            try {
                list2.addAll(CertPathValidatorUtilities.getAdditionalStoresFromAltNames(x509Certificate.getExtensionValue(Extension.issuerAlternativeName.getId()), pkixExtendedBuilderParameters.getBaseParameters().getNamedCertificateStoreMap()));
            }
            catch (final CertificateParsingException ex4) {
                throw new AnnotatedException("No additional X.509 stores can be added from certificate locations.", ex4);
            }
            final HashSet set = new HashSet();
            try {
                set.addAll(CertPathValidatorUtilities.findIssuerCerts(x509Certificate, pkixExtendedBuilderParameters.getBaseParameters().getCertStores(), list2));
            }
            catch (final AnnotatedException ex5) {
                throw new AnnotatedException("Cannot find issuer certificate for certificate in certification path.", ex5);
            }
            if (set.isEmpty()) {
                throw new AnnotatedException("No issuer certificate for certificate in certification path found.");
            }
            final Iterator iterator = set.iterator();
            while (iterator.hasNext() && build == null) {
                final X509Certificate x509Certificate2 = (X509Certificate)iterator.next();
                if (x509Certificate2.getIssuerX500Principal().equals(x509Certificate2.getSubjectX500Principal())) {
                    continue;
                }
                build = this.build(x509AttributeCertificate, x509Certificate2, pkixExtendedBuilderParameters, list);
            }
        }
        catch (final AnnotatedException ex6) {
            this.certPathException = new AnnotatedException("No valid certification path could be build.", ex6);
        }
        if (build == null) {
            list.remove(x509Certificate);
        }
        return build;
    }
    
    protected static Collection findCertificates(final X509AttributeCertStoreSelector x509AttributeCertStoreSelector, final List list) throws AnnotatedException {
        final HashSet set = new HashSet();
        for (final Object next : list) {
            if (next instanceof Store) {
                final Store store = (Store)next;
                try {
                    set.addAll(store.getMatches(x509AttributeCertStoreSelector));
                }
                catch (final StoreException ex) {
                    throw new AnnotatedException("Problem while picking certificates from X.509 store.", ex);
                }
            }
        }
        return set;
    }
}
