package org.bouncycastle.jce.provider;

import java.security.cert.CertPathValidatorException;
import java.util.Date;
import org.bouncycastle.x509.X509AttributeCertificate;
import org.bouncycastle.jcajce.PKIXCertStoreSelector;
import java.util.List;
import org.bouncycastle.jce.exception.ExtCertPathValidatorException;
import java.util.Set;
import java.security.cert.X509Certificate;
import org.bouncycastle.x509.X509AttributeCertStoreSelector;
import java.security.cert.PKIXParameters;
import java.util.HashSet;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.x509.ExtendedPKIXParameters;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPath;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import java.security.cert.CertPathValidatorSpi;

public class PKIXAttrCertPathValidatorSpi extends CertPathValidatorSpi
{
    private final JcaJceHelper helper;
    
    public PKIXAttrCertPathValidatorSpi() {
        this.helper = new BCJcaJceHelper();
    }
    
    @Override
    public CertPathValidatorResult engineValidate(final CertPath certPath, final CertPathParameters certPathParameters) throws CertPathValidatorException, InvalidAlgorithmParameterException {
        if (!(certPathParameters instanceof ExtendedPKIXParameters) && !(certPathParameters instanceof PKIXExtendedParameters)) {
            throw new InvalidAlgorithmParameterException("Parameters must be a " + ExtendedPKIXParameters.class.getName() + " instance.");
        }
        Set attrCertCheckers = new HashSet();
        Set prohibitedACAttributes = new HashSet();
        Set necessaryACAttributes = new HashSet();
        final HashSet set = new HashSet();
        PKIXExtendedParameters build;
        if (certPathParameters instanceof PKIXParameters) {
            final PKIXExtendedParameters.Builder builder = new PKIXExtendedParameters.Builder((PKIXParameters)certPathParameters);
            if (certPathParameters instanceof ExtendedPKIXParameters) {
                final ExtendedPKIXParameters extendedPKIXParameters = (ExtendedPKIXParameters)certPathParameters;
                builder.setUseDeltasEnabled(extendedPKIXParameters.isUseDeltasEnabled());
                builder.setValidityModel(extendedPKIXParameters.getValidityModel());
                attrCertCheckers = extendedPKIXParameters.getAttrCertCheckers();
                prohibitedACAttributes = extendedPKIXParameters.getProhibitedACAttributes();
                necessaryACAttributes = extendedPKIXParameters.getNecessaryACAttributes();
            }
            build = builder.build();
        }
        else {
            build = (PKIXExtendedParameters)certPathParameters;
        }
        final PKIXCertStoreSelector targetConstraints = build.getTargetConstraints();
        if (!(targetConstraints instanceof X509AttributeCertStoreSelector)) {
            throw new InvalidAlgorithmParameterException("TargetConstraints must be an instance of " + X509AttributeCertStoreSelector.class.getName() + " for " + this.getClass().getName() + " class.");
        }
        final X509AttributeCertificate attributeCert = ((X509AttributeCertStoreSelector)targetConstraints).getAttributeCert();
        final CertPath processAttrCert1 = RFC3281CertPathUtilities.processAttrCert1(attributeCert, build);
        final CertPathValidatorResult processAttrCert2 = RFC3281CertPathUtilities.processAttrCert2(certPath, build);
        final X509Certificate x509Certificate = (X509Certificate)certPath.getCertificates().get(0);
        RFC3281CertPathUtilities.processAttrCert3(x509Certificate, build);
        RFC3281CertPathUtilities.processAttrCert4(x509Certificate, set);
        RFC3281CertPathUtilities.processAttrCert5(attributeCert, build);
        RFC3281CertPathUtilities.processAttrCert7(attributeCert, certPath, processAttrCert1, build, attrCertCheckers);
        RFC3281CertPathUtilities.additionalChecks(attributeCert, prohibitedACAttributes, necessaryACAttributes);
        Date validCertDateFromValidityModel;
        try {
            validCertDateFromValidityModel = CertPathValidatorUtilities.getValidCertDateFromValidityModel(build, null, -1);
        }
        catch (final AnnotatedException ex) {
            throw new ExtCertPathValidatorException("Could not get validity date from attribute certificate.", ex);
        }
        RFC3281CertPathUtilities.checkCRLs(attributeCert, build, x509Certificate, validCertDateFromValidityModel, certPath.getCertificates(), this.helper);
        return processAttrCert2;
    }
}
