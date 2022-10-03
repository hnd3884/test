package sun.security.provider.certpath;

import sun.security.x509.PolicyMappingsExtension;
import sun.security.x509.CertificatePolicyMap;
import java.security.cert.PolicyNode;
import java.util.Iterator;
import java.util.List;
import sun.security.x509.CertificatePoliciesExtension;
import java.security.cert.CertPath;
import java.security.cert.PKIXReason;
import sun.security.x509.PolicyInformation;
import java.security.cert.PolicyQualifierInfo;
import sun.security.x509.InhibitAnyPolicyExtension;
import sun.security.x509.PolicyConstraintsExtension;
import java.io.IOException;
import java.security.cert.CertificateException;
import sun.security.x509.X509CertImpl;
import java.security.cert.X509Certificate;
import java.security.cert.Certificate;
import java.util.Collections;
import sun.security.x509.PKIXExtensions;
import java.security.cert.CertPathValidatorException;
import java.util.Collection;
import java.util.HashSet;
import sun.security.util.Debug;
import java.util.Set;
import java.security.cert.PKIXCertPathChecker;

class PolicyChecker extends PKIXCertPathChecker
{
    private final Set<String> initPolicies;
    private final int certPathLen;
    private final boolean expPolicyRequired;
    private final boolean polMappingInhibited;
    private final boolean anyPolicyInhibited;
    private final boolean rejectPolicyQualifiers;
    private PolicyNodeImpl rootNode;
    private int explicitPolicy;
    private int policyMapping;
    private int inhibitAnyPolicy;
    private int certIndex;
    private Set<String> supportedExts;
    private static final Debug debug;
    static final String ANY_POLICY = "2.5.29.32.0";
    
    PolicyChecker(final Set<String> set, final int certPathLen, final boolean expPolicyRequired, final boolean polMappingInhibited, final boolean anyPolicyInhibited, final boolean rejectPolicyQualifiers, final PolicyNodeImpl rootNode) {
        if (set.isEmpty()) {
            (this.initPolicies = new HashSet<String>(1)).add("2.5.29.32.0");
        }
        else {
            this.initPolicies = new HashSet<String>(set);
        }
        this.certPathLen = certPathLen;
        this.expPolicyRequired = expPolicyRequired;
        this.polMappingInhibited = polMappingInhibited;
        this.anyPolicyInhibited = anyPolicyInhibited;
        this.rejectPolicyQualifiers = rejectPolicyQualifiers;
        this.rootNode = rootNode;
    }
    
    @Override
    public void init(final boolean b) throws CertPathValidatorException {
        if (b) {
            throw new CertPathValidatorException("forward checking not supported");
        }
        this.certIndex = 1;
        this.explicitPolicy = (this.expPolicyRequired ? 0 : (this.certPathLen + 1));
        this.policyMapping = (this.polMappingInhibited ? 0 : (this.certPathLen + 1));
        this.inhibitAnyPolicy = (this.anyPolicyInhibited ? 0 : (this.certPathLen + 1));
    }
    
    @Override
    public boolean isForwardCheckingSupported() {
        return false;
    }
    
    @Override
    public Set<String> getSupportedExtensions() {
        if (this.supportedExts == null) {
            (this.supportedExts = new HashSet<String>(4)).add(PKIXExtensions.CertificatePolicies_Id.toString());
            this.supportedExts.add(PKIXExtensions.PolicyMappings_Id.toString());
            this.supportedExts.add(PKIXExtensions.PolicyConstraints_Id.toString());
            this.supportedExts.add(PKIXExtensions.InhibitAnyPolicy_Id.toString());
            this.supportedExts = Collections.unmodifiableSet((Set<? extends String>)this.supportedExts);
        }
        return this.supportedExts;
    }
    
