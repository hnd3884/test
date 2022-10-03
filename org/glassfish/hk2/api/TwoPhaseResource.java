package org.glassfish.hk2.api;

public interface TwoPhaseResource
{
    void prepareDynamicConfiguration(final TwoPhaseTransactionData p0) throws MultiException;
    
    void activateDynamicConfiguration(final TwoPhaseTransactionData p0);
    
    void rollbackDynamicConfiguration(final TwoPhaseTransactionData p0);
}
