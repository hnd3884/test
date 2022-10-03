package com.sun.xml.internal.ws.policy;

import java.util.Iterator;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import java.util.ArrayList;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.util.LinkedList;
import java.util.Collection;

public final class PolicyMerger
{
    private static final PolicyMerger merger;
    
    private PolicyMerger() {
    }
    
    public static PolicyMerger getMerger() {
        return PolicyMerger.merger;
    }
    
    public Policy merge(final Collection<Policy> policies) {
        if (policies == null || policies.isEmpty()) {
            return null;
        }
        if (policies.size() == 1) {
            return policies.iterator().next();
        }
        final Collection<Collection<AssertionSet>> alternativeSets = new LinkedList<Collection<AssertionSet>>();
        final StringBuilder id = new StringBuilder();
        NamespaceVersion mergedVersion = policies.iterator().next().getNamespaceVersion();
        for (final Policy policy : policies) {
            alternativeSets.add(policy.getContent());
            if (mergedVersion.compareTo(policy.getNamespaceVersion()) < 0) {
                mergedVersion = policy.getNamespaceVersion();
            }
            final String policyId = policy.getId();
            if (policyId != null) {
                if (id.length() > 0) {
                    id.append('-');
                }
                id.append(policyId);
            }
        }
        final Collection<Collection<AssertionSet>> combinedAlternatives = PolicyUtils.Collections.combine((Collection)null, alternativeSets, false);
        if (combinedAlternatives == null || combinedAlternatives.isEmpty()) {
            return Policy.createNullPolicy(mergedVersion, null, (id.length() == 0) ? null : id.toString());
        }
        final Collection<AssertionSet> mergedSetList = new ArrayList<AssertionSet>(combinedAlternatives.size());
        for (final Collection<AssertionSet> toBeMerged : combinedAlternatives) {
            mergedSetList.add(AssertionSet.createMergedAssertionSet(toBeMerged));
        }
        return Policy.createPolicy(mergedVersion, null, (id.length() == 0) ? null : id.toString(), mergedSetList);
    }
    
    static {
        merger = new PolicyMerger();
    }
}
