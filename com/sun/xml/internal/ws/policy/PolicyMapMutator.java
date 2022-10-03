package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public abstract class PolicyMapMutator
{
    private static final PolicyLogger LOGGER;
    private PolicyMap map;
    
    PolicyMapMutator() {
        this.map = null;
    }
    
    public void connect(final PolicyMap map) {
        if (this.isConnected()) {
            throw PolicyMapMutator.LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0044_POLICY_MAP_MUTATOR_ALREADY_CONNECTED()));
        }
        this.map = map;
    }
    
    public PolicyMap getMap() {
        return this.map;
    }
    
    public void disconnect() {
        this.map = null;
    }
    
    public boolean isConnected() {
        return this.map != null;
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(PolicyMapMutator.class);
    }
}