    @Override
    public void check(final Certificate certificate, final Collection<String> collection) throws CertPathValidatorException {
        this.checkPolicy((X509Certificate)certificate);
        if (collection != null && !collection.isEmpty()) {
            collection.remove(PKIXExtensions.CertificatePolicies_Id.toString());
            collection.remove(PKIXExtensions.PolicyMappings_Id.toString());
            collection.remove(PKIXExtensions.PolicyConstraints_Id.toString());
            collection.remove(PKIXExtensions.InhibitAnyPolicy_Id.toString());
        }
    }
    
    private void checkPolicy(final X509Certificate x509Certificate) throws CertPathValidatorException {
        final String s = "certificate policies";
        if (PolicyChecker.debug != null) {
            PolicyChecker.debug.println("PolicyChecker.checkPolicy() ---checking " + s + "...");
            PolicyChecker.debug.println("PolicyChecker.checkPolicy() certIndex = " + this.certIndex);
            PolicyChecker.debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: explicitPolicy = " + this.explicitPolicy);
            PolicyChecker.debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: policyMapping = " + this.policyMapping);
            PolicyChecker.debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: inhibitAnyPolicy = " + this.inhibitAnyPolicy);
            PolicyChecker.debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: policyTree = " + this.rootNode);
        }
        X509CertImpl impl;
        try {
            impl = X509CertImpl.toImpl(x509Certificate);
        }
        catch (final CertificateException ex) {
            throw new CertPathValidatorException(ex);
        }
        final boolean b = this.certIndex == this.certPathLen;
        this.rootNode = processPolicies(this.certIndex, this.initPolicies, this.explicitPolicy, this.policyMapping, this.inhibitAnyPolicy, this.rejectPolicyQualifiers, this.rootNode, impl, b);
        if (!b) {
            this.explicitPolicy = mergeExplicitPolicy(this.explicitPolicy, impl, b);
            this.policyMapping = mergePolicyMapping(this.policyMapping, impl);
            this.inhibitAnyPolicy = mergeInhibitAnyPolicy(this.inhibitAnyPolicy, impl);
        }
        ++this.certIndex;
        if (PolicyChecker.debug != null) {
            PolicyChecker.debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: explicitPolicy = " + this.explicitPolicy);
            PolicyChecker.debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: policyMapping = " + this.policyMapping);
            PolicyChecker.debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: inhibitAnyPolicy = " + this.inhibitAnyPolicy);
            PolicyChecker.debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: policyTree = " + this.rootNode);
            PolicyChecker.debug.println("PolicyChecker.checkPolicy() " + s + " verified");
        }
    }
    
    static int mergeExplicitPolicy(int n, final X509CertImpl x509CertImpl, final boolean b) throws CertPathValidatorException {
        if (n > 0 && !X509CertImpl.isSelfIssued(x509CertImpl)) {
            --n;
        }
        try {
            final PolicyConstraintsExtension policyConstraintsExtension = x509CertImpl.getPolicyConstraintsExtension();
            if (policyConstraintsExtension == null) {
                return n;
            }
            final int intValue = policyConstraintsExtension.get("require");
            if (PolicyChecker.debug != null) {
                PolicyChecker.debug.println("PolicyChecker.mergeExplicitPolicy() require Index from cert = " + intValue);
            }
            if (!b) {
                if (intValue != -1 && (n == -1 || intValue < n)) {
                    n = intValue;
                }
            }
            else if (intValue == 0) {
                n = intValue;
            }
        }
        catch (final IOException ex) {
            if (PolicyChecker.debug != null) {
                PolicyChecker.debug.println("PolicyChecker.mergeExplicitPolicy unexpected exception");
                ex.printStackTrace();
            }
            throw new CertPathValidatorException(ex);
        }
        return n;
    }
    
