package org.bouncycastle.jce.provider;

import java.security.cert.CertPath;
import java.util.HashSet;
import java.security.cert.CertificateParsingException;
import org.bouncycastle.asn1.x509.Extension;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXCertPathValidatorResult;
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import java.util.Collection;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import java.util.Iterator;
import java.security.cert.CertPathBuilderException;
import org.bouncycastle.jce.exception.ExtCertPathBuilderException;
import java.util.List;
import java.util.ArrayList;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.X509Certificate;
import java.util.Set;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXCertStore;
import org.bouncycastle.x509.ExtendedPKIXBuilderParameters;
import org.bouncycastle.x509.ExtendedPKIXParameters;
import java.security.cert.PKIXParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathBuilderSpi;

public class PKIXCertPathBuilderSpi extends CertPathBuilderSpi
{
    private Exception certPathException;
    
    @Override
    public CertPathBuilderResult engineBuild(final CertPathParameters certPathParameters) throws CertPathBuilderException, InvalidAlgorithmParameterException {
        PKIXExtendedBuilderParameters build;
        if (certPathParameters instanceof PKIXBuilderParameters) {
            final PKIXExtendedParameters.Builder builder = new PKIXExtendedParameters.Builder((PKIXParameters)certPathParameters);
            PKIXExtendedBuilderParameters.Builder builder2;
            if (certPathParameters instanceof ExtendedPKIXParameters) {
                final ExtendedPKIXBuilderParameters extendedPKIXBuilderParameters = (ExtendedPKIXBuilderParameters)certPathParameters;
                final Iterator iterator = extendedPKIXBuilderParameters.getAdditionalStores().iterator();
                while (iterator.hasNext()) {
                    builder.addCertificateStore((PKIXCertStore)iterator.next());
                }
                builder2 = new PKIXExtendedBuilderParameters.Builder(builder.build());
                builder2.addExcludedCerts(extendedPKIXBuilderParameters.getExcludedCerts());
                builder2.setMaxPathLength(extendedPKIXBuilderParameters.getMaxPathLength());
            }
            else {
                builder2 = new PKIXExtendedBuilderParameters.Builder((PKIXBuilderParameters)certPathParameters);
            }
            build = builder2.build();
        }
        else {
            if (!(certPathParameters instanceof PKIXExtendedBuilderParameters)) {
                throw new InvalidAlgorithmParameterException("Parameters must be an instance of " + PKIXBuilderParameters.class.getName() + " or " + PKIXExtendedBuilderParameters.class.getName() + ".");
            }
            build = (PKIXExtendedBuilderParameters)certPathParameters;
        }
        final ArrayList list = new ArrayList();
        final PKIXCertStoreSelector targetConstraints = build.getBaseParameters().getTargetConstraints();
        Collection certificates;
        try {
            certificates = CertPathValidatorUtilities.findCertificates(targetConstraints, build.getBaseParameters().getCertificateStores());
            certificates.addAll(CertPathValidatorUtilities.findCertificates(targetConstraints, build.getBaseParameters().getCertStores()));
        }
        catch (final AnnotatedException ex) {
            throw new ExtCertPathBuilderException("Error finding target certificate.", ex);
        }
        if (certificates.isEmpty()) {
            throw new CertPathBuilderException("No certificate found matching targetContraints.");
        }
        CertPathBuilderResult build2 = null;
        for (Iterator iterator2 = certificates.iterator(); iterator2.hasNext() && build2 == null; build2 = this.build((X509Certificate)iterator2.next(), build, list)) {}
        if (build2 == null && this.certPathException != null) {
            if (this.certPathException instanceof AnnotatedException) {
                throw new CertPathBuilderException(this.certPathException.getMessage(), this.certPathException.getCause());
            }
            throw new CertPathBuilderException("Possible certificate chain could not be validated.", this.certPathException);
        }
        else {
            if (build2 == null && this.certPathException == null) {
                throw new CertPathBuilderException("Unable to find certificate chain.");
            }
            return build2;
        }
    }
    
    protected CertPathBuilderResult build(final X509Certificate x509Certificate, final PKIXExtendedBuilderParameters pkixExtendedBuilderParameters, final List list) {
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
        CertificateFactory certificateFactory;
        PKIXCertPathValidatorSpi pkixCertPathValidatorSpi;
        try {
            certificateFactory = new CertificateFactory();
            pkixCertPathValidatorSpi = new PKIXCertPathValidatorSpi();
        }
        catch (final Exception ex) {
            throw new RuntimeException("Exception creating support classes.");
        }
        try {
            if (CertPathValidatorUtilities.isIssuerTrustAnchor(x509Certificate, pkixExtendedBuilderParameters.getBaseParameters().getTrustAnchors(), pkixExtendedBuilderParameters.getBaseParameters().getSigProvider())) {
                CertPath engineGenerateCertPath;
                try {
                    engineGenerateCertPath = certificateFactory.engineGenerateCertPath(list);
                }
                catch (final Exception ex2) {
                    throw new AnnotatedException("Certification path could not be constructed from certificate list.", ex2);
                }
                PKIXCertPathValidatorResult pkixCertPathValidatorResult;
                try {
                    pkixCertPathValidatorResult = (PKIXCertPathValidatorResult)pkixCertPathValidatorSpi.engineValidate(engineGenerateCertPath, pkixExtendedBuilderParameters);
                }
                catch (final Exception ex3) {
                    throw new AnnotatedException("Certification path could not be validated.", ex3);
                }
                return new PKIXCertPathBuilderResult(engineGenerateCertPath, pkixCertPathValidatorResult.getTrustAnchor(), pkixCertPathValidatorResult.getPolicyTree(), pkixCertPathValidatorResult.getPublicKey());
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
            for (Iterator iterator = set.iterator(); iterator.hasNext() && build == null; build = this.build((X509Certificate)iterator.next(), pkixExtendedBuilderParameters, list)) {}
        }
        catch (final AnnotatedException certPathException) {
            this.certPathException = certPathException;
        }
        if (build == null) {
            list.remove(x509Certificate);
        }
        return build;
    }
}
