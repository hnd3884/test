package com.sun.xml.internal.ws.policy;

import java.util.Iterator;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public final class PolicyIntersector
{
    private static final PolicyIntersector STRICT_INTERSECTOR;
    private static final PolicyIntersector LAX_INTERSECTOR;
    private static final PolicyLogger LOGGER;
    private CompatibilityMode mode;
    
    private PolicyIntersector(final CompatibilityMode intersectionMode) {
        this.mode = intersectionMode;
    }
    
    public static PolicyIntersector createStrictPolicyIntersector() {
        return PolicyIntersector.STRICT_INTERSECTOR;
    }
    
    public static PolicyIntersector createLaxPolicyIntersector() {
        return PolicyIntersector.LAX_INTERSECTOR;
    }
    
    public Policy intersect(final Policy... policies) {
        if (policies == null || policies.length == 0) {
            throw PolicyIntersector.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0056_NEITHER_NULL_NOR_EMPTY_POLICY_COLLECTION_EXPECTED()));
        }
        if (policies.length == 1) {
            return policies[0];
        }
        boolean found = false;
        boolean allPoliciesEmpty = true;
        NamespaceVersion latestVersion = null;
        for (final Policy tested : policies) {
            if (tested.isEmpty()) {
                found = true;
            }
            else {
                if (tested.isNull()) {
                    found = true;
                }
                allPoliciesEmpty = false;
            }
            if (latestVersion == null) {
                latestVersion = tested.getNamespaceVersion();
            }
            else if (latestVersion.compareTo(tested.getNamespaceVersion()) < 0) {
                latestVersion = tested.getNamespaceVersion();
            }
            if (found && !allPoliciesEmpty) {
                return Policy.createNullPolicy(latestVersion, null, null);
            }
        }
        latestVersion = ((latestVersion != null) ? latestVersion : NamespaceVersion.getLatestVersion());
        if (allPoliciesEmpty) {
            return Policy.createEmptyPolicy(latestVersion, null, null);
        }
        final List<AssertionSet> finalAlternatives = new LinkedList<AssertionSet>(policies[0].getContent());
        final Queue<AssertionSet> testedAlternatives = new LinkedList<AssertionSet>();
        final List<AssertionSet> alternativesToMerge = new ArrayList<AssertionSet>(2);
        for (int i = 1; i < policies.length; ++i) {
            final Collection<AssertionSet> currentAlternatives = policies[i].getContent();
            testedAlternatives.clear();
            testedAlternatives.addAll((Collection<?>)finalAlternatives);
            finalAlternatives.clear();
            AssertionSet testedAlternative;
            while ((testedAlternative = testedAlternatives.poll()) != null) {
                for (final AssertionSet currentAlternative : currentAlternatives) {
                    if (testedAlternative.isCompatibleWith(currentAlternative, this.mode)) {
                        alternativesToMerge.add(testedAlternative);
                        alternativesToMerge.add(currentAlternative);
                        finalAlternatives.add(AssertionSet.createMergedAssertionSet(alternativesToMerge));
                        alternativesToMerge.clear();
                    }
                }
            }
        }
        return Policy.createPolicy(latestVersion, null, null, finalAlternatives);
    }
    
    static {
        STRICT_INTERSECTOR = new PolicyIntersector(CompatibilityMode.STRICT);
        LAX_INTERSECTOR = new PolicyIntersector(CompatibilityMode.LAX);
        LOGGER = PolicyLogger.getLogger(PolicyIntersector.class);
    }
    
    enum CompatibilityMode
    {
        STRICT, 
        LAX;
    }
}
