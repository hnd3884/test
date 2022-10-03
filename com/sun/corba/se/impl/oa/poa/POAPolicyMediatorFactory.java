package com.sun.corba.se.impl.oa.poa;

abstract class POAPolicyMediatorFactory
{
    static POAPolicyMediator create(final Policies policies, final POAImpl poaImpl) {
        if (policies.retainServants()) {
            if (policies.useActiveMapOnly()) {
                return new POAPolicyMediatorImpl_R_AOM(policies, poaImpl);
            }
            if (policies.useDefaultServant()) {
                return new POAPolicyMediatorImpl_R_UDS(policies, poaImpl);
            }
            if (policies.useServantManager()) {
                return new POAPolicyMediatorImpl_R_USM(policies, poaImpl);
            }
            throw poaImpl.invocationWrapper().pmfCreateRetain();
        }
        else {
            if (policies.useDefaultServant()) {
                return new POAPolicyMediatorImpl_NR_UDS(policies, poaImpl);
            }
            if (policies.useServantManager()) {
                return new POAPolicyMediatorImpl_NR_USM(policies, poaImpl);
            }
            throw poaImpl.invocationWrapper().pmfCreateNonRetain();
        }
    }
}