    static int mergePolicyMapping(int n, final X509CertImpl x509CertImpl) throws CertPathValidatorException {
        if (n > 0 && !X509CertImpl.isSelfIssued(x509CertImpl)) {
            --n;
        }
        try {
            final PolicyConstraintsExtension policyConstraintsExtension = x509CertImpl.getPolicyConstraintsExtension();
            if (policyConstraintsExtension == null) {
                return n;
            }
            final int intValue = policyConstraintsExtension.get("inhibit");
            if (PolicyChecker.debug != null) {
                PolicyChecker.debug.println("PolicyChecker.mergePolicyMapping() inhibit Index from cert = " + intValue);
            }
            if (intValue != -1 && (n == -1 || intValue < n)) {
                n = intValue;
            }
        }
        catch (final IOException ex) {
            if (PolicyChecker.debug != null) {
                PolicyChecker.debug.println("PolicyChecker.mergePolicyMapping unexpected exception");
                ex.printStackTrace();
            }
            throw new CertPathValidatorException(ex);
        }
        return n;
    }
    
    static int mergeInhibitAnyPolicy(int n, final X509CertImpl x509CertImpl) throws CertPathValidatorException {
        if (n > 0 && !X509CertImpl.isSelfIssued(x509CertImpl)) {
            --n;
        }
        try {
            final InhibitAnyPolicyExtension inhibitAnyPolicyExtension = (InhibitAnyPolicyExtension)x509CertImpl.getExtension(PKIXExtensions.InhibitAnyPolicy_Id);
            if (inhibitAnyPolicyExtension == null) {
                return n;
            }
            final int intValue = inhibitAnyPolicyExtension.get("skip_certs");
            if (PolicyChecker.debug != null) {
                PolicyChecker.debug.println("PolicyChecker.mergeInhibitAnyPolicy() skipCerts Index from cert = " + intValue);
            }
            if (intValue != -1 && intValue < n) {
                n = intValue;
            }
        }
        catch (final IOException ex) {
            if (PolicyChecker.debug != null) {
                PolicyChecker.debug.println("PolicyChecker.mergeInhibitAnyPolicy unexpected exception");
                ex.printStackTrace();
            }
            throw new CertPathValidatorException(ex);
        }
        return n;
    }
    
    static PolicyNodeImpl processPolicies(final int n, final Set<String> set, int mergeExplicitPolicy, final int n2, final int n3, final boolean b, final PolicyNodeImpl policyNodeImpl, final X509CertImpl x509CertImpl, final boolean b2) throws CertPathValidatorException {
        boolean critical = false;
        Set<PolicyQualifierInfo> policyQualifiers = new HashSet<PolicyQualifierInfo>();
        PolicyNodeImpl policyNodeImpl2;
        if (policyNodeImpl == null) {
            policyNodeImpl2 = null;
        }
        else {
            policyNodeImpl2 = policyNodeImpl.copyTree();
        }
        final CertificatePoliciesExtension certificatePoliciesExtension = x509CertImpl.getCertificatePoliciesExtension();
        if (certificatePoliciesExtension != null && policyNodeImpl2 != null) {
            critical = certificatePoliciesExtension.isCritical();
            if (PolicyChecker.debug != null) {
                PolicyChecker.debug.println("PolicyChecker.processPolicies() policiesCritical = " + critical);
            }
            List<PolicyInformation> value;
            try {
                value = certificatePoliciesExtension.get("policies");
            }
            catch (final IOException ex) {
                throw new CertPathValidatorException("Exception while retrieving policyOIDs", ex);
            }
            if (PolicyChecker.debug != null) {
                PolicyChecker.debug.println("PolicyChecker.processPolicies() rejectPolicyQualifiers = " + b);
            }
            boolean b3 = false;
            for (final PolicyInformation policyInformation : value) {
                final String string = policyInformation.getPolicyIdentifier().getIdentifier().toString();
                if (string.equals("2.5.29.32.0")) {
                    b3 = true;
                    policyQualifiers = policyInformation.getPolicyQualifiers();
                }
                else {
                    if (PolicyChecker.debug != null) {
                        PolicyChecker.debug.println("PolicyChecker.processPolicies() processing policy: " + string);
                    }
                    final Set<PolicyQualifierInfo> policyQualifiers2 = policyInformation.getPolicyQualifiers();
                    if (!policyQualifiers2.isEmpty() && b && critical) {
                        throw new CertPathValidatorException("critical policy qualifiers present in certificate", null, null, -1, PKIXReason.INVALID_POLICY);
                    }
                    if (processParents(n, critical, b, policyNodeImpl2, string, policyQualifiers2, false)) {
                        continue;
                    }
                    processParents(n, critical, b, policyNodeImpl2, string, policyQualifiers2, true);
                }
            }
            if (b3 && (n3 > 0 || (!b2 && X509CertImpl.isSelfIssued(x509CertImpl)))) {
                if (PolicyChecker.debug != null) {
                    PolicyChecker.debug.println("PolicyChecker.processPolicies() processing policy: 2.5.29.32.0");
                }
                processParents(n, critical, b, policyNodeImpl2, "2.5.29.32.0", policyQualifiers, true);
            }
            policyNodeImpl2.prune(n);
            if (!policyNodeImpl2.getChildren().hasNext()) {
                policyNodeImpl2 = null;
            }
        }
        else if (certificatePoliciesExtension == null) {
            if (PolicyChecker.debug != null) {
                PolicyChecker.debug.println("PolicyChecker.processPolicies() no policies present in cert");
            }
            policyNodeImpl2 = null;
        }
        if (policyNodeImpl2 != null && !b2) {
            policyNodeImpl2 = processPolicyMappings(x509CertImpl, n, n2, policyNodeImpl2, critical, policyQualifiers);
        }
        if (policyNodeImpl2 != null && !set.contains("2.5.29.32.0") && certificatePoliciesExtension != null) {
            policyNodeImpl2 = removeInvalidNodes(policyNodeImpl2, n, set, certificatePoliciesExtension);
            if (policyNodeImpl2 != null && b2) {
                policyNodeImpl2 = rewriteLeafNodes(n, set, policyNodeImpl2);
            }
        }
        if (b2) {
            mergeExplicitPolicy = mergeExplicitPolicy(mergeExplicitPolicy, x509CertImpl, b2);
        }
        if (mergeExplicitPolicy == 0 && policyNodeImpl2 == null) {
            throw new CertPathValidatorException("non-null policy tree required and policy tree is null", null, null, -1, PKIXReason.INVALID_POLICY);
        }
        return policyNodeImpl2;
    }
    
