package com.sun.corba.se.spi.extension;

import org.omg.CORBA.Policy;
import org.omg.CORBA.LocalObject;

public class ServantCachingPolicy extends LocalObject implements Policy
{
    public static final int NO_SERVANT_CACHING = 0;
    public static final int FULL_SEMANTICS = 1;
    public static final int INFO_ONLY_SEMANTICS = 2;
    public static final int MINIMAL_SEMANTICS = 3;
    private static ServantCachingPolicy policy;
    private static ServantCachingPolicy infoOnlyPolicy;
    private static ServantCachingPolicy minimalPolicy;
    private int type;
    
    public String typeToName() {
        switch (this.type) {
            case 1: {
                return "FULL";
            }
            case 2: {
                return "INFO_ONLY";
            }
            case 3: {
                return "MINIMAL";
            }
            default: {
                return "UNKNOWN(" + this.type + ")";
            }
        }
    }
    
    @Override
    public String toString() {
        return "ServantCachingPolicy[" + this.typeToName() + "]";
    }
    
    private ServantCachingPolicy(final int type) {
        this.type = type;
    }
    
    public int getType() {
        return this.type;
    }
    
    public static synchronized ServantCachingPolicy getPolicy() {
        return getFullPolicy();
    }
    
    public static synchronized ServantCachingPolicy getFullPolicy() {
        if (ServantCachingPolicy.policy == null) {
            ServantCachingPolicy.policy = new ServantCachingPolicy(1);
        }
        return ServantCachingPolicy.policy;
    }
    
    public static synchronized ServantCachingPolicy getInfoOnlyPolicy() {
        if (ServantCachingPolicy.infoOnlyPolicy == null) {
            ServantCachingPolicy.infoOnlyPolicy = new ServantCachingPolicy(2);
        }
        return ServantCachingPolicy.infoOnlyPolicy;
    }
    
    public static synchronized ServantCachingPolicy getMinimalPolicy() {
        if (ServantCachingPolicy.minimalPolicy == null) {
            ServantCachingPolicy.minimalPolicy = new ServantCachingPolicy(3);
        }
        return ServantCachingPolicy.minimalPolicy;
    }
    
    @Override
    public int policy_type() {
        return 1398079488;
    }
    
    @Override
    public Policy copy() {
        return this;
    }
    
    @Override
    public void destroy() {
    }
    
    static {
        ServantCachingPolicy.policy = null;
        ServantCachingPolicy.infoOnlyPolicy = null;
        ServantCachingPolicy.minimalPolicy = null;
    }
}
