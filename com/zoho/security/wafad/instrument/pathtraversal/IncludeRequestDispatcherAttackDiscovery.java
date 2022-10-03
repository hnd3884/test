package com.zoho.security.wafad.instrument.pathtraversal;

import javax.servlet.DispatcherType;

public final class IncludeRequestDispatcherAttackDiscovery extends ReqDispatcherPathTraversalAttackDiscovery
{
    public IncludeRequestDispatcherAttackDiscovery() {
        super(DispatcherType.INCLUDE);
    }
    
    @Override
    public boolean isInvalidCalleeClass(final String className) {
        return className.equals("org.apache.jasper.runtime.JspRuntimeLibrary") || super.isInvalidCalleeClass(className);
    }
}