    private static PolicyNodeImpl rewriteLeafNodes(final int n, final Set<String> set, PolicyNodeImpl policyNodeImpl) {
        final Set<PolicyNodeImpl> policyNodesValid = policyNodeImpl.getPolicyNodesValid(n, "2.5.29.32.0");
        if (policyNodesValid.isEmpty()) {
            return policyNodeImpl;
        }
        final PolicyNodeImpl policyNodeImpl2 = policyNodesValid.iterator().next();
        final PolicyNodeImpl policyNodeImpl3 = (PolicyNodeImpl)policyNodeImpl2.getParent();
        policyNodeImpl3.deleteChild(policyNodeImpl2);
        final HashSet set2 = new HashSet((Collection<? extends E>)set);
        final Iterator<PolicyNodeImpl> iterator = policyNodeImpl.getPolicyNodes(n).iterator();
        while (iterator.hasNext()) {
            set2.remove(iterator.next().getValidPolicy());
        }
        if (set2.isEmpty()) {
            policyNodeImpl.prune(n);
            if (!policyNodeImpl.getChildren().hasNext()) {
                policyNodeImpl = null;
            }
        }
        else {
            final boolean critical = policyNodeImpl2.isCritical();
            final Set<PolicyQualifierInfo> policyQualifiers = policyNodeImpl2.getPolicyQualifiers();
            for (final String s : set2) {
                final PolicyNodeImpl policyNodeImpl4 = new PolicyNodeImpl(policyNodeImpl3, s, policyQualifiers, critical, Collections.singleton(s), false);
            }
        }
        return policyNodeImpl;
    }
    
