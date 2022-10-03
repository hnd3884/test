package sun.security.provider.certpath;

import java.security.cert.CertStore;
import java.util.List;
import java.security.cert.TrustAnchor;
import java.util.Set;
import java.security.cert.PKIXCertPathChecker;
import java.util.Date;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertSelector;
import java.security.Timestamp;
import java.security.cert.PKIXBuilderParameters;

public class PKIXTimestampParameters extends PKIXBuilderParameters
{
    private final PKIXBuilderParameters p;
    private Timestamp jarTimestamp;
    
    public PKIXTimestampParameters(final PKIXBuilderParameters p2, final Timestamp jarTimestamp) throws InvalidAlgorithmParameterException {
        super(p2.getTrustAnchors(), null);
        this.p = p2;
        this.jarTimestamp = jarTimestamp;
    }
    
    public Timestamp getTimestamp() {
        return this.jarTimestamp;
    }
    
    public void setTimestamp(final Timestamp jarTimestamp) {
        this.jarTimestamp = jarTimestamp;
    }
    
    @Override
    public void setDate(final Date date) {
        this.p.setDate(date);
    }
    
    @Override
    public void addCertPathChecker(final PKIXCertPathChecker pkixCertPathChecker) {
        this.p.addCertPathChecker(pkixCertPathChecker);
    }
    
    @Override
    public void setMaxPathLength(final int maxPathLength) {
        this.p.setMaxPathLength(maxPathLength);
    }
    
    @Override
    public int getMaxPathLength() {
        return this.p.getMaxPathLength();
    }
    
    @Override
    public String toString() {
        return this.p.toString();
    }
    
    @Override
    public Set<TrustAnchor> getTrustAnchors() {
        return this.p.getTrustAnchors();
    }
    
    @Override
    public void setTrustAnchors(final Set<TrustAnchor> trustAnchors) throws InvalidAlgorithmParameterException {
        if (this.p == null) {
            return;
        }
        this.p.setTrustAnchors(trustAnchors);
    }
    
    @Override
    public Set<String> getInitialPolicies() {
        return this.p.getInitialPolicies();
    }
    
    @Override
    public void setInitialPolicies(final Set<String> initialPolicies) {
        this.p.setInitialPolicies(initialPolicies);
    }
    
    @Override
    public void setCertStores(final List<CertStore> certStores) {
        this.p.setCertStores(certStores);
    }
    
    @Override
    public void addCertStore(final CertStore certStore) {
        this.p.addCertStore(certStore);
    }
    
    @Override
    public List<CertStore> getCertStores() {
        return this.p.getCertStores();
    }
    
    @Override
    public void setRevocationEnabled(final boolean revocationEnabled) {
        this.p.setRevocationEnabled(revocationEnabled);
    }
    
    @Override
    public boolean isRevocationEnabled() {
        return this.p.isRevocationEnabled();
    }
    
    @Override
    public void setExplicitPolicyRequired(final boolean explicitPolicyRequired) {
        this.p.setExplicitPolicyRequired(explicitPolicyRequired);
    }
    
    @Override
    public boolean isExplicitPolicyRequired() {
        return this.p.isExplicitPolicyRequired();
    }
    
    @Override
    public void setPolicyMappingInhibited(final boolean policyMappingInhibited) {
        this.p.setPolicyMappingInhibited(policyMappingInhibited);
    }
    
    @Override
    public boolean isPolicyMappingInhibited() {
        return this.p.isPolicyMappingInhibited();
    }
    
    @Override
    public void setAnyPolicyInhibited(final boolean anyPolicyInhibited) {
        this.p.setAnyPolicyInhibited(anyPolicyInhibited);
    }
    
    @Override
    public boolean isAnyPolicyInhibited() {
        return this.p.isAnyPolicyInhibited();
    }
    
    @Override
    public void setPolicyQualifiersRejected(final boolean policyQualifiersRejected) {
        this.p.setPolicyQualifiersRejected(policyQualifiersRejected);
    }
    
    @Override
    public boolean getPolicyQualifiersRejected() {
        return this.p.getPolicyQualifiersRejected();
    }
    
    @Override
    public Date getDate() {
        return this.p.getDate();
    }
    
    @Override
    public void setCertPathCheckers(final List<PKIXCertPathChecker> certPathCheckers) {
        this.p.setCertPathCheckers(certPathCheckers);
    }
    
    @Override
    public List<PKIXCertPathChecker> getCertPathCheckers() {
        return this.p.getCertPathCheckers();
    }
    
    @Override
    public String getSigProvider() {
        return this.p.getSigProvider();
    }
    
    @Override
    public void setSigProvider(final String sigProvider) {
        this.p.setSigProvider(sigProvider);
    }
    
    @Override
    public CertSelector getTargetCertConstraints() {
        return this.p.getTargetCertConstraints();
    }
    
    @Override
    public void setTargetCertConstraints(final CertSelector targetCertConstraints) {
        if (this.p == null) {
            return;
        }
        this.p.setTargetCertConstraints(targetCertConstraints);
    }
}
