package com.sun.xml.internal.ws.policy.spi;

import com.sun.xml.internal.ws.policy.PolicyAssertion;

public interface PolicyAssertionValidator
{
    Fitness validateClientSide(final PolicyAssertion p0);
    
    Fitness validateServerSide(final PolicyAssertion p0);
    
    String[] declareSupportedDomains();
    
    public enum Fitness
    {
        UNKNOWN, 
        INVALID, 
        UNSUPPORTED, 
        SUPPORTED;
        
        public Fitness combine(final Fitness other) {
            if (this.compareTo(other) < 0) {
                return other;
            }
            return this;
        }
    }
}
