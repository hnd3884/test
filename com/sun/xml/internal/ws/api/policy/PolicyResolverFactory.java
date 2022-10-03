package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.policy.jaxws.DefaultPolicyResolver;
import java.util.Iterator;
import com.sun.xml.internal.ws.util.ServiceFinder;

public abstract class PolicyResolverFactory
{
    public static final PolicyResolver DEFAULT_POLICY_RESOLVER;
    
    public abstract PolicyResolver doCreate();
    
    public static PolicyResolver create() {
        for (final PolicyResolverFactory factory : ServiceFinder.find(PolicyResolverFactory.class)) {
            final PolicyResolver policyResolver = factory.doCreate();
            if (policyResolver != null) {
                return policyResolver;
            }
        }
        return PolicyResolverFactory.DEFAULT_POLICY_RESOLVER;
    }
    
    static {
        DEFAULT_POLICY_RESOLVER = new DefaultPolicyResolver();
    }
}
