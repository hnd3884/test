package com.zoho.security.wafad.instrument.pathtraversal;

import javax.servlet.DispatcherType;

public final class ForwardRequestDispatcherAttackDiscovery extends ReqDispatcherPathTraversalAttackDiscovery
{
    public ForwardRequestDispatcherAttackDiscovery() {
        super(DispatcherType.FORWARD);
    }
    
    @Override
    public boolean isInvalidCalleeClass(final String className) {
        return className.equals("org.apache.jasper.runtime.PageContextImpl") || super.isInvalidCalleeClass(className);
    }
}
