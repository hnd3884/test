package com.sun.xml.internal.ws.policy.jaxws;

import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import com.sun.xml.internal.ws.policy.PolicyMapExtender;
import java.util.HashSet;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapMutator;
import java.util.LinkedList;
import java.util.List;

class PolicyMapBuilder
{
    private List<BuilderHandler> policyBuilders;
    
    PolicyMapBuilder() {
        this.policyBuilders = new LinkedList<BuilderHandler>();
    }
    
    void registerHandler(final BuilderHandler builder) {
        if (null != builder) {
            this.policyBuilders.add(builder);
        }
    }
    
    PolicyMap getPolicyMap(final PolicyMapMutator... externalMutators) throws PolicyException {
        return this.getNewPolicyMap(externalMutators);
    }
    
    private PolicyMap getNewPolicyMap(final PolicyMapMutator... externalMutators) throws PolicyException {
        final HashSet<PolicyMapMutator> mutators = new HashSet<PolicyMapMutator>();
        final PolicyMapExtender myExtender = PolicyMapExtender.createPolicyMapExtender();
        mutators.add(myExtender);
        if (null != externalMutators) {
            mutators.addAll((Collection<?>)Arrays.asList(externalMutators));
        }
        final PolicyMap policyMap = PolicyMap.createPolicyMap(mutators);
        for (final BuilderHandler builder : this.policyBuilders) {
            builder.populate(myExtender);
        }
        return policyMap;
    }
    
    void unregisterAll() {
        this.policyBuilders = null;
    }
}