    private static boolean processParents(final int n, final boolean b, final boolean b2, final PolicyNodeImpl policyNodeImpl, final String s, final Set<PolicyQualifierInfo> set, final boolean b3) throws CertPathValidatorException {
        boolean b4 = false;
        if (PolicyChecker.debug != null) {
            PolicyChecker.debug.println("PolicyChecker.processParents(): matchAny = " + b3);
        }
        for (final PolicyNodeImpl policyNodeImpl2 : policyNodeImpl.getPolicyNodesExpected(n - 1, s, b3)) {
            if (PolicyChecker.debug != null) {
                PolicyChecker.debug.println("PolicyChecker.processParents() found parent:\n" + policyNodeImpl2.asString());
            }
            b4 = true;
            policyNodeImpl2.getValidPolicy();
            if (s.equals("2.5.29.32.0")) {
            Label_0156:
                for (final String s2 : policyNodeImpl2.getExpectedPolicies()) {
                    final Iterator<PolicyNodeImpl> children = policyNodeImpl2.getChildren();
                    while (children.hasNext()) {
                        final String validPolicy = children.next().getValidPolicy();
                        if (s2.equals(validPolicy)) {
                            if (PolicyChecker.debug != null) {
                                PolicyChecker.debug.println(validPolicy + " in parent's expected policy set already appears in child node");
                                continue Label_0156;
                            }
                            continue Label_0156;
                        }
                    }
                    final HashSet set2 = new HashSet();
                    set2.add(s2);
                    final PolicyNodeImpl policyNodeImpl3 = new PolicyNodeImpl(policyNodeImpl2, s2, set, b, set2, false);
                }
            }
            else {
                final HashSet set3 = new HashSet();
                set3.add(s);
                final PolicyNodeImpl policyNodeImpl4 = new PolicyNodeImpl(policyNodeImpl2, s, set, b, set3, false);
            }
        }
        return b4;
    }
    
    private static PolicyNodeImpl processPolicyMappings(final X509CertImpl x509CertImpl, final int n, final int n2, PolicyNodeImpl policyNodeImpl, final boolean b, final Set<PolicyQualifierInfo> set) throws CertPathValidatorException {
        final PolicyMappingsExtension policyMappingsExtension = x509CertImpl.getPolicyMappingsExtension();
        if (policyMappingsExtension == null) {
            return policyNodeImpl;
        }
        if (PolicyChecker.debug != null) {
            PolicyChecker.debug.println("PolicyChecker.processPolicyMappings() inside policyMapping check");
        }
        List<CertificatePolicyMap> value;
        try {
            value = policyMappingsExtension.get("map");
        }
        catch (final IOException ex) {
            if (PolicyChecker.debug != null) {
                PolicyChecker.debug.println("PolicyChecker.processPolicyMappings() mapping exception");
                ex.printStackTrace();
            }
            throw new CertPathValidatorException("Exception while checking mapping", ex);
        }
        boolean b2 = false;
        for (final CertificatePolicyMap certificatePolicyMap : value) {
            final String string = certificatePolicyMap.getIssuerIdentifier().getIdentifier().toString();
            final String string2 = certificatePolicyMap.getSubjectIdentifier().getIdentifier().toString();
            if (PolicyChecker.debug != null) {
                PolicyChecker.debug.println("PolicyChecker.processPolicyMappings() issuerDomain = " + string);
                PolicyChecker.debug.println("PolicyChecker.processPolicyMappings() subjectDomain = " + string2);
            }
            if (string.equals("2.5.29.32.0")) {
                throw new CertPathValidatorException("encountered an issuerDomainPolicy of ANY_POLICY", null, null, -1, PKIXReason.INVALID_POLICY);
            }
            if (string2.equals("2.5.29.32.0")) {
                throw new CertPathValidatorException("encountered a subjectDomainPolicy of ANY_POLICY", null, null, -1, PKIXReason.INVALID_POLICY);
            }
            final Set<PolicyNodeImpl> policyNodesValid = policyNodeImpl.getPolicyNodesValid(n, string);
            if (!policyNodesValid.isEmpty()) {
                for (final PolicyNodeImpl policyNodeImpl2 : policyNodesValid) {
                    if (n2 > 0 || n2 == -1) {
                        policyNodeImpl2.addExpectedPolicy(string2);
                    }
                    else {
                        if (n2 != 0) {
                            continue;
                        }
                        final PolicyNodeImpl policyNodeImpl3 = (PolicyNodeImpl)policyNodeImpl2.getParent();
                        if (PolicyChecker.debug != null) {
                            PolicyChecker.debug.println("PolicyChecker.processPolicyMappings() before deleting: policy tree = " + policyNodeImpl);
                        }
                        policyNodeImpl3.deleteChild(policyNodeImpl2);
                        b2 = true;
                        if (PolicyChecker.debug == null) {
                            continue;
                        }
                        PolicyChecker.debug.println("PolicyChecker.processPolicyMappings() after deleting: policy tree = " + policyNodeImpl);
                    }
                }
            }
            else {
                if (n2 <= 0 && n2 != -1) {
                    continue;
                }
                final Iterator<PolicyNodeImpl> iterator3 = policyNodeImpl.getPolicyNodesValid(n, "2.5.29.32.0").iterator();
                while (iterator3.hasNext()) {
                    final PolicyNodeImpl policyNodeImpl4 = (PolicyNodeImpl)iterator3.next().getParent();
                    final HashSet set2 = new HashSet();
                    set2.add(string2);
                    final PolicyNodeImpl policyNodeImpl5 = new PolicyNodeImpl(policyNodeImpl4, string, set, b, set2, true);
                }
            }
        }
        if (b2) {
            policyNodeImpl.prune(n);
            if (!policyNodeImpl.getChildren().hasNext()) {
                if (PolicyChecker.debug != null) {
                    PolicyChecker.debug.println("setting rootNode to null");
                }
                policyNodeImpl = null;
            }
        }
        return policyNodeImpl;
    }
    
