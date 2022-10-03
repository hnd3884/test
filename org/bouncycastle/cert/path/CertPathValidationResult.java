package org.bouncycastle.cert.path;

import org.bouncycastle.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class CertPathValidationResult
{
    private final boolean isValid;
    private final CertPathValidationException cause;
    private final Set unhandledCriticalExtensionOIDs;
    private final int certIndex;
    private final int ruleIndex;
    private CertPathValidationException[] causes;
    private int[] certIndexes;
    private int[] ruleIndexes;
    
    public CertPathValidationResult(final CertPathValidationContext certPathValidationContext) {
        this.unhandledCriticalExtensionOIDs = Collections.unmodifiableSet((Set<?>)certPathValidationContext.getUnhandledCriticalExtensionOIDs());
        this.isValid = this.unhandledCriticalExtensionOIDs.isEmpty();
        this.certIndex = -1;
        this.ruleIndex = -1;
        this.cause = null;
    }
    
    public CertPathValidationResult(final CertPathValidationContext certPathValidationContext, final int certIndex, final int ruleIndex, final CertPathValidationException cause) {
        this.unhandledCriticalExtensionOIDs = Collections.unmodifiableSet((Set<?>)certPathValidationContext.getUnhandledCriticalExtensionOIDs());
        this.isValid = false;
        this.certIndex = certIndex;
        this.ruleIndex = ruleIndex;
        this.cause = cause;
    }
    
    public CertPathValidationResult(final CertPathValidationContext certPathValidationContext, final int[] certIndexes, final int[] ruleIndexes, final CertPathValidationException[] causes) {
        this.unhandledCriticalExtensionOIDs = Collections.unmodifiableSet((Set<?>)certPathValidationContext.getUnhandledCriticalExtensionOIDs());
        this.isValid = false;
        this.cause = causes[0];
        this.certIndex = certIndexes[0];
        this.ruleIndex = ruleIndexes[0];
        this.causes = causes;
        this.certIndexes = certIndexes;
        this.ruleIndexes = ruleIndexes;
    }
    
    public boolean isValid() {
        return this.isValid;
    }
    
    public CertPathValidationException getCause() {
        if (this.cause != null) {
            return this.cause;
        }
        if (!this.unhandledCriticalExtensionOIDs.isEmpty()) {
            return new CertPathValidationException("Unhandled Critical Extensions");
        }
        return null;
    }
    
    public int getFailingCertIndex() {
        return this.certIndex;
    }
    
    public int getFailingRuleIndex() {
        return this.ruleIndex;
    }
    
    public Set getUnhandledCriticalExtensionOIDs() {
        return this.unhandledCriticalExtensionOIDs;
    }
    
    public boolean isDetailed() {
        return this.certIndexes != null;
    }
    
    public CertPathValidationException[] getCauses() {
        if (this.causes != null) {
            final CertPathValidationException[] array = new CertPathValidationException[this.causes.length];
            System.arraycopy(this.causes, 0, array, 0, this.causes.length);
            return array;
        }
        if (!this.unhandledCriticalExtensionOIDs.isEmpty()) {
            return new CertPathValidationException[] { new CertPathValidationException("Unhandled Critical Extensions") };
        }
        return null;
    }
    
    public int[] getFailingCertIndexes() {
        return Arrays.clone(this.certIndexes);
    }
    
    public int[] getFailingRuleIndexes() {
        return Arrays.clone(this.ruleIndexes);
    }
}
