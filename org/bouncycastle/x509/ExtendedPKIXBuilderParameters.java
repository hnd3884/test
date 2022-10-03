package org.bouncycastle.x509;

import java.security.cert.X509CertSelector;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXParameters;
import java.security.InvalidParameterException;
import java.security.InvalidAlgorithmParameterException;
import org.bouncycastle.util.Selector;
import java.util.Collection;
import java.util.HashSet;
import java.util.Collections;
import java.util.Set;

public class ExtendedPKIXBuilderParameters extends ExtendedPKIXParameters
{
    private int maxPathLength;
    private Set excludedCerts;
    
    public Set getExcludedCerts() {
        return Collections.unmodifiableSet((Set<?>)this.excludedCerts);
    }
    
    public void setExcludedCerts(Set empty_SET) {
        if (empty_SET == null) {
            empty_SET = Collections.EMPTY_SET;
        }
        else {
            this.excludedCerts = new HashSet(empty_SET);
        }
    }
    
    public ExtendedPKIXBuilderParameters(final Set set, final Selector targetConstraints) throws InvalidAlgorithmParameterException {
        super(set);
        this.maxPathLength = 5;
        this.excludedCerts = Collections.EMPTY_SET;
        this.setTargetConstraints(targetConstraints);
    }
    
    public void setMaxPathLength(final int maxPathLength) {
        if (maxPathLength < -1) {
            throw new InvalidParameterException("The maximum path length parameter can not be less than -1.");
        }
        this.maxPathLength = maxPathLength;
    }
    
    public int getMaxPathLength() {
        return this.maxPathLength;
    }
    
    @Override
    protected void setParams(final PKIXParameters params) {
        super.setParams(params);
        if (params instanceof ExtendedPKIXBuilderParameters) {
            final ExtendedPKIXBuilderParameters extendedPKIXBuilderParameters = (ExtendedPKIXBuilderParameters)params;
            this.maxPathLength = extendedPKIXBuilderParameters.maxPathLength;
            this.excludedCerts = new HashSet(extendedPKIXBuilderParameters.excludedCerts);
        }
        if (params instanceof PKIXBuilderParameters) {
            this.maxPathLength = ((PKIXBuilderParameters)params).getMaxPathLength();
        }
    }
    
    @Override
    public Object clone() {
        ExtendedPKIXBuilderParameters extendedPKIXBuilderParameters;
        try {
            extendedPKIXBuilderParameters = new ExtendedPKIXBuilderParameters(this.getTrustAnchors(), this.getTargetConstraints());
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        extendedPKIXBuilderParameters.setParams(this);
        return extendedPKIXBuilderParameters;
    }
    
    public static ExtendedPKIXParameters getInstance(final PKIXParameters params) {
        ExtendedPKIXBuilderParameters extendedPKIXBuilderParameters;
        try {
            extendedPKIXBuilderParameters = new ExtendedPKIXBuilderParameters(params.getTrustAnchors(), X509CertStoreSelector.getInstance((X509CertSelector)params.getTargetCertConstraints()));
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        extendedPKIXBuilderParameters.setParams(params);
        return extendedPKIXBuilderParameters;
    }
}