    private static PolicyNodeImpl removeInvalidNodes(PolicyNodeImpl policyNodeImpl, final int n, final Set<String> set, final CertificatePoliciesExtension certificatePoliciesExtension) throws CertPathValidatorException {
        List<PolicyInformation> value;
        try {
            value = certificatePoliciesExtension.get("policies");
        }
        catch (final IOException ex) {
            throw new CertPathValidatorException("Exception while retrieving policyOIDs", ex);
        }
        boolean b = false;
        final Iterator<PolicyInformation> iterator = value.iterator();
        while (iterator.hasNext()) {
            final String string = iterator.next().getPolicyIdentifier().getIdentifier().toString();
            if (PolicyChecker.debug != null) {
                PolicyChecker.debug.println("PolicyChecker.processPolicies() processing policy second time: " + string);
            }
            for (final PolicyNodeImpl policyNodeImpl2 : policyNodeImpl.getPolicyNodesValid(n, string)) {
                final PolicyNodeImpl policyNodeImpl3 = (PolicyNodeImpl)policyNodeImpl2.getParent();
                if (policyNodeImpl3.getValidPolicy().equals("2.5.29.32.0") && !set.contains(string) && !string.equals("2.5.29.32.0")) {
                    if (PolicyChecker.debug != null) {
                        PolicyChecker.debug.println("PolicyChecker.processPolicies() before deleting: policy tree = " + policyNodeImpl);
                    }
                    policyNodeImpl3.deleteChild(policyNodeImpl2);
                    b = true;
                    if (PolicyChecker.debug == null) {
                        continue;
                    }
                    PolicyChecker.debug.println("PolicyChecker.processPolicies() after deleting: policy tree = " + policyNodeImpl);
                }
            }
        }
        if (b) {
            policyNodeImpl.prune(n);
            if (!policyNodeImpl.getChildren().hasNext()) {
                policyNodeImpl = null;
            }
        }
        return policyNodeImpl;
    }
    
    PolicyNode getPolicyTree() {
        if (this.rootNode == null) {
            return null;
        }
        final PolicyNodeImpl copyTree = this.rootNode.copyTree();
        copyTree.setImmutable();
        return copyTree;
    }
    
    static {
        debug = Debug.getInstance("certpath");
    }
}
